plugins {
    `java-library`
}

dependencies {
    api(edc.spi.core)

    testImplementation(libs.assertj)
}
