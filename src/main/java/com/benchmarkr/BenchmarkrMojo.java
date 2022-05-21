package com.benchmarkr;

import com.github.benchmarkr.BenchmarkrCore;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;


@Mojo(name = "benchmark", defaultPhase =  LifecyclePhase.TEST)
public class BenchmarkrMojo extends AbstractMojo {
  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  MavenProject project;

  public void execute() {
    getLog().info("Running Benchmarking Tests");
    getLog().info("Scanning for tests in package " + project.getGroupId());

    String[] args = {"-p", project.getGroupId()};
    BenchmarkrCore.main(args);
  }
}
