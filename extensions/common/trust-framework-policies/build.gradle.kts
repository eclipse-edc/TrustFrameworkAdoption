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

val edcVersion: String by project
val edcGroup: String by project

val jupiterVersion: String by project
val mockitoVersion: String by project
val assertj: String by project
val jsonpathVersion: String by project

dependencies {
    api(project(":spi:common:trust-framework-spi"))

    api("${edcGroup}:core-spi:${edcVersion}")
    api("${edcGroup}:policy-spi:${edcVersion}")
    api("${edcGroup}:policy-engine-spi:${edcVersion}")

    implementation("com.jayway.jsonpath:json-path:${jsonpathVersion}")

    testImplementation("${edcGroup}:policy-engine:${edcVersion}")
    testImplementation("${edcGroup}:junit:${edcVersion}")

    testImplementation("org.assertj:assertj-core:${assertj}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${jupiterVersion}")
    testImplementation("org.mockito:mockito-core:${mockitoVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${jupiterVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${jupiterVersion}")
    testImplementation("org.mockito:mockito-core:${mockitoVersion}")
}
