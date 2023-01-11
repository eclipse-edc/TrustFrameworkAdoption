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
    `java-test-fixtures`
    `maven-publish`
}

dependencies {
    api(project(":spi:common:trust-framework-spi"))
    implementation(project(":extensions:common:trust-framework-policies:trust-framework-policies-core"))

    api(edc.spi.policy.engine)

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