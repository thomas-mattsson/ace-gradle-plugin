package com.ibm.gradle.plugins.ace.utils

class ProjectParser {

    static boolean isApplicationProject(File projectFile) {
        return isProjectOfType(projectFile, 'com.ibm.etools.msgbroker.tooling.applicationNature')
    }

    static boolean isLibraryProject(File projectFile) {
        return isProjectOfType(projectFile, 'com.ibm.etools.msgbroker.tooling.libraryNature')
    }

    static boolean isSharedLibraryProject(File projectFile) {
        return isProjectOfType(projectFile, 'com.ibm.etools.msgbroker.tooling.sharedLibraryNature')
    }

    static boolean isPolicyProject(File projectFile) {
        return isProjectOfType(projectFile, 'com.ibm.etools.mft.policy.ui.Nature')
    }

    static boolean isTestProject(File projectFile) {
        return isProjectOfType(projectFile, 'com.ibm.etools.msgbroker.tooling.testProjectNature')
    }

    static boolean isProjectOfType(File projectFile, String type) {
        Object projectDescription = new XmlSlurper().parse(projectFile)
        if (projectDescription && projectDescription.natures && projectDescription.natures.'*'.find { node ->
            node.text() == type
        }) {
            return true
        }
        return false
    }
}