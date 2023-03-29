plugins {
    java
    id("com.github.weave-mc.weave") version "8b70bcc707"
}

group = "wtf.zani"
version = "2.0"

minecraft.version("1.8.9")

repositories {
    maven("https://jitpack.io")
    maven("https://repo.spongepowered.org/maven/")
}

dependencies {
    compileOnly("org.spongepowered:mixin:0.8.5")
    compileOnly("com.github.Weave-MC:Weave-Loader:6a9e6a3245")
}

tasks.compileJava {
    options.release.set(17)
}

tasks.jar {
    destinationDirectory.set(File("${System.getProperty("user.home")}/.lunarclient/mods"))
}
