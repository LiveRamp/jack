@Library('liveramp-base@v2') _

mvnBuildPipeline {
    agentLabel = 'ubuntu-2004'
    environment {
        JAVA_HOME = '/usr/lib/jvm/java-8-openjdk-amd64'
    }
    mavenPropertiesOverride = '-Dmaven.test.failure.ignore=false'
}
