tasks.bootJar { enabled = true }
tasks.jar { enabled = false }

dependencies {
    implementation(project(":rouletteup-common"))
    implementation(project(":rouletteup-domain"))

    /* Spring */
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
}