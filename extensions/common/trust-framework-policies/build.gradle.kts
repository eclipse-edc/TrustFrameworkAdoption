plugins {
    `java-library`
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
