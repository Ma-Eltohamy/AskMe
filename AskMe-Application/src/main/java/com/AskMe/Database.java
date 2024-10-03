package com.AskMe;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import org.mariadb.jdbc.Statement;
import org.mindrot.jbcrypt.BCrypt;

public class Database {
  private static final String dbName = "ask_me";
  private static final String dbUrl = "jdbc:mariadb://localhost:3306/";
  private static final String dbUserName = "Your Database Username";
  private static final String dbPassword = "Your Database Password";
  private static String query;
  private static Connection connection;

  public static String jbCrypt(String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt());
  }

  public static void checkDatabase() {
    try (HikariDataSource ds = new HikariDataSource()) {
      ds.setJdbcUrl(dbUrl);
      ds.setUsername(dbUserName);
      ds.setPassword(dbPassword);

      connection = ds.getConnection();

      query = "show databases like '" + dbName + "'";
      try (PreparedStatement statement = connection.prepareStatement(query)) {
        ResultSet result = statement.executeQuery();

        if (!result.next())
          createDatabase(connection);
        else
          System.out.println("'" + dbName + "' Database already exists.");
      } catch (Exception e) {
        System.out.println("Error, While showing the databases table." +
                           e.getMessage());
      }
    } catch (Exception e) {
      System.out.println(
          "Error, While HikariDataSource making the connection. " +
          e.getMessage());
    }
  }

  private static void createDatabase(Connection connection) {
    System.out.println("==> Database not found.");
    System.out.println("Creating a database with name: " + dbName + "...");

    query = "create database " + dbName;
    try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.execute();
      System.out.println(dbName + " has been created successfully.");
    } catch (Exception e) {
      System.out.println("Failed to create the database --> " + dbName);
      System.out.println("Error, While creating the database. " +
                         e.getMessage());
    }
  }

  public static void checkUsersTable() {
    try (HikariDataSource ds = new HikariDataSource()) {
      ds.setJdbcUrl(dbUrl + dbName);
      ds.setUsername(dbUserName);
      ds.setPassword(dbPassword);

      connection = ds.getConnection();
      query = "show tables like 'users'";
      try (PreparedStatement statement = connection.prepareStatement(query)) {
        ResultSet result = statement.executeQuery();

        if (!result.next())
          createUsersTable(connection);
        else
          System.out.println("'users' table already exists.");

      } catch (SQLException e) {
        System.out.println(
            "Error, While checking the databases table 'users' ." +
            e.getMessage());
        ds.close();
      }
    } catch (SQLException e) {
      System.out.println(
          "Error, While HikariDataSource making the connection. " +
          e.getMessage());
    }
  }

  private static void createUsersTable(Connection connection) {
    System.out.println("==> 'users' table not found.");
    System.out.println("Creating the 'users' table...");
    query = "" "
        CREATE TABLE
        users(id INT PRIMARY KEY AUTO_INCREMENT,
              username VARCHAR(255) UNIQUE NOT NULL,
              password VARCHAR(255) NOT NULL, name varchar(255) not null,
              email VARCHAR(255) UNIQUE NOT NULL,
              allow_anonymous_questions BOOLEAN NOT NULL) "" ";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.execute();
      System.out.println("'users' table has been created successfully.");
    } catch (SQLException e) {
      System.out.println("Failed to create the users table.");
      System.out.println("Error, While creating the 'users' table. " +
                         e.getMessage());
    }
  }

  private static void createQuestionsTable(Connection connection) {
    System.out.println("==> 'questions' table not found.");
    System.out.println("Creating the 'questions' table...");
    int affectedRows;
    query = "" "
        CREATE TABLE
        questions(id INT PRIMARY KEY AUTO_INCREMENT, qid INT DEFAULT NULL,
                  from_user VARCHAR(255) NOT NULL,
                  to_user VARCHAR(255) NOT NULL, is_anonymous BOOLEAN NOT NULL,
                  question_text TEXT NOT NULL, answer_text TEXT NULL,
                  CONSTRAINT fk_question_thread FOREIGN KEY(qid)
                      REFERENCES questions(id) ON DELETE CASCADE) "" ";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.execute();
      System.out.println("'questions' table has been created successfully.");
    } catch (SQLException e) {
      System.out.println("Failed to create the questions table.");
      System.out.println("Error, While creating the 'questions' table. " +
                         e.getMessage());
    }
  }

  public static void checkQuestionsTable() {
    try (HikariDataSource ds = new HikariDataSource()) {
      ds.setJdbcUrl(dbUrl + dbName);
      ds.setUsername(dbUserName);
      ds.setPassword(dbPassword);

      connection = ds.getConnection();
      query = "" "
          show tables like 'questions' "" ";

          try (PreparedStatement statement =
                   connection.prepareStatement(query)) {
        ResultSet result = statement.executeQuery();

        if (!result.next())
          createQuestionsTable(connection);
        else
          System.out.println("'questions' table already exists.");
      } catch (SQLException e) {
        System.out.println(
            "Error, While checking the databases table 'questions' ." +
            e.getMessage());
      }
    } catch (SQLException e) {
      System.out.println(
          "Error, While HikariDataSource making the connection. " +
          e.getMessage());
    }
  }

  public static boolean isColumnValueAvailable(String labelName,
                                               String userInput) {
    try (HikariDataSource ds = new HikariDataSource()) {
      ds.setJdbcUrl(dbUrl + dbName);
      ds.setUsername(dbUserName);
      ds.setPassword(dbPassword);

      connection = ds.getConnection();
      query = "" "
          select
          ? from users where
          ? = ? "" ";
            try (PreparedStatement statement =
                     connection.prepareStatement(query)) {
        statement.setString(1, labelName);
        statement.setString(2, labelName);
        statement.setString(3, userInput);
        ResultSet result = statement.executeQuery();
        return !result.next();
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    } catch (Exception e) {
      System.out.println("Error, While checking the " + labelName +
                         " address. " + e.getMessage());
    }
    return false;
  }

  public static Integer addUser(User newUser) {
    Integer userId = null;
    try (HikariDataSource ds = new HikariDataSource()) {
      ds.setJdbcUrl(dbUrl + dbName);
      ds.setUsername(dbUserName);
      ds.setPassword(dbPassword);

      connection = ds.getConnection();

      userId = insertUser(connection, newUser);

      if (userId == null)
        System.out.println("User creation failed.");
    } catch (Exception e) {
      System.out.println(
          "Error, While HikariDataSource making the connection. " +
          e.getMessage());
    }

    return userId;
  }

  private static Integer insertUser(Connection connection, User newUser) {
    String query = """
        insert into users (username, password, name, email, allow_anonymous_questions)
        values(?, ?, ?, ?, ?)
        """;
    Integer userId = null;

    try (PreparedStatement statement = connection.prepareStatement(
             query, Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, newUser.getUserName());
      statement.setString(2, jbCrypt(newUser.getPassword()));
      statement.setString(3, newUser.getName());
      statement.setString(4, newUser.getEmail());
      statement.setBoolean(5, newUser.isAllowAnonymous());

      int rowInserted = statement.executeUpdate();

      if (rowInserted > 0) {
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
          if (generatedKeys.next())
            userId = generatedKeys.getInt(1);
        }
      }
      System.out.println("Row Inserted: " + rowInserted +
                         ", Generated User ID: " + userId);

    } catch (SQLException e) {
      System.out.println("Error, failed to process the query. " +
                         e.getMessage());
    }

    return userId;
  }

  public static boolean insertQuestion(Question newQuestion) {
    try (HikariDataSource ds = new HikariDataSource()) {
      ds.setJdbcUrl(dbUrl + dbName);
      ds.setUsername(dbUserName);
      ds.setPassword(dbPassword);

      connection = ds.getConnection();
      String query = """
          insert into questions (qid, from_user, to_user, is_anonymous, question_text)
          values(?, ?, ?, ? ,?)
          """;

      try (PreparedStatement statement = connection.prepareStatement(query)) {
        int qid = newQuestion.getQid();
        if (qid == -1)
          statement.setNull(1, Types.NULL);
        else
          statement.setInt(1, qid);
        statement.setInt(2, newQuestion.getFrom());
        statement.setInt(3, newQuestion.getTo());
        statement.setBoolean(4, newQuestion.getIsAnonymous());
        statement.setString(5, newQuestion.getQuestionText());

        int rowInserted = statement.executeUpdate();

        if (rowInserted > 0) {
          System.out.println("Row Inserted: " + rowInserted);
          return true;
        }
      } catch (SQLException e) {
        System.out.println("Error, failed to process the query. " +
                           e.getMessage());
      }
    } catch (Exception e) {
      System.out.println("Error, failed to process the query. " +
                         e.getMessage());
    }
    return false;
  }

  public static HashMap<Integer, ArrayList<Object>> getSystemUsersData() {
    HashMap<Integer, ArrayList<Object>> usersData = null;
    try (HikariDataSource ds = new HikariDataSource()) {
      ds.setJdbcUrl(dbUrl + dbName);
      ds.setUsername(dbUserName);
      ds.setPassword(dbPassword);

      connection = ds.getConnection();
      int rowAffected;
      query = "" "
          select id,
      username,
      allow_anonymous_questions from users order by id "" ";
          try (PreparedStatement statement =
                   connection.prepareStatement(query)) {
        ResultSet result = statement.executeQuery();
        usersData = new HashMap<Integer, ArrayList<Object>>();
        while (result.next()) {
          ArrayList<Object> userData = new ArrayList<Object>();
          userData.add(result.getString("username"));
          userData.add(result.getBoolean("allow_anonymous_questions"));
          usersData.put(result.getInt("id"), userData);
        }
        return usersData;
      } catch (Exception e) {
        System.out.println("Error, while getting the System users data. " +
                           e.getMessage());
      }
    } catch (Exception e) {
      System.out.println(
          "Error, While HikariDataSource making the connection. " +
          e.getMessage());
    }
    return usersData;
  }

  public static Integer isRegistered(String userName, String password) {
    Integer userId = null;
    try (HikariDataSource ds = new HikariDataSource()) {
      ds.setJdbcUrl(dbUrl + dbName);
      ds.setUsername(dbUserName);
      ds.setPassword(dbPassword);

      connection = ds.getConnection();

      query = "" "
          select id,
      username,
      password from users where username = ? "" ";

          try (PreparedStatement statement =
                   connection.prepareStatement(query)) {
        statement.setString(1, userName);
        ResultSet result = statement.executeQuery();

        if (result.next() &&
            BCrypt.checkpw(password, result.getString("password")))
          userId = result.getInt("id");
      } catch (Exception e) {
        System.out.println("Error, while getting user credentials. " +
                           e.getMessage());
      }
    } catch (SQLException e) {
      System.out.println(
          "Error, While HikariDataSource making the connection. " +
          e.getMessage());
    }
    return userId;
  }

  public static User getUserById(int userId) {
    try (HikariDataSource ds = new HikariDataSource()) {
      ds.setJdbcUrl(dbUrl + dbName);
      ds.setUsername(dbUserName);
      ds.setPassword(dbPassword);

      connection = ds.getConnection();

      query = "" "
          select *from users where id =
              ? "" ";

          try (PreparedStatement statement =
                   connection.prepareStatement(query)) {
        statement.setInt(1, userId);
        ResultSet result = statement.executeQuery();

        User foundUser = new User();
        if (result.next()) {
          foundUser.setId(result.getInt("id"));
          foundUser.setUserName(result.getString("username"));
          foundUser.setName(result.getString("name"));
          foundUser.setAllowAnonymous(
              result.getBoolean("allow_anonymous_questions"));
          return foundUser;
        }
      } catch (Exception e) {
        System.out.println("Error, while getting user credentials. " +
                           e.getMessage());
      }
    } catch (SQLException e) {
      System.out.println(
          "Error, While HikariDataSource making the connection. " +
          e.getMessage());
    }
    return null;
  }

  public static HashMap<Integer, Question> getFeeds(Integer userId) {
    HashMap<Integer, Question> questionsMap = null;
    try (HikariDataSource ds = new HikariDataSource()) {
      ds.setJdbcUrl(dbUrl + dbName);
      ds.setUsername(dbUserName);
      ds.setPassword(dbPassword);

      connection = ds.getConnection();

      String query = "" "
                     select *
                     from questions "" ";

                     if (userId != null) query += " where to_user = ?";

      try (PreparedStatement statement = connection.prepareStatement(query)) {
        if (userId != null)
          statement.setInt(1, userId);
        ResultSet resultSet = statement.executeQuery();

        questionsMap = new HashMap<Integer, Question>();
        while (resultSet.next()) {
          Question retrievedQuestion = extractQuestion(resultSet, true);
          if (retrievedQuestion != null)
            loadToMeQuestions(questionsMap, retrievedQuestion);
        }
      }
    } catch (SQLException e) {
      System.out.println("Error executing query: " + e.getMessage());
    }
    return questionsMap;
  }

  private static void
  loadToMeQuestions(HashMap<Integer, Question> questionHashMap,
                    Question question) {

    int id = question.getId();
    int qid = question.getQid();

    if (qid == 0) {
      questionHashMap.put(id, question);
    } else {
      Question parentQuestion = questionHashMap.get(qid);

      if (parentQuestion != null) {
        if (parentQuestion.getThreads() == null)
          parentQuestion.setThreads(new ArrayList<>());
        parentQuestion.getThreads().add(question);
      }
    }
  }

  public static Question extractQuestion(ResultSet resultSet,
                                         boolean getFromId) {
    try {
      Question extractedQuestion = new Question();

      extractedQuestion.setId(resultSet.getInt("id"));
      extractedQuestion.setQid(resultSet.getInt("qid"));
      boolean isAnonymous = resultSet.getBoolean("is_anonymous");
      extractedQuestion.setIsAnonymous(isAnonymous);

      if (getFromId) { // I shouldn't fetch the sender id
        int from = -1;
        if (!isAnonymous) // don't retrieve if it's an anonymous question
          from = resultSet.getInt("from_user");
        extractedQuestion.setFrom(from);
      }

      // Extracting From Me Questions so from_user == myId
      extractedQuestion.setTo(resultSet.getInt("to_user"));
      extractedQuestion.setQuestionText(resultSet.getString("question_text"));
      extractedQuestion.setAnswerText(resultSet.getString("answer_text"));

      return extractedQuestion;
    } catch (Exception e) {
      System.out.println("Error, While extracting the resultSet. " +
                         e.getMessage());
    }
    return null;
  }

  public static ArrayList<Question> getFromMeQuestions(int userId) {
    ArrayList<Question> questions = null;
    try (HikariDataSource ds = new HikariDataSource()) {
      ds.setJdbcUrl(dbUrl + dbName);
      ds.setUsername(dbUserName);
      ds.setPassword(dbPassword);

      connection = ds.getConnection();

      query = "" "
          select *from questions where from_user =
              ? "" ";

          try (PreparedStatement statement =
                   connection.prepareStatement(query)) {
        statement.setInt(1, userId);
        ResultSet resultSet = statement.executeQuery();

        questions = new ArrayList<>();
        while (resultSet.next()) {
          Question retrievedQuestion = extractQuestion(resultSet, false);
          if (retrievedQuestion != null)
            questions.add(retrievedQuestion);
        }
      } catch (SQLException e) {
        System.out.println("Error executing query: " + e.getMessage());
      }
    } catch (Exception e) {
      System.out.println("Error connecting to the database: " + e.getMessage());
    }
    return questions;
  }

  public static boolean updateAnswer(int questionId, String answer) {
    try (HikariDataSource ds = new HikariDataSource()) {
      ds.setJdbcUrl(dbUrl + dbName);
      ds.setUsername(dbUserName);
      ds.setPassword(dbPassword);

      connection = ds.getConnection();
      query = "" "
          update questions set answer_text =
              ? where id = ? "" ";

                try (PreparedStatement statement =
                         connection.prepareStatement(query)) {
        statement.setString(1, answer);
        statement.setInt(2, questionId);
        return statement.executeUpdate() > 0;
      } catch (SQLException e) {
        System.out.println("Error executing query: " + e.getMessage());
      }
    } catch (Exception e) {
      System.out.println("Error connecting to the database: " + e.getMessage());
    }
    return false;
  }

  public static boolean deleteQuestionById(int userId, int questionId) {
    try (HikariDataSource ds = new HikariDataSource()) {
      ds.setJdbcUrl(dbUrl + dbName);
      ds.setUsername(dbUserName);
      ds.setPassword(dbPassword);

      connection = ds.getConnection();
      query = """
          delete from questions
          where (id = ? OR qid = ?)
          AND from_user = ?
          """;

      // Start a transaction
      connection.setAutoCommit(false);
      try (PreparedStatement statement = connection.prepareStatement(query)) {
        statement.setInt(1, questionId);
        statement.setInt(2, questionId); // parent_id for thread questions
        statement.setInt(3, userId);     // ensure it belongs to the user
        int rowsAffected = statement.executeUpdate();

        connection.commit();

        if (rowsAffected > 0) {
          System.out.println("Main question and any thread questions "
                             + "deleted successfully.");
          return true;
        } else {
          System.out.println("Error: No question found or you are not "
                             + "authorized to delete it.");
          return false;
        }

      } catch (SQLException e) {
        System.out.println("Error while deleting the question: " +
                           e.getMessage());
        connection.rollback();
        return false;
      }

    } catch (SQLException e) {
      System.out.println("Error establishing connection: " + e.getMessage());
      return false;
    }
  }

  public static Question getQuestionById(int questionId) {
    try (HikariDataSource ds = new HikariDataSource()) {
      ds.setJdbcUrl(dbUrl + dbName);
      ds.setUsername(dbUserName);
      ds.setPassword(dbPassword);

      Connection connection = ds.getConnection();
      String query = "select * from questions where id = ?";

      try (PreparedStatement statement = connection.prepareStatement(query)) {
        statement.setInt(1, questionId);
        ResultSet resultSet = statement.executeQuery();
        if (!resultSet.next())
          return null;

        return extractQuestion(resultSet, false);
      } catch (Exception e) {
        System.out.println("Error, While executing the query. " +
                           e.getMessage());
      }
    } catch (Exception e) {
      System.out.println("Error, While connecting to the database. " +
                         e.getMessage());
    }
    return null;
  }

  public static Question getQuestionWithThreadsById(int questionId,
                                                    int userId) {
    try (HikariDataSource ds = new HikariDataSource()) {
      ds.setJdbcUrl(dbUrl + dbName);
      ds.setUsername(dbUserName);
      ds.setPassword(dbPassword);

      connection = ds.getConnection();
      String query =
          "select * from questions where (id = ? OR qid = ?) AND from_user = ?";

      try (PreparedStatement statement = connection.prepareStatement(query)) {
        statement.setInt(1, questionId);
        statement.setInt(2, questionId);
        statement.setInt(3, userId);

        ResultSet resultSet = statement.executeQuery();
        if (!resultSet.next())
          return null;

        Question parentQuestion = extractQuestion(
            resultSet, true); // just to extract the first question
        if (parentQuestion == null)
          return null;

        ArrayList<Question> threads = parentQuestion.getThreads();

        if (threads == null)
          parentQuestion.setThreads(new ArrayList<Question>());

        while (resultSet.next())
          parentQuestion.getThreads().add(extractQuestion(resultSet, true));

        return parentQuestion;
      } catch (Exception e) {
        System.out.println("Error, While executing the query. " +
                           e.getMessage());
      }
    } catch (Exception e) {
      System.out.println("Error, While connecting to the database. " +
                         e.getMessage());
    }
    return null;
  }
};
