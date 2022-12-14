package com.ibm.gradle.plugins.ace

import com.ibm.gradle.plugins.ace.extensions.TestExtension
import com.ibm.gradle.plugins.ace.tasks.BarTask
import com.ibm.gradle.plugins.ace.tasks.CopyBuildFilesTask
import com.ibm.gradle.plugins.ace.tasks.CreateWorkDirTask
import com.ibm.gradle.plugins.ace.tasks.DeployTask
import com.ibm.gradle.plugins.ace.tasks.TestTask
import com.ibm.gradle.plugins.ace.utils.ProjectParser

import java.io.File

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency

class ACEPlugin implements Plugin<Project> {
    void apply(Project project) {
        String baseFilePath = System.getenv('MQSI_BASE_FILEPATH')
        if (!baseFilePath) {
            throw new GradleException('You must source mqsiprofile to configure your build environment first')
        }
        String binpath = baseFilePath + '/server/bin/'
        String commonclasspath = baseFilePath + '/common/classes'
        String serverclasspath = baseFilePath + '/server/classes'
        Configuration aceconfig = project.getConfigurations().create('ace')
        Configuration testconfig = project.getConfigurations().create('acetest')
        testconfig.extendsFrom(aceconfig)

        Task copyTask = project.task('copybarsource', type: CopyBuildFilesTask)
        copyTask.getConfiguration().set(aceconfig)
        Task barTask = project.task('bar', type: BarTask, constructorArgs: [binpath])
        barTask.dependsOn(copyTask)

        // If there's a jar task in the project, make sure we depend on it
        Task jarTask = project.tasks.findByName('jar')
        if (jarTask) {
            copyTask.dependsOn(jarTask)
        }

        String type = 'bar'
        File projectFile = new File(project.getProjectDir(), '.project');
        if (projectFile.exists() && ProjectParser.isTestProject(projectFile)) {
            type = 'test-bar'
            Configuration compileOnly = project.getConfigurations().findByName('compileOnly')
            if (compileOnly) {
                // Adding necessary dependencies for java compilation of test projects
                Dependency dep = project.dependencies.create(project.fileTree(dir: commonclasspath, include: 'Integration*.jar'))
                project.dependencies.add('compileOnly', dep)
                dep = project.dependencies.create(project.fileTree(dir: serverclasspath, include: 'junit*.jar'))
                project.dependencies.add('compileOnly', dep)
            }
        }

        project.getArtifacts().add(type == 'test-bar' ? 'acetest' : 'ace', barTask.barFile, cpa -> {
            cpa.setName(project.getName())
            cpa.setExtension('bar')
            cpa.setType(type)
            cpa.builtBy(barTask)
        })

        Task createworkdirTask = project.task('createworkdir', type: CreateWorkDirTask, constructorArgs: [binpath])
        Task deployTask = project.task('acedeploy', type: DeployTask, constructorArgs: [binpath])
        deployTask.dependsOn(createworkdirTask)
        deployTask.getConfiguration().set(testconfig)
        Object testExtension = project.getExtensions().create('acetest', TestExtension);
        Task testTask = project.task('acetest', type: TestTask, constructorArgs: [binpath])
        testTask.dependsOn(deployTask)
        testTask.getConfiguration().set(testconfig)
        testTask.getAdditionalTestProjectConfiguration().set(testExtension.getAdditionalTestProjectConfiguration())
    }
}