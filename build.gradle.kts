plugins {
    java
    id("com.github.weave-mc.weave") version "8b70bcc707"
}

group = "wtf.zani"
version = "1.0"

minecraft.version("1.8.9")

repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.Weave-MC:Weave-Loader:0c09d7496f")
}

tasks.compileJava {
    options.release.set(17)
}

tasks.jar {
    manifest.attributes(
        "Weave-Entry" to "wtf.zani.vanillamenu.VanillaMenu"
    )
}
