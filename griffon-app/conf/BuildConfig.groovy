griffon.project.dependency.resolution = {
    inherits "global"
    log "warn"
    repositories {
        mavenCentral()
        mavenRepo 'https://repository.jboss.org/nexus/content/groups/public-jboss'
        mavenRepo 'https://nexus.codehaus.org/content/repositories/releases'
    }
    dependencies {
        String avroVersion = '1.6.1'
        build "org.apache.avro:avro:$avroVersion",
              "org.apache.avro:avro-tools:$avroVersion",
              'com.thoughtworks.paranamer:paranamer:2.4.1',
              'com.thoughtworks.paranamer:paranamer-ant:2.4.1'
              'org.codehaus.jackson:jackson-mapper-asl:1.9.4'
        compile("org.apache.avro:avro:$avroVersion") { transitive = false }
    }
}

griffon {
    doc {
        logo = '<a href="http://griffon.codehaus.org" target="_blank"><img alt="The Griffon Framework" src="../img/griffon.png" border="0"/></a>'
        sponsorLogo = "<br/>"
        footer = "<br/><br/>Made with Griffon (@griffon.version@)"
    }
}

log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    appenders {
        console name: 'stdout', layout: pattern(conversionPattern: '%d [%t] %-5p %c - %m%n')
    }

    error 'org.codehaus.griffon',
          'org.springframework',
          'org.apache.karaf',
          'groovyx.net'
    warn  'griffon'
}