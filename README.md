Do you use Ruby/Rails and Java in your company? We do. And we're sick and tired of maintaining two different sets of schemas, models, and whatnot!

To that end, we've created Jack (**J**ava **AC**tive record (+**K**)). The project consists of:

- a scheme for defining and managing multiple Rails projects that contribute models
- a Ruby parser for schema.rb and ActiveRecord models that generates Java code
- a Java library that provides model-oriented database access on top of standard database connections
