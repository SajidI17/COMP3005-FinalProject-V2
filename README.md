# COMP3005-FinalProject-V2
This is a Maven project for the COMP3005 V2 Project 

# Pre-req Software
- Before running, ensure you have JDK-17 installed on your machine

# Setup 
1. Clone the repo locally
2. Create a database called "HealthFitnessDatabase" PostgreSQL (or modify the URL in DatabaseInterface.java with a name of your choosing)
3. Create the tables using the sql file located under sql/DDL.sql
4. Insert data into the tables using the sql file located under sql/DML.sql
5. Using IntelliJ, Go to File > Open
6. Select the pom.xml, click OK.
7. In the dialog that opens, click Open as Project.
8. Inside src/main/java/DatabaseInterface.java, modify the URL, USERNAME, and PASSWORD variables as needed
```
private static final String URL = "jdbc:postgresql://localhost:5432/HealthFitnessDatabase"; //Change HealthFitnessDatabase to the name of the database you have
private static final String USERNAME = "postgres";
private static final String PASSWORD = "admin";
```
9. Build and run the src/main/java/DatabaseInterface.java inside IntelliJ

# Video Demo (Youtube Link)
https://www.youtube.com/watch?v=8H9wfNG2-Hk

# Files
Several files related to the project can be found in this repository
- DDL.sql and DML.sql files can be found under the sql folder
- Conceptual Design and Relational Schema Diagrams can be found under the diagrams folder
- A PDF (Assumptions.PDF) containing assumptions made for the project can be found under the diagrams folder
- Source code is under src/main/java/DatabaseInterface.java

