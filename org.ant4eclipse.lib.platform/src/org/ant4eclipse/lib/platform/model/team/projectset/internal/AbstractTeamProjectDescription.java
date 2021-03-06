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
package org.ant4eclipse.lib.platform.model.team.projectset.internal;

import org.ant4eclipse.lib.core.Assure;
import org.ant4eclipse.lib.core.util.StringMap;
import org.ant4eclipse.lib.platform.model.team.projectset.TeamProjectDescription;

public abstract class AbstractTeamProjectDescription implements TeamProjectDescription {
  /** the name of the project */
  private String _projectname;

  /**
   * {@inheritDoc}
   */
  public String getProjectName() {
    return this._projectname;
  }

  public AbstractTeamProjectDescription(String projectname) {
    Assure.notNull("projectname", projectname);
    this._projectname = projectname;
  }

  public StringMap getAsProperties() {
    // Create properties object
    StringMap properties = new StringMap();

    // add project name
    properties.put("projectName", this._projectname);

    // add provider-specific properties
    addSpecificProperties(properties);

    // return properties
    return properties;
  }

  /**
   * Add (provider-)specific properties to the given StringMap
   * 
   * @param properties
   */
  protected abstract void addSpecificProperties(StringMap properties);

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int hashCode = 1;
    hashCode = 31 * hashCode + (this._projectname == null ? 0 : this._projectname.hashCode());
    return hashCode;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null) {
      return false;
    }
    if (o.getClass() != getClass()) {
      return false;
    }
    AbstractTeamProjectDescription castedObj = (AbstractTeamProjectDescription) o;
    return ((this._projectname == null ? castedObj._projectname == null : this._projectname
        .equals(castedObj._projectname)));
  }
}
