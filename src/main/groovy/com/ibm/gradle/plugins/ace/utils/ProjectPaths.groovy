package com.ibm.gradle.plugins.ace.utils

import com.ibm.gradle.plugins.ace.utils.ProjectPaths

import java.io.File

import org.gradle.api.Project

class ProjectPaths {
    static File getBuildDir(Project project) {
        return new File(project.getBuildDir().getAbsolutePath(), 'acebuild')
    }

    static File getDeployDir(Project project) {
        return new File(project.getBuildDir().getAbsolutePath(), 'acedeploy')
    }

    static File getBuildTraceFile(Project project) {
        return new File(project.getBuildDir().getAbsolutePath(), 'trace.log')
    }

    static File getBarFile(Project project) {
        return new File(project.getBuildDir().getAbsolutePath(), project.getName() + '.bar')
    }

    static File getDeployTraceFile(Project project, String barfile) {
        return new File(project.getBuildDir().getAbsolutePath(), 'deploy' + barfile + '.log')
    }
}