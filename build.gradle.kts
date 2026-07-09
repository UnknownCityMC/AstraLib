plugins {
    id("base")
}

group = "de.unknowncity.astralib"
version = "0.7.0-SNAPSHOT"

// Paper 26.1.2 requires java 25, modules targeting older runtimes set options.release themselves
subprojects {
    plugins.withType<JavaPlugin> {
        extensions.configure<JavaPluginExtension> {
            toolchain.languageVersion.set(JavaLanguageVersion.of(25))
        }
    }
}
