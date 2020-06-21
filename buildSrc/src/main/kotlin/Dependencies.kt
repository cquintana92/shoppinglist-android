const val kotlinVersion = "1.3.72"

object BuildPlugins {

    object Versions {
        const val buildToolsVersion = "4.0.0"
        const val gmsVersion = "4.3.3"
    }

    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.buildToolsVersion}"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    const val gmsPlugin = "com.google.gms:google-services:${Versions.gmsVersion}"
    const val androidApplication = "com.android.application"
    const val kotlinAndroid = "kotlin-android"
    const val kotlinAndroidExtensions = "kotlin-android-extensions"

}

object AndroidSdk {
    const val min = 21
    const val compile = 29
    const val target = compile
    const val buildTools = "29.0.2"
}


object Libraries {
    private object Versions {
        const val jetpack = "1.1.0"
        const val ktx = "1.3.0"
        const val timber = "4.7.1"
        const val rxjava  = "3.0.0"
        const val material = "1.2.0-beta01"
        const val retrofit = "2.7.2"
        const val okhttp = "4.4.0"
        const val moshi = "1.9.2"
        const val swiperefreshlayout = "1.0.0"

    }

    const val kotlinStdLib       = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"
    const val recyclerView       = "androidx.recyclerview:recyclerview:${Versions.jetpack}"
    const val appCompat          = "androidx.appcompat:appcompat:${Versions.jetpack}"
    const val preferences        = "androidx.preference:preference-ktx:${Versions.jetpack}"
    const val swiperefreshlayout = "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.swiperefreshlayout}"
    const val ktxCore            = "androidx.core:core-ktx:${Versions.ktx}"
    const val material           = "com.google.android.material:material:${Versions.material}"
    const val timber             = "com.jakewharton.timber:timber:${Versions.timber}"
    const val rxandroid          = "io.reactivex.rxjava3:rxandroid:${Versions.rxjava}"
    const val rxjava             = "io.reactivex.rxjava3:rxjava:${Versions.rxjava}"
    const val retrofit           = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"

    const val retrofit_rxjava    = "com.github.akarnokd:rxjava3-retrofit-adapter:${Versions.rxjava}"
    const val retrofit_moshi     = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"
    const val moshi              = "com.squareup.moshi:moshi:${Versions.moshi}"
    const val moshi_kotlin       = "com.squareup.moshi:moshi-kotlin:${Versions.moshi}"
    const val okhttp             = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val okhttp_log         = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"

}

object TestLibraries {
    private object Versions {
        const val junit4 = "4.12"
        const val testRunner = "1.1.1"
        const val espresso = "3.2.0"
    }
    const val junit4     = "junit:junit:${Versions.junit4}"
    const val extjunit   = "androidx.test.ext:junit:${Versions.testRunner}"
    const val testRunner = "androidx.test:runner:${Versions.testRunner}"
    const val espresso   = "androidx.test.espresso:espresso-core:${Versions.espresso}"
}
