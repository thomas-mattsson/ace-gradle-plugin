package com.ibm.gradle.plugins.ace.tasks

import com.ibm.gradle.plugins.ace.utils.ProjectPaths

import javax.inject.Inject

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class DeployTask extends Exec {

    @Input
    abstract public Property<Configuration> getConfiguration()

    private final String binpath

    @Inject
    DeployTask(String binpath) {
        this.binpath = binpath
        dependsOn({getConfiguration().get()})
        group = 'ACE'
        description = 'Deploys ACE projects from the provided configuration'
    }

    @TaskAction
    @Override
    protected void exec() {
        String binpath = this.binpath
        Project project = getProject()
        String workdir = ProjectPaths.getDeployDir(project).getAbsolutePath()
        getConfiguration().get().resolvedConfiguration.getResolvedArtifacts().each {
            if (it.type == 'bar' || it.type == 'test-bar') {
                getLogger().info('Deploying ' + it.file.name)
                String traceFile = ProjectPaths.getDeployTraceFile(project, it.name).getAbsolutePath()
                commandLine binpath + 'ibmint', 'deploy', '--do-not-compile-java', '--output-work-directory', workdir, '--trace', traceFile, '--input-bar-file', it.file.path
                super.exec()
            }
        }
    }
}