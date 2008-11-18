package org.ant4eclipse;


import org.ant4eclipse.core.Ant4EclipseConfigurationPropertiesTest;
import org.ant4eclipse.core.AssertTest;
import org.ant4eclipse.core.dependencygraph.DependencyGraphTest;
import org.ant4eclipse.core.logging.A4ELogging_FailureTest;
import org.ant4eclipse.core.service.ServiceRegistryTest;
import org.ant4eclipse.core.util.JarUtilitiesTest;
import org.ant4eclipse.core.util.UtilitiesTest;
import org.ant4eclipse.core.xquery.XQueryHandlerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { JarUtilitiesTest.class, Ant4EclipseConfigurationPropertiesTest.class, AssertTest.class,
    XQueryHandlerTest.class, UtilitiesTest.class, ServiceRegistryTest.class, A4ELogging_FailureTest.class,
    DependencyGraphTest.class })
public class AllTests {
}
