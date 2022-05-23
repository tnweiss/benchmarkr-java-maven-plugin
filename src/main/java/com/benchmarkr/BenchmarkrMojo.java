package com.benchmarkr;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import com.github.benchmarkr.BenchmarkrCore;
import com.github.benchmarkr.console.Consoles;
import com.github.benchmarkr.test.plan.TestPlanParameters;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;


@Mojo(name = "benchmark", defaultPhase =  LifecyclePhase.TEST)
public class BenchmarkrMojo extends AbstractMojo {
  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  MavenProject project;

  @Parameter(defaultValue = BenchmarkrCore.PACKAGE_PROPERTY_DEFAULT, property = BenchmarkrCore.PACKAGE_PROPERTY)
  String packageName;

  @Parameter(property = BenchmarkrCore.CLASS_PROPERTY, defaultValue = BenchmarkrCore.CLASS_PROPERTY_DEFAULT)
  String className;

  @Parameter(defaultValue = BenchmarkrCore.METHOD_PROPERTY_DEFAULT, property = BenchmarkrCore.METHOD_PROPERTY)
  String methodName;

  @Parameter(defaultValue = BenchmarkrCore.ITERATIONS_PROPERTY_DEFAULT, property = BenchmarkrCore.ITERATIONS_PROPERTY)
  int iterations;

  @Parameter(defaultValue = BenchmarkrCore.CONSOLE_PROPERTY_DEFAULT, property = BenchmarkrCore.CONSOLE_PROPERTY)
  String console;

  @Parameter(defaultValue = BenchmarkrCore.RECORD_PROPERTY_DEFAULT, property = BenchmarkrCore.RECORD_PROPERTY)
  boolean record;

  @Parameter(defaultValue = BenchmarkrCore.IGNORE_FAILURES_PROPERTY_DEFAULT,
      property = BenchmarkrCore.IGNORE_FAILURES_PROPERTY)
  boolean ignoreFailures;

  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info("Running Benchmarking Tests");

    TestPlanParameters testPlanParameters = TestPlanParameters.builder()
        .console(Consoles.system())
        .classLoader(getClassLoader())
        .packageName(packageName == null || packageName.isBlank() ? project.getGroupId() : packageName)
        .className(className)
        .methodName(methodName)
        .iterations(iterations)
        .console(Consoles.from(console))
        .recordResults(record)
        .ignoreFailure(ignoreFailures)
        .build();

    getLog().info("Scanning for tests in package " + testPlanParameters.getPackageName());

    if (BenchmarkrCore.main(testPlanParameters) != 0) {
      throw new MojoFailureException("Benchmark tests failed");
    }
  }

  /**
   * Solution pulled from <a href="https://stackoverflow.com/questions/871708/maven-plugin-cant-load-class">Stack Overflow</a>
   *   and modified. Get a classloader for the dependent projects test sources.
   *
   * @throws MojoExecutionException on any errors loading the test classpath
   * @return the dependent projects test resource classpath
   */
  private ClassLoader getClassLoader() throws MojoExecutionException
  {
    try {
      String testOutputDirectory = project.getBuild().getTestOutputDirectory();
      getLog().debug("Loading test resources from " + testOutputDirectory);

      URL[] testOutputUrls = {new File(testOutputDirectory).toURI().toURL()};
      return new URLClassLoader(testOutputUrls, getClass().getClassLoader());
    } catch (Exception e) {
      throw new MojoExecutionException("Couldn't create a classloader.", e);
    }
  }
}
