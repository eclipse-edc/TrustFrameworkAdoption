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
            version("jsonpath", "2.7.0")

            library("jsonpath", "com.jayway.jsonpath", "json-path").versionRef("jsonpath")
        }
        create("edc") {
            version("edc", "0.0.1-20221113-SNAPSHOT")
            library("spi-core", "org.eclipse.edc", "core-spi").versionRef("edc")
            library("spi-policy", "org.eclipse.edc", "policy-spi").versionRef("edc")
            library("spi-policy-engine", "org.eclipse.edc", "policy-engine-spi").versionRef("edc")
            library("policy-engine", "org.eclipse.edc", "policy-engine").versionRef("edc")
            library("core-junit", "org.eclipse.edc", "junit").versionRef("edc")
        }
    }
}


include(":spi:common:trust-framework-spi")
include(":extensions:common:trust-framework-policies")
