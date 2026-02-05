tasks.bootJar { enabled = false }
tasks.jar { enabled = true }

dependencies {
    compileOnly("org.springframework.boot:spring-boot-starter-web")
}