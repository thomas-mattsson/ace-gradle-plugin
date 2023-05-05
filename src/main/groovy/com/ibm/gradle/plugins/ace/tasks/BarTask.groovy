package com.ibm.gradle.plugins.ace.tasks

import com.ibm.gradle.plugins.ace.utils.ProjectParser
import com.ibm.gradle.plugins.ace.utils.ProjectPaths

import java.io.File

import javax.inject.Inject

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class BarTask extends Exec {

    @OutputFile
    final File barFile

    @Input
    abstract public Property<Configuration> getConfiguration()

    private final String binpath

    @Inject
    BarTask(String binpath) {
        group = 'ACE'
        description = 'Creates a bar file from the project'
        dependsOn({getConfiguration().get()})
        Project project = getProject()
        this.barFile = ProjectPaths.getBarFile(project);
        this.binpath = binpath
    }

    @TaskAction
    @Override
    protected void exec() {
        Project project = getProject()
        String projectDir = project.getProjectDir().getAbsolutePath()
        String projectName = project.getName()
        String inputPath = ProjectPaths.getBuildDir(project).getAbsolutePath();
        String traceFile = ProjectPaths.getBuildTraceFile(project).getAbsolutePath();

        def barDependencies = getConfiguration().get().resolvedConfiguration.getResolvedArtifacts().findAll {(it.type == 'bar' || it.type == 'test-bar')}
        if (!barDependencies.isEmpty()) {
            getLogger().info('Project depends on other bar file(s) which will be used as input for new bar file');
            if (barDependencies.size() > 1) {
                getLogger().warn('Warning: ibmint package might only support a single bar file as input so execution might fail')
            }
            def args = [this.binpath + 'ibmint', 'package', '--output-bar-file', barFile.getAbsolutePath(), '--trace', traceFile]
            barDependencies.each {
                getLogger().info('Adding ' + it.name)
                args.add('--input-bar-file')
                args.add(it.file)
            }
            commandLine args
            super.exec()
            return
        }

        getLogger().info('Project directory: ' + projectDir);
        File projectFile = new File(projectDir, '.project');
        if (projectFile.exists()) {
            getLogger().info('Project file exists');
        }
        if (projectFile.exists() && ProjectParser.isApplicationProject(projectFile)) {
            getLogger().info('Project is an application, compiling maps and schemas.');
            commandLine this.binpath + 'ibmint', 'package', '--do-not-compile-java', '--compile-maps-and-schemas', '--input-path', inputPath, '--output-bar-file', barFile.getAbsolutePath(), '--project', projectName, '--trace', traceFile
        } else {
            commandLine this.binpath + 'ibmint', 'package', '--do-not-compile-java', '--input-path', inputPath, '--output-bar-file', barFile.getAbsolutePath(), '--project', projectName, '--trace', traceFile
        }
        super.exec()
    }
}