tasks.bootJar { enabled = false }
tasks.jar { enabled = true }

dependencies {
    implementation(project(":rouletteup-common"))

    /* JPA */
    api("org.springframework.boot:spring-boot-starter-data-jpa")

    /* DB */
    runtimeOnly("com.mysql:mysql-connector-j")
}
