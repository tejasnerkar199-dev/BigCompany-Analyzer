## Employee Analyzer is a Java application for managing and analyzing employee data.
## It allows you to:

- Load employee data from CSV files.
- Build reporting hierarchies.
- Identify managers earning too much or too little relative to their team.
- Check if reporting lines exceed allowed depth.

## Technologies
- Java 24+
- Maven (or Gradle)
- JUnit 5 for unit testing
- Mockito for mocking static methods

## Build the project
 mvn clean install

## Run the application
mvn exec:java -Dexec.mainClass="com.bigcompany.analyzer.Main"

## Test application
mvn test


