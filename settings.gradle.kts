/*
 *  Copyright (c) 2022-2023 Amadeus
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

rootProject.name = "trust-framework-adoption"

// this is needed to have access to snapshot builds of plugins
pluginManagement {
    repositories {
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {

    repositories {
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
        mavenCentral()
        mavenLocal()
    }
    versionCatalogs {
        create("libs") {
            from("org.eclipse.edc:edc-versions:0.0.1-SNAPSHOT")
        }
        create("edc") {
            version("edc", "0.0.1-SNAPSHOT")
            library("spi-core", "org.eclipse.edc", "core-spi").versionRef("edc")
            library("spi-policy", "org.eclipse.edc", "policy-spi").versionRef("edc")
            library("spi-policy-engine", "org.eclipse.edc", "policy-engine-spi").versionRef("edc")
            library("policy-engine", "org.eclipse.edc", "policy-engine").versionRef("edc")
            library("core-junit", "org.eclipse.edc", "junit").versionRef("edc")
        }
        create("identityHub") {
            version("ih", "0.0.1-SNAPSHOT")
            library("spi-core", "org.eclipse.edc", "identity-hub-spi").versionRef("ih")

        }
    }
}

include(":extensions:common:trust-framework-policies:trust-framework-policies-core")
include(":extensions:common:trust-framework-policies:trust-framework-policies-gaiax-participant-2206")
