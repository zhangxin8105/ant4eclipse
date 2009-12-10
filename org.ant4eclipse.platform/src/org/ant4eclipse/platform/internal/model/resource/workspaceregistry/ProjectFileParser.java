/**********************************************************************
 * Copyright (c) 2005-2009 ant4eclipse project team.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nils Hartmann, Daniel Kasmeroglu, Gerd Wuetherich
 **********************************************************************/
package org.ant4eclipse.platform.internal.model.resource.workspaceregistry;

import org.ant4eclipse.core.Assert;
import org.ant4eclipse.core.service.ServiceRegistry;
import org.ant4eclipse.core.util.Utilities;
import org.ant4eclipse.core.xquery.XQuery;
import org.ant4eclipse.core.xquery.XQueryHandler;

import org.ant4eclipse.platform.internal.model.resource.BuildCommandImpl;
import org.ant4eclipse.platform.internal.model.resource.EclipseProjectImpl;
import org.ant4eclipse.platform.internal.model.resource.LinkedResourceImpl;
import org.ant4eclipse.platform.internal.model.resource.ProjectNatureImpl;
import org.ant4eclipse.platform.model.resource.EclipseProject;
import org.ant4eclipse.platform.model.resource.variable.EclipseVariableResolver;

import java.io.File;
import java.util.StringTokenizer;

/**
 * <p>
 * Parser that reads an eclipse <code>.project</code> file and creates an
 * {@link org.ant4eclipse.platform.model.resource.EclipseProject EclipseProject}.
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class ProjectFileParser {

  /**
   * <p>
   * Parses the '<code>.project</code>' file of the given eclipse project.
   * </p>
   * 
   * @param eclipseProject
   * 
   * @return the supplied {@link EclipseProject} instance.
   */
  public static EclipseProjectImpl parseProject(EclipseProjectImpl eclipseProject) {
    Assert.notNull(eclipseProject);

    // retrieve the '.project' file
    File projectFile = eclipseProject.getChild(".project");

    XQueryHandler queryhandler2 = new XQueryHandler(projectFile.getAbsolutePath());

    // create Queries
    XQuery projectNameQuery = queryhandler2.createQuery("/projectDescription/name");
    XQuery commentQuery = queryhandler2.createQuery("/projectDescription/comment");
    XQuery referencedProjectQuery = queryhandler2.createQuery("/projectDescription/projects/project");
    XQuery natureQuery = queryhandler2.createQuery("/projectDescription/natures/nature");
    XQuery buildCommandNameQuery = queryhandler2.createQuery("/projectDescription/buildSpec/{buildCommand}/name");
    XQuery linkedResourceNameQuery = queryhandler2.createQuery("/projectDescription/linkedResources/{link}/name");
    XQuery linkedResourceTypeQuery = queryhandler2.createQuery("/projectDescription/linkedResources/{link}/type");
    XQuery linkedResourceLocationQuery = queryhandler2
        .createQuery("/projectDescription/linkedResources/{link}/location");
    XQuery linkedResourceLocationURIQuery = queryhandler2
        .createQuery("/projectDescription/linkedResources/{link}/locationURI");

    XQueryHandler.queryFile(projectFile, queryhandler2);

    String projectName = projectNameQuery.getSingleResult();
    String comment = commentQuery.getSingleResult();
    String[] referencedProjects = referencedProjectQuery.getResult();
    String[] natures = natureQuery.getResult();
    String[] buildCommandNames = buildCommandNameQuery.getResult();
    String[] linkedResourceNames = linkedResourceNameQuery.getResult();
    String[] linkedResourceTypes = linkedResourceTypeQuery.getResult();
    String[] linkedResourceLocations = linkedResourceLocationQuery.getResult();
    String[] linkedResourceLocationURIs = linkedResourceLocationURIQuery.getResult();

    // set specified name
    eclipseProject.setSpecifiedName(projectName);

    // set comment
    eclipseProject.setComment(comment);

    // set referenced projects
    for (String referencedProject : referencedProjects) {
      eclipseProject.addReferencedProject(referencedProject);
    }

    // set project natures
    for (String nature : natures) {
      eclipseProject.addNature(new ProjectNatureImpl(nature));
    }

    // set build commands
    for (String buildCommandName : buildCommandNames) {
      eclipseProject.addBuildCommand(new BuildCommandImpl(buildCommandName));
    }

    // set linked resources
    for (int i = 0; i < linkedResourceNames.length; i++) {

      // retrieve location and locationURI
      String locationuri = linkedResourceLocationURIs[i];
      String location = linkedResourceLocations[i];

      // 
      if (locationuri != null) {
        location = resolveLocation(eclipseProject, locationuri);
        if (location == null) {
          // resolving the variable failed for some reason
          // TODO!!
          throw (new RuntimeException("couldn't resolve variable '" + locationuri + "'"));
        }
      } else {
        // this is needed since variable names are stored under the <location> element
        // in eclipse versions before 3.2. this is some sort of guessing since
        String newlocation = location;
        int first = newlocation.indexOf('/');
        if (first != -1) {
          // only the part until the first slash can refer to a variable name
          newlocation = newlocation.substring(0, first);
        }
        newlocation = resolveLocation(eclipseProject, newlocation);
        if (newlocation != null) {
          File test = new File(newlocation);
          if (test.exists()) {
            if (first != -1) {
              // the content has been cut down, so we need to add the relative path here
              location = newlocation + location.substring(first);
            } else {
              location = newlocation;
            }
          }
        }
      }
      String relative = Utilities.calcRelative(eclipseProject.getFolder(), new File(location));
      int typeAsInt = Integer.parseInt(linkedResourceTypes[i]);
      LinkedResourceImpl linkedResource = new LinkedResourceImpl(linkedResourceNames[i], location, relative, typeAsInt);
      eclipseProject.addLinkedResource(linkedResource);
    }

    return eclipseProject;
  }

  /*
   * TODO:
   * 
   * Mostly unlikely, some weird path may be presented, so checking for extraneous separators may be worthwhile in the
   * long run.
   */
  private static final String resolveLocation(EclipseProjectImpl p, String path) {
    if (path == null) {
      return null;
    }
    String S = "/";
    StringTokenizer t = new StringTokenizer(path, S);
    StringBuffer b = new StringBuffer(path.length() * 3);
    while (t.hasMoreElements()) {
      String segment = (String) t.nextElement();
      if (segment == null) {
        return null;
      }
      String resolved = getLocation(p, segment);
      if (resolved != null) {
        b.append(resolved);
      } else {
        b.append(segment);
      }
      if (t.hasMoreElements()) {
        b.append(S);
      }
    }
    return b.toString();
  }

  /**
   * Returns the location while expanding the supplied variable.
   * 
   * @param eclipseProject
   *          The project that will be used for the variable expansion.
   * @param variable
   *          The name of the variable.
   * 
   * @return The expanded variable or null in case no expansion happened.
   */
  private static final String getLocation(EclipseProjectImpl eclipseProject, String variable) {
    String key = "${" + variable + "}";

    String location = getEclipseVariableResolver().resolveEclipseVariables(key, eclipseProject, null);
    if (key.equals(location)) {
      // fallback for the internal prefs of an eclipse .metadata dir
      key = "${pathvariable." + variable + "}";
      location = getEclipseVariableResolver().resolveEclipseVariables(key, eclipseProject, null);
      if (key.equals(location)) {
        // the result is the key itself, so resolving failed
        location = null;
      }
    }
    return (location);
  }

  private static EclipseVariableResolver getEclipseVariableResolver() {
    EclipseVariableResolver resolver = (EclipseVariableResolver) ServiceRegistry.instance().getService(
        EclipseVariableResolver.class.getName());
    return resolver;
  }

} /* ENDCLASS */