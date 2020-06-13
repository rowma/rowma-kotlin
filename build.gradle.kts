import org.gradle.jvm.tasks.Jar
import com.jfrog.bintray.gradle.BintrayExtension

group = "com.rowma.rowma-kotlin"
version = "0.0.20"
val artifactID = "rowma-kotlin"
val publicationName = "default"

plugins {
  `maven-publish`
  kotlin("jvm") version "1.3.70"
  id("com.jfrog.bintray") version "1.8.5"
}

repositories {
  jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.socket:socket.io-client:1.0.0") {
        exclude(group = "org.json", module = "json")
    }
    implementation("org.json:json:20200518")
}

val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}

publishing {
  publications {
    create<MavenPublication>("default") {
      from(components["java"])
    }
  }

  publications.invoke {
    publicationName(MavenPublication::class) {
      artifactId = artifactID
      artifact(sourcesJar.get())
    }
  }
}

fun findProperty(s: String) = project.findProperty(s) as String?
bintray {
    user = findProperty("bintrayUser")
    key = findProperty("bintrayApiKey")
    publish = true
    setPublications(publicationName)
    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = "rowma"
        name = "rowma-kotlin"
        userOrg = "asmsuechan"
        websiteUrl = "https://github.com"
        githubRepo = "rowma/rowma-kotlin"
        vcsUrl = "https://github.com/rowma/rowma-kotlin"
        description = ""
        setLabels("kotlin")
        setLicenses("MIT")
        desc = description
    })
}