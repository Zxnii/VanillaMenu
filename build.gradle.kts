plugins {
    java
    id("com.github.weave-mc.weave") version "8b70bcc707"
}

group = "wtf.zani"
version = "3.0.0"

minecraft.version("1.8.9")

repositories {
    maven("https://jitpack.io")
    maven("https://repo.spongepowered.org/maven/")
}

dependencies {
    compileOnly("org.spongepowered:mixin:0.8.5")
    compileOnly("com.github.weave-mc:weave-loader:v0.2.0")
}

tasks.compileJava {
    options.release.set(17)
}

tasks.jar {
    destinationDirectory.set(File("${System.getProperty("user.home")}/.lunarclient/mods"))
}
