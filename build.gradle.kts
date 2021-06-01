group = "co.remotectrl.myevent"
version = "1.0"

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${co.remotectrl.ctrl.shell.cli.Versions.Dependencies.kotlin}")
    }
}