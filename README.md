# AskMe System

The **AskMe System** is a Java-based console application that mimics an anonymous Q&A platform where users can post and answer questions anonymously. The system features question handling and database interactions, making it ideal for practicing object-oriented design and database management.

## Features

### User Features:

* **Ask and Answer Questions**: Users can post questions to other users or answer questions directed to them.
* **Anonymous Mode**: Users can ask questions anonymously to hide their identity.
* **Feed Management**: Users can browse through the feed to see all available questions or filter questions specifically directed to them.
* **Question Management**: Users can delete their own questions.

## Technologies Used:

* **Java 17**: Core programming language.
* **HikariCP**: Connection pooling to manage database connections efficiently.
* **MariaDB/MySQL**: For persistent storage of users and questions.
* **Maven**: Dependency management and build automation.

## Running the Application

To run the AskMe System without an IDE, follow these steps:

1. Ensure you have Java Runtime Environment (JRE) installed on your machine. You can download it from [Oracle's official website](https://www.oracle.com/java/technologies/javase-jre8-downloads.html) or use OpenJDK.

2. Clone the repository: 
   ```bash
   git clone https://github.com/Ma-Eltohamy/AskMe
   ```
3. Navigate to the project directory:
   ```bash
   cd AskMe/AskMe-Application
   ```
4. Set your database connection properties
   ```md
   use your favorite IDE to change database properties inside

   ./src/main/java/com/AskMe/Database.java
   ```
   ```bash
   dbUrl = "jdbc:mariadb://localhost:3306/";
   dbUserName = "Your Database Username";
   dbPassword = "Your Database Password";
   ```

5. Build the project using Maven:
   ```bash
   mvn clean package
   ```
6. Run the application using the following command:
   ```bash
   java -jar target/AskMe-1.0.jar
   ```
