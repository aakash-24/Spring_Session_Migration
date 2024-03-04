# Overview

The HTTP Session Storage Switcher is a flexible Java application that allows you to 
switch between different storage options for HTTP sessions.
You can choose between Redis, MongoDB, and JDBC as your primary and secondary storage options. This project aims to provide developers with a customizable solution for managing HTTP sessions in their applications.

# Features
1. Supports multiple storage options: Redis, MongoDB, and JDBC.

2. Allows configuration of primary and secondary storage in application.properties.

3. Open-source and customizable for integration with various Java applications.

# Getting Started
1. Java Development Kit (JDK) version 17 or higher
2. Maven

# Installation
1. Clone the repository
2. Navigate to the project directory
3. Build the project using Maven

# Configuration
1. Open the application.properties file located in the src/main/resources directory.
2. Configure your primary and secondary storage options by setting the following properties:
    1. Primary storage option (choose one: redis, mongo, jdbc)
        1. spring.session.primary_storage=REDIS 
    2. Secondary storage option (choose one: redis, mongo, jdbc)
        1. spring.session.secondary_storage=MONGO
3. Provide additional configuration properties for Redis, MongoDB, and JDBC connections.
4. We can also disable the secondary storage by enabling it to false.
        1. spring.session.secondary_storageIsEnabled=FALSE
