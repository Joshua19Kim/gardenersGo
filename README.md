# SENG302 Template Project Overview
This project, Gardeners Grove, is an application targeted towards gardeners. The application allows gardeners to engage with the wider community or the environment in general. Gardener’s Grove is designed to make the life of gardeners better by recording the different plants in their garden, find information on ways to best care for plants, access historical and upcoming weather data. Gardeners can interact with the wider community by sharing images of their garden, offering advice on forums, giving away or selling plants and produce to those in their community.

## Authors
- SENG302 teaching team
- Kush Desai
- Benjamin Moore
- Sam Dawson
- Joshua Kim

## Dependencies
This project requires Java version >= 21, [click here to get the latest stable OpenJDK release (as of time of writing)](https://jdk.java.net/21/)

This projects requires Spring Security ( add implementation "org.springframework.boot:spring-boot-starter-security" in build.gradle)


## Technologies
This project makes use of several technologies that you will have to work with. Here are some helpful links to documentation/resources for the big one:

- [Spring Boot](https://spring.io/projects/spring-boot) - Used to provide http server functionality
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa) - Used to implement JPA (Java Persistence API) repositories
- [h2](https://www.h2database.com/html/main.html) - Used as an SQL JDBC and embedded database
- [Thymeleaf](https://www.thymeleaf.org/) - A templating engine to render HTML on the server, as opposed to a separate client-side application (such as React)
- [Gradle](https://gradle.org/) - A build tool that greatly simplifies getting application up and running, even managing our dependencies (for those who did SENG202, you can think of Gradle as a Maven replacement)
- [Spring Boot Gradle Plugin](https://docs.spring.io/spring-boot/docs/3.0.2/gradle-plugin/reference/html/) - Allows us to more easily integrate our Spring Boot application with Gradle
- [Spring Security](https://spring.io/projects/spring-security) - Allows us to provide authentication and authorization to our application for users


## Quickstart Guide

### Building and running the project with Gradle
We'll give some steps here for building and running via the commandline, though IDEs such as IntelliJ will typically 
have a 'gradle' tab somewhere that you can use to perform the same actions with as well. 

#### 1 - Running the project
From the root directory ...

On Linux:
```
./gradlew bootRun
```

On Windows:
```
gradlew bootRun
```

By default, the application will run on local port 8080 ('http://localhost:8080')

#### 2 - Connecting to the UI from your browser
Everything should now be up and running, so you can load up your preferred browser and connect to the application at 
[http://localhost:8080](http://localhost:8080). We have set up some example pages you can reach from the application 
home '/', we suggest you have a play around with these to understand the basics of the new technologies involved.

#### 3 - What's included to play with
Routes implemented:\
/ \
/login \
/register \
/user (need to be logged in to access) \
/main

This template project includes basic users to test the application.\
User 1 - email: a@gmail.com, password: Password1!\
User 2 - email: b@gmail.com, password: Password1!\
User 3 - email: c@gmail.com, password: Password1!\
User 4 - email: d@gmail.com, password: Password1!

#### 4 - Commands to run test suites:

On Linux:
```
./gradlew test
```

On Windows:
```
gradlew test
```
