Project Jack
------------

[![Build Status](https://travis-ci.org/LiveRamp/jack.svg?branch=master)](https://travis-ci.org/LiveRamp/jack)

Do you use Ruby/Rails and Java in your company? We do. And we're sick and tired of maintaining two different sets of schemas, models, and whatnot!

To that end, we've created Jack (**J**ava **AC**tive record (+**K**)). The project consists of:

- a scheme for defining and managing multiple Rails projects that contribute models
- a Ruby parser for schema.rb and ActiveRecord models that generates Java code
- a Java library that provides model-oriented database access and database-wide query on top of standard database connections

Project Organization
====

A Jack project consists of two things:

1. The **project definition file**
1. One or more Rails project

### Project Definition File ###

The project definition file is a YAML file that tells Jack where to find your Rails projects and how to generate code. Here's an annotated example:

```yaml
# This is the class path where you want the top-level generated code to go.
databases_namespace: com.rapleaf.jack.test_project
# Here, we define each of the separate databases for which Jack should
# generate code. Each database is roughly equivalent to a Rails project.
databases:
  -
    # The root namespace for this database. It's nice, but not required, to
    # have this be a subpackage of the 'databases_namespace'.
    root_namespace: com.rapleaf.jack.test_project.database_1
    # What do you want to call this database? Leave out the "_production".
    db_name: Database1
    # The path to the schema.rb in your Rails project.
    schema_rb: database_1/db/schema.rb
    # The path to the app/models dir in your Rails project.
    models: database_1/app/models
    # Tables to be ignored. No code will be generated for them.
    ignored_tables:
      table_1
      table_2
  -
    root_namespace: com.rapleaf.jack.test_project.database_2
    db_name: Database2
    schema_rb: database_2/db/schema.rb
    models: database_2/app/models
```

### Rails Projects ###

Jack supports generating code for an arbitrary number of inter-related Rails 3 projects. If you only have one Rails project, then things are easy - just configure your project.yml appropriately.

If you have more than one project, here's the setup we suggest. (We use this ourselves.)

    /all_my_databases
      /project.yml 
      /rails_project_1
      /rails_project_2
    /ruby_project_that_uses_rails_project_2
      /include/rails_project_2              # <= svn external to /all_my_databases/rails_project_2


Running the Generator
====

Running the Jack generator is easy. From a fresh clone, do the following:

```sh
export PATH=$PATH:`pwd`/src/rb
```

Then, change directories to wherever your project.yml lives and run:

```sh
ruby jack.rb project.yml /path/for/generated/code
```

Assuming everything is configured correctly, that's it.

_Note: We know that the path thing stinks. We're going to improve this in a future version._

Layout of the Generated Code
====

The Java code that Jack produces is designed around interfaces so that it is very modular and mockable.

### Models ###

The generated models contain getters and setters for all the fields, as well as getters for detected associations. In contrast to ActiveRecord models, there are no CRUD methods on the Java models.

### Model Persistences ###

This is where the CRUD methods live. The generic base class supports:

- find
- find all (with and without conditions)
- finding by foreign key
- delete by id or instance
- delete all
- save (update)
- cache manipulators

while there is a unique, per-model interface and implementation that additionally provides:

- create

### Databases ###

You get one Database per database entry in your project.yml. Their main purpose is to provide a collection of all the individual model persistences. You can also execute queries across all models within each database.

### All Databases ###

Finally, there is one overarching Databases class that serves as a collection for all of the databases configured in your project.yml. Generally, this is what you will instantiate, though you can subsequently get the Database or Persistence instance you actually care about and use that.

Download
====
You can either build jack from source or pull the latest jar from the Liveramp Maven repository:

```xml
<repository>
  <id>repository.liveramp.com</id>
  <name>liveramp-repositories</name>
  <url>http://repository.liveramp.com/artifactory/liveramp-repositories</url>
</repository>
```

The 1.0-SNAPSHOT build can be retrieved there:

```xml
<dependency>
    <groupId>com.liveramp</groupId>
    <artifactId>jack</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

License
====

Copyright 2014 LiveRamp

Licensed under the Apache License, Version 2.0

http://www.apache.org/licenses/LICENSE-2.0
