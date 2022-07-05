package com.ibm.gradle.plugins.ace.tasks

import com.ibm.gradle.plugins.ace.utils.ProjectPaths

import java.io.File

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Copy

class CopyBuildFilesTask extends Copy {

    CopyBuildFilesTask() {
        group = 'ACE'
        description = 'Copies the necessary files to build the bar file'
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
}