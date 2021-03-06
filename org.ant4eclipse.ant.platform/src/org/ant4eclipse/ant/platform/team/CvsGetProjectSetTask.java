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
package org.ant4eclipse.ant.platform.team;


import org.ant4eclipse.ant.platform.internal.team.CvsAdapter;
import org.ant4eclipse.ant.platform.internal.team.VcsAdapter;
import org.apache.tools.ant.BuildException;

/**
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class CvsGetProjectSetTask extends AbstractGetProjectSetTask {

  private boolean _cvsQuiet       = false;

  private boolean _cvsReallyQuiet = false;

  private String  _tag            = null;

  public boolean isCvsQuiet() {
    return this._cvsQuiet;
  }

  public void setCvsQuiet(boolean cvsQuiet) {
    this._cvsQuiet = cvsQuiet;
  }

  public boolean isCvsReallyQuiet() {
    return this._cvsReallyQuiet;
  }

  public void setCvsReallyQuiet(boolean cvsReallyQuiet) {
    this._cvsReallyQuiet = cvsReallyQuiet;
  }

  /**
   * @return Returns the cvsPwd.
   */
  public String getCvsPwd() {
    return getPassword();
  }

  /**
   * @param cvsPwd
   *          The cvsPwd to set.
   */
  public void setCvsPwd(String cvsPwd) {
    setPassword(cvsPwd);
  }

  /**
   * @return Returns the cvsUser.
   */
  public String getCvsUser() {
    return getUsername();
  }

  public String getTag() {
    return this._tag;
  }

  public void setTag(String tag) {
    this._tag = tag;
  }

  /**
   * @param cvsUser
   *          The cvsUser to set.
   */
  public void setCvsUser(String cvsUser) {
    setUsername(cvsUser);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected VcsAdapter createVcsAdapter() {
    // set property
    System.setProperty("javacvs.multiple_commands_warning", "false");
    return new CvsAdapter(getProject(), isCvsQuiet(), isCvsReallyQuiet(), getTag());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void checkPrereqs() {
    requiresCvsUserSet();
  }

  private void requiresCvsUserSet() {
    // check that cvsuser is set..
    if (getUsername() == null) {
      throw new BuildException("cvsuser has to be set!");
    }
  }

}