plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.10"
}
apply(plugin = "application")

group = "com.pavlus.raytrace"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("com.pavlus.raytrace:common-lib:0.1")
    implementation("com.pavlus.raytrace:common-lib-jvm:0.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.3.10")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.10")
    implementation("no.tornado:tornadofx:1.7.17")

}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}
tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
}


val mainClassName = "com.example.demo.app.MyApp"

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Class-Path" to (configurations as Set<Configuration>).filter{it.name == "compile"}.joinToString(" ", transform = { it.name }),
                "Main-Class" to mainClassName
            )
        )
    }
    from(configurations.compile.map { entry -> zipTree(entry) }) {
        exclude(
            "META-INF/MANIFEST.MF",
            "META-INF/*.SF",
            "META-INF/*.DSA",
            "META-INF/*.RSA"
        )
    }
}
