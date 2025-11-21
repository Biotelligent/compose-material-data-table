import com.vanniktech.maven.publish.*
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    // Recommended by https://github.com/kotlin-hands-on/fibonacci
    alias(libs.plugins.vanniktechMavenPublish)
}

version = "1.2.5"
group = "com.biotelligent"

kotlin {

    // Explicit API compiler flag, since this is a library. Switch from Warning to Strict at some point
    explicitApiWarning()

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName.set("lib")
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "lib.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
    }
    
    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.materialIconsExtended)
            implementation(libs.kotlinx.datetime)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

android {
    namespace = "io.github.aleksandar_stefanovic.composematerialdatatable"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// Refer https://vanniktech.github.io/gradle-maven-publish-plugin/what/
// ./gradlew :lib:publishToGitHubPackagesRepository
// Publish all klib and .aar types to $(HOME)/.m2/repository/com/airware/
// ./gradlew :lib:publishKotlinMultiplatformPublicationToMavenLocal
// nb. Only lowercase letters are allowed in the artifact and package urls
 mavenPublishing {
    //publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.None(),
            sourcesJar = false,
            androidVariantsToPublish = listOf("debug", "release")
        )
    )

     // ./gradlew publishAllPublicationsToGithubPackagesRepository
      publishing {
          repositories {
              maven {
                  url = uri("https://maven.pkg.github.com/Biotelligent/compose-material-data-table")
                  name = "GithubPackages"
                  credentials {
                      username = project.findProperty("GPR_USER").toString()
                      password = (project.findProperty("GPR_PAT") ?: System.getenv("GPR_PAT") ?: "").toString()
                      println("GPR_USER=$username")
                      println("GPR_PAT=$password")
                  }
              }
              mavenLocal()
          }
      }


     //signAllPublications()

    coordinates(group.toString(), "composematerialdatatable", version.toString())

    pom {
        name = "Compose Material Data Table"
        description = "A Kotlin Multiplatform and Jetpack Compose Multiplatform compliant implementation of the Material 2 Data Table."
        inceptionYear = "2025"
        url = "https://github.com/aleksandar-stefanovic/compose-material-data-table"
        licenses {
            license {
                name = "MIT License"
                url = "http://www.opensource.org/licenses/mit-license.php"
                distribution = "http://www.opensource.org/licenses/mit-license.php"
            }
        }
        developers {
            developer {
                id = "aleksandar-stefanovic"
                name = "Aleksandar Stefanović"
                url = "https://github.com/aleksandar-stefanovic"
            }
        }
//        scm {
//            url = "https://github.com/aleksandar-stefanovic/compose-material-data-table"
//            connection = "scm:git:git://github.com/aleksandar-stefanovic/compose-material-data-table.git"
//            developerConnection = "scm:git:ssh://git@github.com/aleksandar-stefanovic/compose-material-data-table.git"
//        }
    }
}
