
Apache Avro integration
-----------------------

Plugin page: [http://artifacts.griffon-framework.org/plugin/avro](http://artifacts.griffon-framework.org/plugin/avro)


Provides integration with [Apache Avro][1] - a data serialization system used by Apache Hadoop.

Usage
-----
Place your `.apvr` and/or `.apsc` sources at `$basedir/src/avro`. They will be automatically compiled to java sources
(and classes) whenever the application is compiled. Alternatively you may call the `avro` command to compile avro
sources at any time.

Configuration
-------------

Avro version currently supported is **1.6.1**.

Scripts
-------

 * **avro** - compiles avro sources (.avro) then compiles the generated java sources.

[1]: http://wiki.apache.org/hadoop/Avro

