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
package org.ant4eclipse.core.util;

import org.ant4eclipse.core.CoreExceptionCode;
import org.ant4eclipse.core.exception.Ant4EclipseException;
import org.ant4eclipse.core.logging.A4ELogging;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

/**
 * Just a simple replacement for the traditional Properties class. This one is easier to handle since it supports
 * generics.
 * 
 * @author Daniel Kasmeroglu (Daniel.Kasmeroglu@Kasisoft.net)
 */
public class ExtendedProperties extends Hashtable<String, String> {

  /**
   * Simply sets up this datastructure with no content.
   */
  public ExtendedProperties() {
  }

  /**
   * Reads the properties from the supplied InputStream instance.
   * 
   * @param instream
   *          The InputStream providing the content. Maybe <code>null</code>.
   * 
   * @throws Ant4EclipseException
   *           with {@link CoreExceptionCode#IO_FAILURE} in case there was an io error on the stream.
   */
  public ExtendedProperties(InputStream instream) {
    extendProperties(instream);
  }

  /**
   * Loads the properties from a resource on the classpath.
   * 
   * @param classpath
   *          The path within the classpath.
   * 
   * @throws Ant4EclipseException
   *           with code {@link CoreExceptionCode#RESOURCEIO_FAILURE} if accessing the file failed for some reason or
   *           {@link CoreExceptionCode#RESOURCE_NOT_ON_THE_CLASSPATH} if the resource is not located within the
   *           classpath in the first place.
   */
  public ExtendedProperties(String classpath) {
    extendProperties(classpath);
  }

  /**
   * Reads the properties from the supplied resource.
   * 
   * @param resource
   *          The resource providing the content. Not <code>null</code>.
   * 
   * @throws Ant4EclipseException
   *           with code {@link CoreExceptionCode#RESOURCEIO_FAILURE} if accessing the file failed for some reason.
   */
  public ExtendedProperties(URL resource) {
    extendProperties(resource);
  }

  /**
   * Reads the properties from the supplied InputStream instance.
   * 
   * @param propertiesFile
   *          The File providing the content. Not <code>null</code>.
   * 
   * @throws Ant4EclipseException
   *           with code {@link CoreExceptionCode#FILEIO_FAILURE} if accessing the file failed for some reason.
   */
  public ExtendedProperties(File propertiesFile) {
    extendProperties(propertiesFile);
  }

  /**
   * Reads the properties from the supplied resource.
   * 
   * @param resource
   *          The resource providing the content. Not <code>null</code>.
   * 
   * @throws Ant4EclipseException
   *           with code {@link CoreExceptionCode#RESOURCEIO_FAILURE} if accessing the file failed for some reason.
   */
  public void extendProperties(URL resource) {
    InputStream instream = null;
    try {
      instream = resource.openStream();
      extendProperties(instream);
    } catch (Ant4EclipseException ex) {
      if (ex.getExceptionCode() == CoreExceptionCode.IO_FAILURE) {
        throw new Ant4EclipseException(ex.getCause(), CoreExceptionCode.RESOURCEIO_FAILURE, resource.toExternalForm());
      } else {
        throw ex;
      }
    } catch (IOException ex) {
      throw new Ant4EclipseException(ex, CoreExceptionCode.RESOURCEIO_FAILURE, resource.toExternalForm());
    } finally {
      Utilities.close(instream);
    }
  }

  /**
   * Reads the properties from the supplied InputStream instance.
   * 
   * @param propertiesFile
   *          The File providing the content. Not <code>null</code>.
   * 
   * @throws Ant4EclipseException
   *           with code {@link CoreExceptionCode#FILEIO_FAILURE} if accessing the file failed for some reason.
   */
  public void extendProperties(File propertiesFile) {
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(propertiesFile);
      extendProperties(fis);
      A4ELogging.debug("Read settings from '%s'", propertiesFile.getAbsolutePath());
    } catch (Ant4EclipseException ex) {
      if (ex.getExceptionCode() == CoreExceptionCode.IO_FAILURE) {
        throw new Ant4EclipseException(ex.getCause(), CoreExceptionCode.FILEIO_FAILURE, propertiesFile);
      } else {
        throw ex;
      }
    } catch (IOException ex) {
      throw new Ant4EclipseException(ex, CoreExceptionCode.FILEIO_FAILURE, propertiesFile);
    } finally {
      Utilities.close(fis);
    }
  }

  /**
   * Reads the properties from the supplied InputStream instance.
   * 
   * @param instream
   *          The InputStream providing the content. Maybe <code>null</code>.
   * 
   * @throws Ant4EclipseException
   *           with {@link CoreExceptionCode#IO_FAILURE} in case there was an io error on the stream.
   */
  public void extendProperties(InputStream instream) {
    if (instream != null) {
      Properties properties = new Properties();
      try {
        properties.load(instream);
      } catch (IOException ex) {
        throw new Ant4EclipseException(ex, CoreExceptionCode.IO_FAILURE);
      }
      for (Map.Entry<Object, Object> entry : properties.entrySet()) {
        put((String) entry.getKey(), (String) entry.getValue());
      }
    }
  }

  /**
   * Loads the properties from a resource on the classpath.
   * 
   * @param classpath
   *          The path within the classpath.
   * 
   * @throws Ant4EclipseException
   *           with code {@link CoreExceptionCode#RESOURCEIO_FAILURE} if accessing the file failed for some reason or
   *           {@link CoreExceptionCode#RESOURCE_NOT_ON_THE_CLASSPATH} if the resource is not located within the
   *           classpath in the first place.
   */
  public void extendProperties(String classpath) {
    URL resource = Utilities.class.getResource(classpath);
    if (resource == null) {
      throw new Ant4EclipseException(CoreExceptionCode.RESOURCE_NOT_ON_THE_CLASSPATH, classpath);
    }
    extendProperties(resource);
  }

  /**
   * 
   * @param key
   * @param defvalue
   * @return
   */
  public String get(String key, String defvalue) {
    if (containsKey(key)) {
      return get(key);
    } else {
      return defvalue;
    }
  }

} /* ENDCLASS */