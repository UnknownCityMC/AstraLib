plugins {
    id("java")
    alias(libs.plugins.run.velocity)
    id("com.gradleup.shadow")
    alias(libs.plugins.blossom)
}

dependencies {
    implementation(project(":astralib-common"))
    implementation(project(":astralib-velocity-api"))

    implementation(libs.mariadb.client)
    implementation(libs.gson)

    compileOnly(libs.velocity)
    annotationProcessor(libs.velocity)
}

fun authorsAsString(vararg authors: String) = authors.joinToString("\", \"")
val mainClass = "de.unknowncity.astralib.velocity.plugin.AstraLibVelocityPlugin"
val authors = authorsAsString("UnknownCity", "TheZexquex")

sourceSets {
    main {
        blossom {
            resources {
                property("version", project.version.toString())
                property("id", "astralib-velocity")
                property("name", "AstraLib-Velocity")
                property("main", mainClass)
                property(
                    "description",
                    "A super cool plugin utility library for Velocity"
                )
                property("authors", authors)
            }
        }
    }
}

tasks {
    jar {
        enabled = false;
    }

    shadowJar {
        dependsOn(":astralib-velocity-api:build")
        archiveBaseName.set("AstraLib-Velocity")
    }

    runVelocity {
        velocityVersion("3.5.0-SNAPSHOT")
    }
}
