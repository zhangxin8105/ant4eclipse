/**********************************************************************
 * Copyright (c) 2005-2008 ant4eclipse project team.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nils Hartmann, Daniel Kasmeroglu, Gerd Wuetherich
 **********************************************************************/
package org.ant4eclipse.pde.internal.tools;

import org.ant4eclipse.core.Assert;

import org.ant4eclipse.pde.model.featureproject.FeatureProjectRole;
import org.ant4eclipse.pde.model.pluginproject.PluginProjectRole;

import org.ant4eclipse.platform.model.resource.EclipseProject;
import org.ant4eclipse.platform.model.resource.Workspace;

/**
 * <p>
 * A {@link BundleAndFeatureSet} implementation that represent the plug-ins and features that are contained in the
 * workspace as eclipse projects.
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 * @author Nils Hartmann (nils@nilshartmann.net)
 */
public final class PluginAndFeatureProjectSet extends AbstractBundleAndFeatureSet {

  /** the workspace which contains the projects to build */
  private final Workspace _workspace;

  /**
   * <p>
   * Creates a new instance of type TargetPlatform for a specific workspace.
   * </p>
   * 
   * @param workspace
   *          the {@link Workspace}
   */
  public PluginAndFeatureProjectSet(final Workspace workspace) {
    Assert.notNull(workspace);

    this._workspace = workspace;
  }

  /**
   * {@inheritDoc}
   */
  protected void readBundlesAndFeatures() {

    // get all projects...
    final EclipseProject[] eclipseProjects = this._workspace.getAllProjects();

    // add all plug-in projects
    for (EclipseProject eclipseProject : eclipseProjects) {

      // add plug-in projects
      if (PluginProjectRole.Helper.hasPluginProjectRole(eclipseProject)) {
        addBundleDescription(PluginProjectRole.Helper.getPluginProjectRole(eclipseProject).getBundleDescription());
      }
      // add feature projects
      else if (FeatureProjectRole.Helper.hasFeatureProjectRole(eclipseProject)) {
        FeatureProjectRole featureProjectRole = FeatureProjectRole.Helper.getFeatureProjectRole(eclipseProject);
        addFeaturesDescription(new FeatureDescription(eclipseProject, featureProjectRole.getFeatureManifest()));
      }
    }
  }
}