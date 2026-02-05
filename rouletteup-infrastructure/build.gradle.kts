tasks.bootJar { enabled = false }
tasks.jar { enabled = true }

dependencies {
    implementation(project(":rouletteup-common"))

    /* Swagger */
    api("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.13")
}
