plugins {
    id(BuildPlugins.androidApplication)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinAndroidExtensions)
}

val env = System.getenv()

android {
    compileSdkVersion(AndroidSdk.compile)
    buildToolsVersion = AndroidSdk.buildTools

    defaultConfig {
        applicationId = "dev.cquintana.shoppinglist"
        minSdkVersion(AndroidSdk.min)
        targetSdkVersion(AndroidSdk.target)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    signingConfigs {
        create("release") {
            storeFile = file("${rootDir.canonicalPath}/shoppinglist.keystore")
            storePassword = env["KEYSTORE_PASSWORD"]
            keyAlias = env["KEYSTORE_ALIAS"]
            keyPassword = env["KEYSTORE_KEY_PASSWORD"]
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }

        getByName("debug") {
            applicationIdSuffix = ".dev"
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(Libraries.kotlinStdLib)
    implementation(Libraries.timber)
    implementation(Libraries.recyclerView)
    implementation(Libraries.swiperefreshlayout)
    implementation(Libraries.appCompat)
    implementation(Libraries.preferences)
    implementation(Libraries.ktxCore)
    implementation(Libraries.material)
    implementation(Libraries.rxjava)
    implementation(Libraries.rxandroid)
    implementation(Libraries.retrofit)
    implementation(Libraries.retrofit_rxjava)
    implementation(Libraries.retrofit_moshi)
    implementation(Libraries.moshi)
    implementation(Libraries.moshi_kotlin)
    implementation(Libraries.okhttp)
    implementation(Libraries.okhttp_log)


    testImplementation(TestLibraries.junit4)
    androidTestImplementation(TestLibraries.extjunit)
    androidTestImplementation(TestLibraries.espresso)
}