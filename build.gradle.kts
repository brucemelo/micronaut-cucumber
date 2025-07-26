plugins {
    id("io.micronaut.application") version "4.5.4"
    id("com.gradleup.shadow") version "8.3.7"
}

repositories {
    mavenCentral()
}

val cucumberVersion = "7.23.0"

dependencies {
    annotationProcessor("io.micronaut.data:micronaut-data-processor")
    annotationProcessor("io.micronaut:micronaut-http-validation")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    annotationProcessor("org.hibernate.orm:hibernate-jpamodelgen")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut.sourcegen:micronaut-sourcegen-annotations")
    annotationProcessor("io.micronaut.sourcegen:micronaut-sourcegen-generator-java")

    compileOnly("io.micronaut:micronaut-http-client")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("com.h2database:h2")
    testImplementation("io.cucumber:cucumber-java:$cucumberVersion")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:$cucumberVersion")
    testImplementation("io.micronaut:micronaut-http-client")
    testImplementation("org.junit.platform:junit-platform-suite")
    testImplementation("org.mockito:mockito-core:5.10.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.10.0")
}

application {
    mainClass = "com.brucemelo.Application"
}
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.brucemelo.*")
    }
}

tasks.test {
    useJUnitPlatform()
    systemProperty("cucumber.publish.quiet", "true")

    doLast {
        println("Cucumber reports generated:")
        println("- HTML Report: ${project.file("build/cucumber-reports/report.html").absolutePath}")
        println("- Non-Technical HTML Report: ${project.file("build/cucumber-reports/non-technical-report.html").absolutePath}")
    }
}


tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("dockerfileNative") {
    jdkVersion = JavaVersion.VERSION_21.majorVersion
}
