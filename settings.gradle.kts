rootProject.name = "myevent"
enableFeaturePreview("GRADLE_METADATA")

include (
 ":event-ctrl",
 ":my-common",
 ":my-jvm",
 ":my-js",
 ":my-api",
 ":my-scenarios",
 ":my-k8s"
)