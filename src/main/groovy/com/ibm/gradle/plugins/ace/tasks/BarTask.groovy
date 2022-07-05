package com.ibm.gradle.plugins.ace.tasks

import com.ibm.gradle.plugins.ace.utils.ProjectParser
import com.ibm.gradle.plugins.ace.utils.ProjectPaths

import java.io.File

import javax.inject.Inject

import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.OutputFile

abstract class BarTask extends Exec {

    @OutputFile
    final File barFile

    @Inject
    BarTask(String binpath) {
        group = 'ACE'
        description = 'Creates a bar file from the project'
        Project project = getProject()
        String projectDir = project.getProjectDir().getAbsolutePath()
        String projectName = project.getName()
        this.barFile = ProjectPaths.getBarFile(project);
        String inputPath = ProjectPaths.getBuildDir(project).getAbsolutePath();
        String traceFile = ProjectPaths.getBuildTraceFile(project).getAbsolutePath();

        File projectFile = new File(projectDir, '.project');
        if (projectFile.exists() && ProjectParser.isApplicationProject(projectFile)) {
            getLogger().info('Project is an application, compiling maps and schemas.');
            commandLine binpath + 'ibmint', 'package', '--do-not-compile-java', '--compile-maps-and-schemas', '--input-path', inputPath, '--output-bar-file', barFile.getAbsolutePath(), '--project', projectName, '--trace', traceFile
        } else {
            commandLine binpath + 'ibmint', 'package', '--do-not-compile-java', '--input-path', inputPath, '--output-bar-file', barFile.getAbsolutePath(), '--project', projectName, '--trace', traceFile
        }
    }
}