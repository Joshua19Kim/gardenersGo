# Gardener's Grove - Team N

Gardener's Grove is an application designed to allow gardeners to record information about their garden(s) and plants 
in their garden(s). This sprint's deliverable currently allows the creation of new gardens, and the addition of plants 
to gardens.

The project currently uses ```gradle```, ```Spring Boot```, ```Thymeleaf```, and will take advantage of ```GitLab CI``` 
in future sprints.

## Contributors
SENG302 teaching team\
Arthur Bell - abe118@uclive.ac.nz\
Isaac Steele - ist46@uclive.ac.nz\
Jasmine Ong - jon25@uclive.ac.nz\
Rinlada Tolley - rto52@uclive.ac.nz\
Kush Desai - kde54@uclive.ac.nz\
Sam Dawson - sda110@uclive.ac.nz\
Ben Moore - BenMoore1.work@gmail.com\
Joshua Byoungsoo Kim - bki42@uclive.ac.nz


## Dependencies
This project requires Java version >= 21, [click here to get the latest stable OpenJDK release (as of time of writing)](https://jdk.java.net/21/)\
To run the project from the command line you will need Gradle, [click here and select version 8.5, and follow the instructions for install](https://jdk.java.net/21/)

## How to run
### 1 - Running the project
From the root directory ...

On Linux:
```
./gradlew bootRun
```

On Windows:
```
gradlew bootRun
```

By default, the application will run on local port 8080 [http://localhost:8080](http://localhost:8080)

### 2 - Using the application
Load up your preferred browser and connect to the application at [http://localhost:8080](http://localhost:8080). You will be redirected 
to the home page which contains 11 pre-loaded gardens. A new garden can be created anywhere on the application by 
clicking on the "Create New Garden" button on the navbar at the top of the page. The list of gardens can also be viewed 
from anywhere by hovering over the "My Gardens" button, and clicking on "My Gardens" will take the user back to the 
home page. Clicking on a garden allows the user to view details about it and any plants in the garden. Garden 
details and plant details can be edited from this page, and new plants can also be added.

## How to run tests
From the root directory ...

On Linux:
```
./gradlew test
```

On Windows:
```
gradlew test
```

There are currently no built-in methods in the project for running tests with coverage.
