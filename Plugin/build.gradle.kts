// Plugin 3D pour Logisim Evolution
// Fichier de build Gradle

plugins {
    id("java-library")
    id("application")
}

group = "com.cburch.logisim"
version = "1.0.0"

repositories {
    mavenCentral()
    google()
}

dependencies {
    // Logisim Evolution (local)
    implementation(files("../logisim-evolution/build/libs/logisim-evolution.jar"))
    
    // JMonkeyEngine pour 3D
    implementation("org.jmonkeyengine:jme3-core:3.6.0")
    implementation("org.jmonkeyengine:jme3-desktop:3.6.0")
    implementation("org.jmonkeyengine:jme3-lwjgl:3.6.0")
    
    // JSON
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    
    // Logging
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("org.slf4j:slf4j-simple:2.0.7")
    
    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass.set("com.cburch.logisim.plugin3d.Plugin3D")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.test {
    useJUnitPlatform()
}

tasks.named<Jar>("jar") {
    manifest {
        attributes["Implementation-Title"] = "Logisim 3D Plugin"
        attributes["Implementation-Version"] = version
    }
}
