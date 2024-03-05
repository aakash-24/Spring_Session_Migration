# Overview

The HTTP Session Storage Switcher is a Java application designed to give developers flexibility in how they manage HTTP sessions in their applications. HTTP sessions are like temporary storage areas for user data while they interact with a website or web application.

This tool allows developers to easily switch between different storage options for these HTTP sessions. The available options include Redis, MongoDB, and JDBC. Each of these options has its own advantages and may be better suited for different types of applications or use cases.

For example:

1. Redis is known for its speed and ability to handle large amounts of data efficiently.
2. MongoDB is a document-oriented database that offers flexibility and scalability.
3. JDBC provides a more traditional approach using relational databases like MySQL or PostgreSQL.
   
By allowing developers to choose between these options, the HTTP Session Storage Switcher aims to provide a customizable solution that can be tailored to the specific needs of each application. This flexibility ensures that developers can optimize performance and scalability while managing HTTP sessions effectively.


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

