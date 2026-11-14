// Shared repositories for all modules. Declared on project level (not in settings) so modules
// can add their own repositories, e.g. for plugin-yml's generated paper-libraries.json
repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc"
    }
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") {
        name = "placeholderapi"
    }
}
