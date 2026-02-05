tasks.bootJar { enabled = false }
tasks.jar { enabled = true }

dependencies {
    implementation(project(":rouletteup-common"))

    /* Swagger */
    api("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.13")

    /* Cache */
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine")
}
