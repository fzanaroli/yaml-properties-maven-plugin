# Yaml-Properties Maven Plugin
***A Yaml extension of the MojoHaus Properties Maven Plugin v.1.0.1***


**Continuous Integration:**<br />
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/it.ozimov/yaml-properties-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/it.ozimov/yaml-properties-maven-plugin)
<br />
[![Build Status](https://travis-ci.org/ozimov/yaml-properties-maven-plugin.svg?branch=master)](https://travis-ci.org/ozimov/yaml-properties-maven-plugin)


## How to use it?

This extension provides capability to import content from a yaml file into your pom.
To this end, you should read the instructions for the [properties-maven-plugin](http://www.mojohaus.org/properties-maven-plugin/).

## Releasing

* Make sure `gpg-agent` is running.
* Execute `mvn -B release:prepare release:perform`

For publishing the site do the following:

```
cd target/checkout
mvn verify site site:stage scm-publish:publish-scm
```
