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

plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    api(project(":spi:common:trust-framework-spi"))

    api(edc.spi.core)
    api(edc.spi.policy)
    api(edc.spi.policy.engine)

    implementation(libs.jsonpath)

    testImplementation(edc.policy.engine)
    testImplementation(edc.core.junit)
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
