/**********************************************************************
 * Copyright (c) 2005-2006 ant4eclipse project team.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nils Hartmann, Daniel Kasmeroglu, Gerd Wuetherich
 **********************************************************************/
package org.ant4eclipse.platform.test.builder;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ant4eclipse.core.Assert;

/**
 * Creates Eclipse projects for test purposes.
 * 
 * @author Nils Hartmann (nils@nilshartmann.net)
 */
public class EclipseProjectBuilder {

  public final static String NL = System.getProperty("line.separator");

  private final String       _projectName;

  private final List<String> _natures;

  private final List<String> _builders;

  public EclipseProjectBuilder(final String projectName) {
    assertNotNull(projectName);
    this._projectName = projectName;
    this._natures = new LinkedList<String>();
    this._builders = new LinkedList<String>();

  }

  // /**
  // * TODO move to PDE layer
  // *
  // * @return
  // */
  // public EclipseProjectBuilder withRequiredPluginsContainer() {
  // _requiredPluginsContainer = true;
  // return this;
  // }

  public EclipseProjectBuilder withNature(String natureId) {
    assertNotNull(natureId);
    this._natures.add(natureId);
    return this;
  }

  //
  // public EclipseProjectCreator withPluginNature() {
  // return withJavaNature().withNature(PluginProjectRole.PLUGIN_NATURE);
  // }
  //
  public EclipseProjectBuilder withBuilder(String buildCmd) {
    assertNotNull(buildCmd);

    this._builders.add(buildCmd);
    return this;
  }

  /**
   * Creates this project
   * 
   * @param destinationDirectory
   *          the directory where this project(directory) should be created to
   * @return The project directory
   */
  public File createIn(File destinationDirectory) {
    Assert.isDirectory(destinationDirectory);
    final File projectDir = new File(destinationDirectory, this._projectName);
    FileHelper.createDirectory(projectDir);

    createArtefacts(projectDir);
    return projectDir;
  }

  protected void createArtefacts(File projectDir) {
    createProjectFile(projectDir);
  }

  protected void createProjectFile(File projectDir) {
    final StringBuffer dotProject = new StringBuffer();
    dotProject.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(NL).append("<projectDescription><name>")
        .append(this._projectName).append("</name>").append("<comment/><projects/>").append(NL).append("<buildSpec>")
        .append(NL);
    Iterator<String> it = this._builders.iterator();
    while (it.hasNext()) {
      final String builder = it.next();
      dotProject.append("<buildCommand><name>").append(builder).append("</name><arguments/></buildCommand>").append(NL);
    }
    dotProject.append("</buildSpec>").append(NL);

    it = this._natures.iterator();
    dotProject.append("<natures>").append(NL);
    while (it.hasNext()) {
      final String builder = it.next();
      dotProject.append("<nature>").append(builder).append("</nature>").append(NL);
    }
    dotProject.append("</natures>").append(NL);
    dotProject.append("</projectDescription>").append(NL);

    File dotProjectFile = new File(projectDir, ".project");
    FileHelper.createFile(dotProjectFile, dotProject.toString());
  }
}