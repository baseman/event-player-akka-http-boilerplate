package co.remotectrl.ctrl.shell.cli


import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.testing.Test

fun configureJUNITReports(tasks: TaskContainer) {
    tasks.withType(Test::class.java) {
        useJUnit()
        reports.junitXml.isEnabled = true
    }
}
