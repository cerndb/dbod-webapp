# dbod-webapp

[![Build Status](https://travis-ci.org/cerndb/dbod-webapp.svg?branch=master)](https://travis-ci.org/cerndb/dbod-webapp)
[![Coverity Scan Build Status](https://scan.coverity.com/projects/6661/badge.svg)](https://scan.coverity.com/projects/cerndb-dbod-webapp)
## Introduction

This repository contains the Web Interface used in the CERN Database On 
Demand Service.

It's implemented in Java and using the [ZK 7.0.3](http://www.zkoss.org/) 
framework. The project uses [Maven](https://maven.apache.org/) as a project
and dependency management tool.

## Requirements

### Container

The application requires a container with support for `JDK 1.7` at least.
The recommended container to run the application is `Apache Tomcat 7`. Unexpected
behaviour occurs using lower versions.

### Authentication

The web application uses `Shibboleth` to allow people to sign in using the CERN 
user's account. The development environment should have the extension to support
this kind of authentication, or a way to overcome it.

### Database

A database with the correct structure is required. The SQL scripts are in the
resources directory of the Web Application. In principle any kind of database
is supported using the correct JDBC connection in your App Container.

### Configuration files

There is a configuration file, with is read by default from an AFS directory 
located in `/opt/dbod/webapp/dbod-web.cfg`.
The location of this file should be updated in the configLocation 
`<config-param>` section of the `web.xml` file.
There is a basic layout of the config file inside the `static` directory of 
this repository.

The file `ch.cern.dbod.util.CommonConstants` contains all the constants used
by the application, in case something more specific has to be changed.

There is also an `announcements.html` file to show a banner with information 
or alerts regarding the Web Application or the service.

The file `context.xml` should be updated with the connection details to reach
the database.

## Installation for a development environment

To generate a .war file that can be deployed in the App Container you need to
clone this repository and compile it using maven. All the dependencies should
be downloaded and installed automatically.

    $ git clone <repo_url>
    $ cd dbod-webapp
    $ mvn compile
    $ mvn package

By default, maven will try to deploy the application in a local Apache Tomcat,
you can change this configuration in the `pom.xml` file.
Now you should found the DBOnDemand-X.Y.Z.war file in the target directory.
This file can be now deployed to your App Container directly.

