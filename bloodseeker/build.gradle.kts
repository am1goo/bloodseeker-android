plugins {
    id("com.android.library")
}

android {
    signingConfigs {
        create("release") {
        }
    }
    namespace = "com.am1goo.bloodseeker.android"
    compileSdk = 34

    defaultConfig {
        minSdk = 17

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        resourceConfigurations += setOf()
    }

    buildTypes {
        debug {
            multiDexEnabled = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    ndkVersion = "23.1.7779620"
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation(files("libs/axml-2.1.3.jar"))
    implementation(files("libs/slf4j-api-1.7.5.jar"))
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}