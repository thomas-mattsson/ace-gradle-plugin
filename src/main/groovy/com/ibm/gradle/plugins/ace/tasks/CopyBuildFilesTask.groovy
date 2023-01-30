package com.ibm.gradle.plugins.ace.tasks

import com.ibm.gradle.plugins.ace.utils.ProjectPaths

import java.io.File

import javax.inject.Inject

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.Property
import org.gradle.api.Task
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class CopyBuildFilesTask extends Copy {

    @Input
    abstract public Property<Configuration> getConfiguration()

    @Inject
    CopyBuildFilesTask() {
        group = 'ACE'
        description = 'Copies the necessary files to build the bar file'
        dependsOn({getConfiguration().get()})
        Project project = getProject()
        File aceBuildDir = new File(ProjectPaths.getBuildDir(project).getAbsolutePath(), project.getName());
        from project.fileTree(dir: project.getProjectDir())
        exclude 'build'
        include '**/*.xsdzip'
        include '**/*.tblxmi'
        include '**/*.xsd'
        include '**/*.wsdl'
        include '**/*.dictionary'
        include '**/*.xsl'
        include '**/*.xslt'
        include '**/*.xml'
        include '**/*.jar'
        include '**/*.inadapter'
        include '**/*.outadapter'
        include '**/*.insca'
        include '**/*.outsca'
        include '**/*.descriptor'
        include '**/*.idl'
        include '**/*.map'
        include '**/*.msgflow'
        include '**/*.json'
        include '**/*.policyxml'
        include '**/*.esql'
        include '**/.project'

        Task jarTask = project.tasks.findByName('jar')
        if (jarTask) {
            File jarFile = jarTask.outputs.getFiles().getSingleFile()
            if (jarFile) {
                getLogger().info('Including jar: ' + jarFile)
                from jarFile
            }
        }

        includeEmptyDirs = false
        into aceBuildDir
    }

    @TaskAction
    @Override
    protected void copy() {
        getConfiguration().get().resolvedConfiguration.getResolvedArtifacts().each {
            if (it.type == 'jar') {
                getLogger().info('Including jar: ' + it.file)
                from it.file
            }
        }
        
        super.copy()
    }
}
