package com.ibm.gradle.plugins.ace.extensions

import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.Property

interface DeployExtension {
    Property<Configuration> getConfiguration()
    Property<Configuration> getAdditionalTestProjectConfiguration()
}