import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.gradle.plugin.publish)
    alias(libs.plugins.vanniktech.maven.publish)
}

val projectGroup: String by extra

gradlePlugin {
    plugins {
        create(rootProject.name) {
            id = projectGroup
            implementationClass = "com.xzakota.gradle.plugin.xposed.GradlePluginForXposed"
        }
    }
}

val generatedDir = File(projectDir, "build/generated")
val generatedJavaSourcesDir = File(generatedDir, "main/java")

tasks {
    val task = register("generateBuildConfigClass") {
        inputs.property("version", version)
        outputs.dir(generatedDir)

        doLast {
            val buildClassFile = File(generatedJavaSourcesDir, "${projectGroup.replace(".", "/")}/BuildConfig.java")
            buildClassFile.parentFile.mkdirs()
            buildClassFile.writeText(
                """
                package $projectGroup;
                
                /**
                 * By Gradle
                 */
                public class BuildConfig {
                   private BuildConfig() {}
                   
                   /**
                    * Project Version
                    */
                   public static final String VERSION = "$version";
                }
                """.trimIndent()
            )
        }
    }

    withType(KotlinCompile::class.java) {
        dependsOn(task)
    }

    withType(Jar::class.java) {
        dependsOn(task)
    }
}

sourceSets {
    main {
        java {
            srcDir(generatedJavaSourcesDir)
        }
    }
}

dependencies {
    compileOnly(libs.android.tools)

    implementation(libs.dom4j)
    implementation(libs.grip)

    implementation(libs.xposed.module.maker.annotation)
    api(libs.xposed.module.maker.constant)
}
