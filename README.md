# Jack

[![Build Status](https://travis-ci.com/LiveRamp/jack.svg?branch=master)](https://travis-ci.com/LiveRamp/jack)

Do you use Ruby/Rails and Java in your company? We do. And we're sick and tired of maintaining two different sets of schemas, models, and whatnot!

To that end, we've created Jack (**J**ava **AC**tive record (+**K**)). The project consists of:

- a scheme for defining and managing multiple Rails projects that contribute models
- a Ruby parser for schema.rb and ActiveRecord models that generates Java code
- a Java library that provides model-oriented database access and database-wide query on top of standard database connections

## Project Organization

A Jack project consists of two things:

1. The **project definition file**
1. One or more Rails project

### Project Definition File

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

### Rails Projects

Jack supports generating code for an arbitrary number of inter-related Rails 3 projects. If you only have one Rails project, then things are easy - just configure your project.yml appropriately.

If you have more than one project, here's the setup we suggest. (We use this ourselves.)

    /all_my_databases
      /project.yml
      /rails_project_1
      /rails_project_2
    /ruby_project_that_uses_rails_project_2
      /include/rails_project_2              # <= svn external to /all_my_databases/rails_project_2

### Running the Generator

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

### Schema-dialect parser specs

The parser accepts schema.rb in both the Rails 4.2 dump format and the modern (Rails 5+/7.1)
format, normalizing the modern forms so both produce identical parse state — and therefore
byte-identical generated Java, including `serialVersionUID`s. The specs for this live in
`jack-test/test/rb/` and run as part of the jack-test Maven build (`run-parser-specs`
execution, test phase). To run them directly:

```sh
cd jack-test
bundle install   # Ruby 2.7.x
bundle exec rspec test/rb        # dialect-equivalence, per-rule variants, UID recipe pin
bash test/diff_rails71.sh        # manual only: full generation from both dialects, byte-diffed
```

**What diff_rails71.sh checks.** It generates the full Java model layer twice — once from the
4.2-format fixture, once from the 7.1-format fixture — and diffs the two outputs. That diff is
deterministic and must always be empty: same schema meaning, same bytes out. The script also
diffs against the committed golden `test/java` as a sanity check. Unfortunately, that comparison is
somewhat dependent on the machine running it: the existing fixture schema (unlike the real rldb schema)
has datetime columns with literal defaults, and the generator converts those to epoch milliseconds
in **the machine's local timezone**. We kept this behavior to minimize the blast radius of the ActiveRecord upgrade.
Since the fixtures were committed from a PST machine, any time this script is run in a machine that's elsewhere,
the diff will show different epoch constants. That is a fixture-only quirk, but also makes it inappropriate
to include this script in CI.

**What the UID recipe spec pins.** Java serialization stamps every class with a
`serialVersionUID`; when one JVM deserializes bytes another JVM wrote (Spark/Hadoop shuffles,
caches — anything crossing a process or deploy boundary), the stamps must match or Java throws
`InvalidClassException`. Jack computes that stamp for each generated model from the schema
itself — a hash over every column's name, type, position, and options. The spec hard-codes one known stamp and
re-derives it. If it fails, we know that the derivation method itself has changed, meaning that every generated
model gets a new stamp, even if the schema has not changed. This would break any consumer that holds serialized models across a deploy.
The correct fix is most likely to revert the change in serialization method, not to update the constant in the spec.

### Layout of the Generated Code

The Java code that Jack produces is designed around interfaces so that it is very modular and mockable.

#### Models

The generated models contain getters and setters for all the fields, as well as getters for detected associations. In contrast to ActiveRecord models, there are no CRUD methods on the Java models.

#### Model Persistences

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

#### Databases

You get one Database per database entry in your project.yml. Their main purpose is to provide a collection of all the individual model persistences. You can also execute queries across all models within each database.

#### All Databases

Finally, there is one overarching Databases class that serves as a collection for all of the databases configured in your project.yml. Generally, this is what you will instantiate, though you can subsequently get the Database or Persistence instance you actually care about and use that.

## Download

You can [find releases] on The Central Repository and [find snapshots] on
[Sonatype OSSRH] (OSS Repository Hosting).

To get snapshots, add the OSSRH snapshot repository.  See the [guide to using
multiple repositories].

```xml
<repository>
  <id>ossrh-snapshots</id>
  <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
  <releases>
    <enabled>false</enabled>
  </releases>
</repository>
```

[find releases]: https://search.maven.org/search?q=g:com.liveramp%20a:jack-*
[find snapshots]: https://oss.sonatype.org/#nexus-search;gav~com.liveramp~jack*~~~
[guide to using multiple repositories]: https://maven.apache.org/guides/mini/guide-multiple-repositories.html
[Sonatype OSSRH]: https://central.sonatype.org/pages/ossrh-guide.html

## License

Copyright 2014 LiveRamp

Licensed under the Apache License, Version 2.0

http://www.apache.org/licenses/LICENSE-2.0
