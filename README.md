# gradle resolution of transitive dependencies not overrideden
I have a conflict with a transitive dependency. Overriding, excluding or forcing does not help. What else can I do to get the right version of the library into the jar?

executing
```
./gradlew shadowJar
```

with geomesa dependencies (which pull in an outdated version of typesafe/lightbend configuration library) disabled:
```
dependencies {

    compile "com.github.kxbmap:configs_2.11:0.4.4"
    //compile "org.locationtech.geomesa:geomesa-hbase-spark-runtime_2.11:2.0.1"
}
```
and executing the jar
```
java -jar build/libs/gradleThing-all.jar                         
```
outputs
```
hello
my config is: Success(Job1Configuration(frequencyCounting))
```

When enabling the dependencies:
```
./gradlew shadowJar
java -jar build/libs/gradleThing-all.jar                         
```

will output an error
```
hello
Exception in thread "main" java.lang.Exception: Failed to start. There is a problem with the configuration: Vector([extract] com.typesafe.config.Config.hasPathOrNull(Ljava/lang/String;)Z)

```
which is related to an outdated version of the config library, https://stackoverflow.com/questions/40610816/how-to-resolve-nosuchmethoderror-on-typesafe-config.
Also confirmed via:
```
gradle dependencyInsight --dependency om.typesafe:config

> Task :dependencyInsight
com.typesafe:config:1.3.1 (conflict resolution)
   variant "runtime" [
      Requested attributes not found in the selected variant:
         org.gradle.usage = java-api
   ]
\--- com.github.kxbmap:configs_2.11:0.4.4
     \--- compileClasspath

com.typesafe:config:1.2.1 -> 1.3.1
   variant "runtime" [
      Requested attributes not found in the selected variant:
         org.gradle.usage = java-api
   ]
+--- org.locationtech.geomesa:geomesa-convert-avro_2.11:2.0.1
|    \--- org.locationtech.geomesa:geomesa-convert-all_2.11:2.0.1
|         +--- org.locationtech.geomesa:geomesa-tools_2.11:2.0.1
|         |    \--- org.locationtech.geomesa:geomesa-hbase-spark-runtime_2.11:2.0.1
|         |         \--- compileClasspath
...

```
How can I fix the transitive dependency to my desired version of 1.3.3?

Trying to set:
```
compile("org.locationtech.geomesa:geomesa-hbase-spark-runtime_2.11:2.0.1") {
                exclude group: 'com.typesafe', module: 'config'
            }

```
and re running the jar fails again with the same issue.

Also an constraint https://docs.gradle.org/current/userguide/managing_transitive_dependencies.html#sec:dependency_constraints like:
```
implementation("com.typesafe:config")
    constraints {
        implementation("com.typesafe:config:1.3.3") {
            because 'previous versions miss a method https://stackoverflow.com/questions/40610816/how-to-resolve-nosuchmethoderror-on-typesafe-config'
        }
    }
```
also using force https://docs.gradle.org/current/userguide/managing_transitive_dependencies.html#sec:enforcing_dependency_version like : 
```
implementation('com.typesafe:config:1.3.3') {
        force = true
    }
```
or like:
```
configurations.all {
    resolutionStrategy {
        force 'com.typesafe:config:1.3.3'
    }
}
```
does not give the right version.