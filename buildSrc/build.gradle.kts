plugins {
    `kotlin-dsl`
}
repositories {
    gradlePluginPortal()
    mavenCentral()
    jcenter()
}
kotlinDslPluginOptions {
    experimentalWarning.set(false)
}
