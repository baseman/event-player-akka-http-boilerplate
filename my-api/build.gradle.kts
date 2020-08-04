repositories {
  jcenter()
  mavenCentral()
//  maven { url "http://dl.bintray.com/jetbrains/spek" }
//  maven { url "http://repo.spring.io/plugins-release/" }
//  maven { url "https://plugins.gradle.org/m2/"}
//
//  maven { url "http://dl.bintray.com/jetbrains/spek" }
//  maven { url "http://repo.spring.io/plugins-release/" }
}

plugins {
  kotlin("jvm")
  kotlin("plugin.spring") version "1.3.61"
  application
//  id("eclipse") version "5.2.0"
  id("org.springframework.boot") version "2.2.2.RELEASE"
  id("io.spring.dependency-management") version "1.0.8.RELEASE"
//  "jacoco"
}

//classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
//classpath("org.jetbrains.kotlin:kotlin-allopen:${kotlinVersion}")
//classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
//classpath("org.junit.platform:junit-platform-gradle-plugin:1.0.0")
//classpath("org.sonarqube:org.sonarqube.gradle.plugin:$sonarVersion")

kotlin {
  sourceSets {
    val main by getting {
      dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation(project(":event-player"))
        implementation(project(":my-common"))

        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.springframework.boot:spring-boot-starter")

        implementation("com.typesafe.akka:akka-http_2.12:10.1.4")
        implementation("com.typesafe.akka:akka-http-jackson_2.12:10.1.4")
        implementation("com.typesafe.akka:akka-stream_2.12:2.5.16")

        implementation("com.github.swagger-akka-http:swagger-akka-http_2.12:1.0.0")
        implementation("io.swagger:swagger-jaxrs:1.5.18")
        implementation("ch.megard:akka-http-cors_2.12:0.3.0")
      }
    }
    val test by getting {
      dependencies {
        implementation(kotlin("test-junit"))

        implementation("com.typesafe.akka:akka-http-testkit_2.12:10.1.4")

        implementation("org.springframework.boot:spring-boot-starter-test:2.0.5.RELEASE")
      }
    }
  }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions {
    jvmTarget = "1.8"
  }
}

application {
  mainClassName = "co.remotectrl.ctrl.shell.application.CtrlShellKt"
}

//test {
//  useJUnitPlatform()
//}



//jar {
//  manifest {}
//
//  // This line of code recursively collects and copies all of a project's files
//  // and adds them to the JAR itself. One can extend this task, to skip certain
//  // files or particular types at will
//  from { configurations.compile.collect { it.isDirectory() ? it : zipTree(it) } }
//}

//test {
//  jacoco {
//
//  }
//}

//jacocoTestReport {
//  reports {
//    xml.enabled = false
//    csv.enabled = false
//    html.destination file("${buildDir}/jacocoHtml")
//  }
//}
//
//
//jacocoTestCoverageVerification {
//  violationRules {
//    rule {
//      limit {
//        minimum = 0.5
//      }
//    }
//
//    rule {
//      enabled = false
//      element = 'CLASS'
//      includes = ['org.gradle.*']
//
//      limit {
//        counter = 'LINE'
//        value = 'TOTALCOUNT'
//        maximum = 0.3
//      }
//    }
//  }
//}