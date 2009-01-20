package org.ant4eclipse.platform.tools;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.ant4eclipse.core.dependencygraph.DependencyGraph;
import org.ant4eclipse.platform.model.resource.EclipseProject;
import org.ant4eclipse.platform.model.resource.Workspace;

/**
 * <p>
 * Resolves the build order of the projects with the given names.
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class BuildOrderResolver {

  /**
   * <p>
   * Resolves the build order with the given name.
   * </p>
   * 
   * @param workspace
   *          the workspace
   * @param projectNames
   *          the project names
   * @param properties
   * @return
   */
  public static final List<EclipseProject> resolveBuildOrder(final Workspace workspace, final String[] projectNames,
      final Properties properties) {

    // retrieve all eclipse projects from the workspace
    final EclipseProject[] eclipseProjects = workspace.getProjects(projectNames, true);

    // create a dependency graph
    final DependencyGraph<EclipseProject> dependencyGraph = new DependencyGraph<EclipseProject>();

    // iterate over the eclipse projects
    for (final EclipseProject eclipseProject : eclipseProjects) {

      // resolve referenced projects
      final List<EclipseProject> referencedProjects = ReferencedProjectsResolverService.Helper.getService()
          .resolveReferencedProjects(eclipseProject, properties);

      // add referenced projects to the dependency graph
      for (final EclipseProject referencedProject : referencedProjects) {
        if (!dependencyGraph.containsVertex(referencedProject)) {
          dependencyGraph.addVertex(referencedProject);
        }
        if (!eclipseProject.equals(referencedProject)) {
          dependencyGraph.addEdge(eclipseProject, referencedProject);
        }
      }
    }

    // calculate the order
    final List<EclipseProject> orderProjects = dependencyGraph.calculateOrder();

    // filter result - only the requested projects should be listed
    final List<EclipseProject> result = new LinkedList<EclipseProject>();
    final List<String> names = Arrays.asList(projectNames);
    for (final EclipseProject eclipseProject : orderProjects) {
      if (names.contains(eclipseProject.getSpecifiedName())) {
        result.add(eclipseProject);
      }
    }

    // return the result
    return result;
  }
}