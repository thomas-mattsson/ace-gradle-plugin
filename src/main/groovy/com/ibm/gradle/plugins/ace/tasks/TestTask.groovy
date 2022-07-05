package com.ibm.gradle.plugins.ace.tasks

import com.ibm.gradle.plugins.ace.utils.ProjectPaths

import javax.inject.Inject

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

abstract class TestTask extends Exec {

    @Input
    abstract public Property<Configuration> getConfiguration()

    @Input
    @Optional
    abstract public Property<Configuration> getAdditionalTestProjectConfiguration()

    private final String binpath

    @Inject
    TestTask(String binpath) {
        this.binpath = binpath
        dependsOn({getConfiguration().get()})
        group = 'ACE'
        description = 'Starts an ACE integration server and executes test projects from the provided configuration'
    }

    @TaskAction
    @Override
    protected void exec() {
        String binpath = this.binpath
        Project project = getProject()
        String workdir = ProjectPaths.getDeployDir(project).getAbsolutePath()
        getConfiguration().get().resolvedConfiguration.getResolvedArtifacts().each {
            if (it.type == 'test-bar') {
                getLogger().info('Testing using ' + it.name)
                String traceFile = ProjectPaths.getDeployTraceFile(project, it.name).getAbsolutePath()
                println getAdditionalTestProjectConfiguration().getOrNull()
                if (getAdditionalTestProjectConfiguration().isPresent()) {
                    boolean first = true;
                    String integrationServerClassPath = '';
                    getAdditionalTestProjectConfiguration().get().each {
                        integrationServerClassPath += (first ? '' : ':') + it.toString()
                        first = false
                    }
                    getLogger().debug('Using additional test project classpath: ' + integrationServerClassPath)
                    commandLine binpath + 'IntegrationServer', '-w', workdir, '--test-project', it.name, '--admin-rest-api', '-1', '--no-nodejs', '--additional-test-project-classpath', integrationServerClassPath
                } else {
                    commandLine binpath + 'IntegrationServer', '-w', workdir, '--test-project', it.name, '--admin-rest-api', '-1', '--no-nodejs'
                }
                super.exec()
            }
        }
    }
}