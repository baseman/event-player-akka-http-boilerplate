plugins {
    kotlin("multiplatform")
}

repositories {
    jcenter()
}

kotlin {
    targets {
        jvm {
        }

        js {
            useCommonJs()
        }
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(project(":event-player"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation("org.spekframework.spek2:spek-runner-junit5:2.0.5")
                implementation("org.spekframework.spek2:spek-dsl-jvm:2.0.5")
                implementation("org.amshove.kluent:kluent:1.4")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
    }
}