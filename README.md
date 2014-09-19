# Bumba-Velocity-Database-Helper
==============================

A lightweight API for JDBC introspection and code generation using JDBC and Apache Velocity to generate CRUD persistence objects that understand column references/foreign keys, strong data types, and primary/unique keys for fetches

This is really just uploaded as a code example.  Please don't use this as-is, because there are much better solutions out there. I've always wanted to try this :D

What does Bumba mean? Taken from Wikipedia and edited slightly:

>"The creation story of Mbombo, or Bumba, tells us that in the beginning, Mbombo was alone, and darkness
and water covered all of earth. It would happen that Mbombo came to feel an intense pain
in his stomach, and then Mbombo vomited the sun, the moon, and stars."

Like Mbombo, Bumba was an initial effort to vomit forth the incredible amount of structure needed for creating a persistence layer.  Bumba provides the tools to create typed database objects and foreign-key-aware accessors straight from an existing schema using JDBC.  Bumba isn't beautiful, but it is... expulsive.

## Features:
- Code generation based on objects from existing database schema for strongly-typed accessors
- A simple API built over the ubiquity of JDBC to provide semantically lighter access to database metadata
- Example Velocity templates included that produce messy but effective classes complete that are aware of column references, primary keys, CRUD ops, constructors, and basic but common data types.

Project is in IntelliJ and uses Apache Velocity for code generation.  Used only for PostgreSQL 9.2 w/ 9.2 connector, but any database with a JDBC driver that provides reasonably compliant metadata should do.

## BUGS:
- Tables without a primary key generate broken code
- Multiple unique keys of the same type create invalid overloaded constructors
- Example template code is coupled to generated classes.  Those should either be refactored out or included in the generated output
- Propagation of deletes and saves to sub-objects don't quite work right and are disabled for the moment (see functions.vm
- Casting code smells are baaaad
- Formatting is also baaaad

## Future features that'd be nice to have:
- Unit tests!
- Templates to output classes compatible with popular persistence frameworks (eg. JPA)
- Better logging!
- Configuration passed in by Properties file or some such
- An output configuration layer to separate database-level structure from class creation structure for cherry-picking data or relationships, and building classes without descending to templates.
- Output conformance to a less clunky design pattern, perhaps utilizing/implementing interfaces
- Style formatting automation to cleanup output files after generation (for now you have to use IDE formatting tools)
- Pruning of smelly functions in Sandbox and conversion to a legitimate usage console
- Decouple SQL strings from implementations in the example template
- Exceptions that don't suck
- Configurable package declarations!
