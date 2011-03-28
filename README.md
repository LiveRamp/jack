Project Jack
------------

Do you use Ruby/Rails and Java in your company? We do. And we're sick and tired of maintaining two different sets of schemas, models, and whatnot!

To that end, we've created Jack (**J**ava **AC**tive record (+**K**)). The project consists of:

- a scheme for defining and managing multiple Rails projects that contribute models
- a Ruby parser for schema.rb and ActiveRecord models that generates Java code
- a Java library that provides model-oriented database access on top of standard database connections

Project Organization
====

A Jack project consists of two things:

1. The **project definition file**
1. One or more Rails project

### Project Definition File ###

The project definition file is a YAML file that tells Jack where to find your Rails projects and how to generate code. Here's an annotated example:

    ---
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
      -
        root_namespace: com.rapleaf.jack.test_project.database_2
        db_name: Database2
        schema_rb: database_2/db/schema.rb
        models: database_2/app/models

### Rails Projects ###

Running the Generator
====

Layout of the Generated Code
====