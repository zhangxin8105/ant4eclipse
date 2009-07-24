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
package org.ant4eclipse.cdt.ant;

import org.ant4eclipse.platform.ant.core.task.AbstractGetProjectPathTask;

import org.ant4eclipse.cdt.internal.tools.CdtUtilities;

import org.apache.tools.ant.BuildException;

import java.io.File;

/**
 * 
 * @author Daniel Kasmeroglu (Daniel.Kasmeroglu@Kasisoft.net)
 */
//@com.kasisoft.lgpl.tools.diagnostic.KDiagnostic(loggername="cdt")
public class GetCdtSourcePathTask extends AbstractGetProjectPathTask {

  /**
   * {@inheritDoc}
   */
  protected void preconditions() throws BuildException {
    super.preconditions();
    if (!CdtUtilities.isCRelatedProject(getEclipseProject())) {
      throw new BuildException(String.format("The project '%s' must have the c or c++ project role!", getEclipseProject()
          .getSpecifiedName()));
    }
  }

  /**
   * {@inheritDoc}
   */
  protected File[] resolvePath() {
    return new File[0];
  }

} /* ENDCLASS */
