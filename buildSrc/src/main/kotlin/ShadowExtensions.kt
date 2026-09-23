import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val ShadowJar.shadeBasePath: String
    get() = "${project.rootProject.group}.libs."

fun ShadowJar.relocateDependency(from: String) {
    relocate(from, "$shadeBasePath$from")
}
