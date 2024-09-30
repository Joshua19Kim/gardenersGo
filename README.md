# Gardener's Grove - Team 700


Gardeners Go is an application designed to help gardeners record and manage information about their gardens and plants. In this sprint, the deliverable includes features that allow new users to register, log in, and edit their profiles. Users can create and edit gardens, add plants, send friend requests, and add tags to gardens. They can also make their gardens public, view weather information for their garden's location, customise their homepage, view and filter public gardens, and follow them.

Additionally, users can explore plant information through a plant wiki, add plants from the wiki to their garden, scan plant images to identify and add plants to their collection, and view their collection on a New Zealand-based map. Users can earn badges for the plants they collect. Friends can view each other's profiles and garden details. Public gardens are also accessible, with search and filter options for gardens and plants by name and tags. When logging in for the first time, users are directed to a homepage with fun, customisable widgets, which can be adjusted through the user profile.

The project currently uses ```gradle```, ```Spring Boot```, ```Thymeleaf```, ```Bootstrap```,```WeatherAPI```, ```LocationIQ```, ```PlantNet```, ```Perenual```, ```Google Fonts/ Icons```, ```Leaflet```, ```UC OpenStreetMap```  and ```Simple Java Mail```.
```GitLab CI``` is configured to build and run the project.

This project is licensed with an MIT license. See LICENCE.txt for more details.

## Contributors
SENG302 Teaching Team\
Arthur Bell - abe118@uclive.ac.nz  
Ben Moore - bmo80@uclive.ac.nz  
Isaac Steele - ist46@uclive.ac.nz  
Jasmine Ong - jon25@uclive.ac.nz  
Joshua Byoungsoo Kim - bki42@uclive.ac.nz  
Kush Desai - kde54@uclive.ac.nz  
Rinlada Tolley - rto52@uclive.ac.nz  
Sam Dawson - sda110@uclive.ac.nz


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

spring.datasource.driverClassName=org.mariadb.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MariaDBDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.defer-datasource-initialization=true
spring.sql.init.mode=always
spring.servlet.multipart.max-file-size=-1
spring.servlet.multipart.max-request-size=-1

spring.web.resources.add-mappings=true

# API keys
email.password=${SJMP}
locationIq.password=${LOCATIONIQ}
weather.password=${WEATHER}
caching.spring.currentWeatherTTL=3600000
plantNet.password=${PLANTNET}
plantWiki.key=${PLANTWIKI}

spring.profiles.active=local

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