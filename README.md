# Background
What is Spring Sessions?

A session refers to an HTTP session, which is a mechanism used to maintain state and store data between multiple requests and responses in a web application. The Spring Framework provides tools and components for managing sessions effectively. Spring Session  provides support for managing and integrating HttpSession data with various data stores.

# Challenges 
By default, Spring Session supports only one Database for eg - Redis, JDBC, Gemfire etc. But the problem is if the DB gets down we can not retrieve the sessions again.

# Solutions
We have implemented multiple storage for sessions for the following reasons :
1. Fault Tolerance - If the primary storage becomes unavailable due to a failure or outage, the secondary storage can serve as a backup, ensuring continued availability of session data. This redundancy improves fault tolerance and reduces the risk of downtime.
2. Improved Scalability - By distributing session data across multiple storage mechanisms, you can better handle increases in load and traffic. 
3. Load Balancing - Utilizing both primary and secondary storage allows for load balancing between the two storage systems.
4. Disaster Recovery: In the event of a catastrophic failure or data loss in the primary storage, having session data replicated in the secondary storage facilitates quicker recovery and restoration of services. This is crucial for maintaining business continuity and minimizing downtime.

This library is based on the Spring Http Sessions which provide the feature to change the Spring Http Session storage by changing the Spring Session Storage on the basis of property configuration.

This library allows developers to easily switch between different storage options for these HTTP sessions. The available options include Redis, MongoDB, and JDBC.


# Features
1. Supports multiple storage options: Redis, MongoDB, and JDBC. With just a property change you can switch between the storage without losing existing user sessions.

2. Allows configuration of primary and secondary storage in application properties.

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
    1. Primary storage option (choose one: redis, mongo, RDBMS)
        1. spring.session.primary_storage.name=REDIS 
    2. Secondary storage option (choose one: redis, mongo, RDBMS)
        1. spring.session.secondary_storage.name=MONGO
3. Provide additional configuration properties for Redis, MongoDB, and Rdbms connections.
4. We can also disable the secondary storage by enabling it to be false.
    1. spring.session.secondary_storage.enabled=FALSE
5. If we don't want to use RDBMS as a storage option in our setup we have to exclude the datasource of the Jdbc 
    1. spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration 

