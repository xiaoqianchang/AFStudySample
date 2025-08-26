import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties
import java.util.TimeZone

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

fun gitBranchName(): String {
    return try {
        val process = ProcessBuilder("git", "symbolic-ref", "--short", "-q", "HEAD")
            .redirectError(ProcessBuilder.Redirect.DISCARD) // 忽略错误输出
            .start()

        process.waitFor()

        if (process.exitValue() == 0) {
            process.inputStream.bufferedReader().use { it.readText().trim() }
        } else {
            // 尝试获取提交哈希作为备选
            getGitCommitHash().take(7)
        }
    } catch (e: Exception) {
        "unknown"
    }
}

fun getGitCommitHash(): String {
    return try {
        val process = ProcessBuilder("git", "rev-parse", "--short", "HEAD")
            .redirectError(ProcessBuilder.Redirect.DISCARD)
            .start()

        process.waitFor()

        if (process.exitValue() == 0) {
            process.inputStream.bufferedReader().use { it.readText().trim() }
        } else {
            "unknown"
        }
    } catch (e: Exception) {
        "unknown"
    }
}

fun buildTime(): String {
    val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss")
    dateFormat.timeZone = TimeZone.getTimeZone("GMT+08")
    return dateFormat.format(Date())
}

fun compileId() = "${gitBranchName()}_${buildTime()}"

// 加载 keystore.properties 文件
val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("signature/keystore.properties")
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
} else {
    logger.warn("Keystore properties file not found: keystore.properties")
}

android {
    namespace = "com.chang.nf.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.chang.nf.android"
        minSdk = 26
        targetSdk = 35
        versionCode = 10000
        versionName = "1.0.0"

        buildConfigField("String", "COMPILE_ID", "\"" + compileId() + "\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("config") {
            storeFile = file(keystoreProperties.getProperty("storeFile"))
            storePassword = keystoreProperties.getProperty("storePassword")
            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword = keystoreProperties.getProperty("keyPassword")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("config")
        }
        debug {
            isMinifyEnabled = false
            versionNameSuffix = "dev"
            signingConfig = signingConfigs.getByName("config")
        }
    }

    applicationVariants.all {
        outputs.all {
            val outputFileName = "NetworkFilter-" +
                    "V${versionName}-" +
                    "${versionCode}-" +
                    "${name}-" +
                    "${buildTime()}" +
                    ".apk"
            (this as BaseVariantOutputImpl).outputFileName = outputFileName
        }
    }

    sourceSets {
        named("main") { // 或者用 getByName
            jni.setSrcDirs(emptyList<String>())
            jniLibs.setSrcDirs(listOf("libs"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
//        compose = true
        buildConfig = true
        viewBinding = true
        dataBinding = true
    }
    packagingOptions {
        exclude("META-INF/INDEX.LIST")
        exclude("META-INF/io.netty.versions.properties")
        doNotStrip("*/arm64-v8a/*.so")

        // 其他可能的 packagingOptions 配置
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("com.google.code.gson:gson:2.10.1")
//    implementation("com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.6")
//    implementation("com.github.bumptech.glide:glide:4.16.0")
//    implementation("io.github.youth5201314:banner:2.2.3")
//    implementation("io.github.scwang90:refresh-layout-kernel:2.1.0")
//    implementation("io.github.scwang90:refresh-header-classics:2.1.0")
}