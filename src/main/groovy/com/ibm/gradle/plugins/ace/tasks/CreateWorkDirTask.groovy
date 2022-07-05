package com.ibm.gradle.plugins.ace.tasks

import com.ibm.gradle.plugins.ace.utils.ProjectPaths

import java.io.File

import javax.inject.Inject

import org.gradle.api.tasks.Exec

abstract class CreateWorkDirTask extends Exec {

    @Inject
    CreateWorkDirTask(String binpath) {
        group = 'ACE'
        description = 'Creates a work directory for deploying ACE projects in the project build directory'
        String deployPath = ProjectPaths.getDeployDir(getProject()).getAbsolutePath()
        commandLine binpath + 'mqsicreateworkdir', deployPath
    }
}