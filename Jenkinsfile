@Library('liveramp-base@v2') _

mvnBuildPipeline {
    agentLabel = 'ubuntu-2004'
    environment {
        JAVA_HOME = '/usr/lib/jvm/java-11-openjdk-amd64'
    }
    mavenPropertiesOverride = '-Dmaven.test.failure.ignore=false -Ddb.user=$HUDSON_DB_USER -Ddb.pass=$HUDSON_DB_PASS -Pmysql'
}
