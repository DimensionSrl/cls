rootProject.name = "todos"

include(":data", ":api", "server", ":android")

enableFeaturePreview("GRADLE_METADATA")

val kotlinVersion: String by settings
val serializationVersion: String by settings
val coroutinesVersion: String by settings
val ktorVersion: String by settings

val androidCompileSdk: String by settings
val androidMinSdk: String by settings
val androidTargetSdk: String by settings

val mobileVersionCode: String by settings
val mobileVersionName: String by settings

val kotlinxMetadataJvm: String by settings