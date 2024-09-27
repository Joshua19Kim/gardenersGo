# Gardener's Grove - Team 700

Gardener's Grove is an application designed to allow gardeners to record information about their garden(s) and plants 
in their garden(s). This sprint's deliverable currently allows new users to register and log in to the application, 
edit their profile, create/edit gardens and add plants to them, send friend requests to other users, 
add tags to gardens, make gardens public, view the weather for the garden location, customise the homepage, view public gardens and filter the results, follow public gardens, explore plant information in a plant wiki, add plants from wiki to a garden, scan plant pictures and add plants to a collection from scanned/ identified plants . Friends can view each other's profile and garden details. There is also the ability to browse public gardens, including searching for gardens/plants by name and filtering by tags. When you first log in you will be directed to the home page which is composed of some interesting and fun widgets, customisable from the user profile.

The project currently uses ```gradle```, ```Spring Boot```, ```Thymeleaf```, ```Bootstrap```,```WeatherAPI```, ```LocationIQ```, ```PlantNet```, ```Perenual```, ```Google Fonts/ Icons``` and ```Simple Java Mail```.
```GitLab CI``` is configured to build and run the project but for sprint 2 we request that the project not be 
evaluated on the online VM.

This project is licensed with an MIT license. See LICENCE.txt for more details.

## Contributors
SENG302 Teaching Team\
Arthur Bell - abe118@uclive.ac.nz\
Isaac Steele - ist46@uclive.ac.nz\
Jasmine Ong - jon25@uclive.ac.nz\
Rinlada Tolley - rto52@uclive.ac.nz\
Kush Desai - kde54@uclive.ac.nz\
Sam Dawson - sda110@uclive.ac.nz\
Ben Moore - bmo80.@uclive.ac.nz\
Joshua Byoungsoo Kim - bki42@uclive.ac.nz


## Dependencies
This project requires Java version >= 21, [click here to get the latest stable OpenJDK release (as of time of writing)](https://jdk.java.net/21/)

To run the project you will need Gradle, [click here and select version 8.5, and follow the instructions for install](https://gradle.org/releases/)

To run the project you will need to add environment variables to the run configuration.

## How to run
### 1 - Running the project

In order to run the project correctly for this sprint, you MUST use IntelliJ IDEA and follow the steps outlined here:

- Clone the repository to your computer and open it in IntelliJ.
- The project should build automatically, but if it doesn't, click on the gradle icon on the right sidebar and click "reload all gradle projects"
- Navigate to src\main\java\nz\ac\canterbury\seng302\gardenersgrove\GardenersGroveApplication.java and open the file
- This should add a run configuration on the top navigation bar. Select the dropdown menu and press "edit configurations"
- Under the run configuration, select "modify options" and click "environment variables"
- Click on the three lines at the end of the new box that has appeared and add new environment variables with name 
DB_USERNAME, DB_PASSWORD, SJMP, LOCATIONIQ, WEATHER, PLANTNET, PLANTWIKI and the values given in the teaching team communications chat on Mattermost
- Create an application-local.properties file in the src\main\resources folder
- Copy and paste the following to the folder:
```
spring.application.name=gardeners-grove

spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.datasource.driverClassName=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create
spring.jpa.defer-datasource-initialization=true
spring.h2.console.enabled=true
spring.h2.console.path=/h2
spring.sql.init.mode=embedded
spring.servlet.multipart.max-file-size=-1
spring.servlet.multipart.max-request-size=-1
email.password=${SJMP}
plantNet.password=${PLANTNET}
plantWiki.key = ${PLANTWIKI}
locationIq.password=${LOCATIONIQ}
weather.password=${WEATHER}
caching.spring.currentWeatherTTL=3600000
server.url=http://localhost:8080 
```

- You should now be able to run the application with the green button at the top of IntelliJ

By default, the application will run on local port 8080 [http://localhost:8080](http://localhost:8080)

### 2 - Using the application
Load up your preferred browser and connect to the application at [http://localhost:8080](http://localhost:8080). From here you must either
log in or register as a new user. When registering as a new user, you will be required to enter a signup code sent to 
your email.

Default accounts for testing:

username: a@gmail.com, password: Password1!
username: b@gmail.com, password: Password1!
username: c@gmail.com, password: Password1!
username: d@gmail.com, password: Password1!

Once logged in, a new garden can be created anywhere on the application by clicking on the "Create New Garden" button 
on the navbar at the top of the page. The list of gardens can also be viewed from anywhere by hovering over the 
"My Gardens" button, and clicking on "My Gardens" will take the user back to the home page. Clicking on a garden 
allows the user to view details about it and any plants in the garden. Garden details and plant details can be edited 
from this page, and new plants can also be added.

## How to run tests
From the root directory ...

On Linux:
```
./gradlew check
```

On Windows:
```
gradlew check
```

There are currently no built-in methods in the project for running tests with coverage.