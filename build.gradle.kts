/*
 *  Copyright (c) 2022 Amadeus
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Amadeus - initial API and implementation
 *
 */


plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

val javaVersion: String by project
val edcScmConnection: String by project
val edcWebsiteUrl: String by project
val edcScmUrl: String by project
val edcGroup: String by project
val defaultVersion: String by project
val annotationProcessorVersion: String by project
val metaModelVersion: String by project

val actualVersion: String = (project.findProperty("trustFrameworkAdoptionVersion") ?: defaultVersion) as String

buildscript {
    dependencies {
        val edcGradlePluginsVersion: String by project
        classpath("org.eclipse.dataspaceconnector.edc-build:org.eclipse.dataspaceconnector.edc-build.gradle.plugin:${edcGradlePluginsVersion}")
    }
}

allprojects {
    apply(plugin = "${edcGroup}.edc-build")

    // configure which version of the annotation processor to use. defaults to the same version as the plugin
    configure<org.eclipse.edc.plugins.autodoc.AutodocExtension> {
        processorVersion.set(annotationProcessorVersion)
        outputDirectory.set(project.buildDir)
    }

    // the following block is optional, as the BuildExtension already provides defaults
    configure<org.eclipse.edc.plugins.edcbuild.extensions.BuildExtension> {
        versions {
            // override default dependency versions here
            projectVersion.set(actualVersion)
            metaModel.set(metaModelVersion)
        }
        pom {
            projectName.set(project.name)
            description.set("edc :: ${project.name}")
            projectUrl.set(edcWebsiteUrl)
            scmConnection.set(edcScmConnection)
            scmUrl.set(edcScmUrl)
        }
        javaLanguageVersion.set(JavaLanguageVersion.of(javaVersion))
    }

    configure<CheckstyleExtension> {
        configFile = rootProject.file("resources/checkstyle-config.xml")
        configDirectory.set(rootProject.file("resources"))
    }


    // EdcRuntimeExtension uses this to determine the runtime classpath of the module to run.
    tasks.register("printClasspath") {
        doLast {
            println(sourceSets["main"].runtimeClasspath.asPath)
        }
    }
}