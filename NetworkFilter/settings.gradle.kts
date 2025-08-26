pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        maven { url = uri("https://jitpack.io") }
        maven {
            isAllowInsecureProtocol = true
            url = uri("http://nexus-pub.abupota.com/nexus/content/repositories/releases/")
        }
        maven { url = uri("https://maven.aliyun.com/repository/jcenter") }
    }
}

rootProject.name = "NetworkFilter"
include(":app")
 