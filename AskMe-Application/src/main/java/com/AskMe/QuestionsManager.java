package com.AskMe;

public class QuestionsManager {
    private static String userInputStr;
    private static Integer userInputInt;

    private static Integer getValidUserInput(){
        userInputStr = Utilities.scan.nextLine();

        userInputInt = Utilities.strToInt(userInputStr);
        if (userInputInt == null) {
            System.out.println("Invalid input. Please enter a valid number.");
            return null;
        }

        if(userInputInt == -1) {
            System.out.println("Action canceled. Returning to the main menu.");
            return -1;
        }
        return userInputInt;
    }

    public static void answerQuestion(){
        System.out.print("Please enter the question ID to answer, or -1 to cancel: ");

        userInputInt = getValidUserInput();
        if(userInputInt == null || userInputInt == -1)
            return;

        Question targetQuestion = Database.getQuestionById(userInputInt);
        if(targetQuestion == null || targetQuestion.getTo() != UsersManager.getUserId()) {
            System.out.println("The question ID you entered is either invalid or doesn't belong to you.");
            return;
        }

        UI.printQuestion(targetQuestion);
        String answer = targetQuestion.getAnswerText();
        if(answer != null && !answer.isEmpty()) {
            System.out.println("Notice: This question has already been answered. If you proceed, your previous answer will be updated.");
        }

        System.out.print("Please enter your new answer: ");
        String newAnswer = Utilities.scan.nextLine();
        if(newAnswer.trim().isEmpty()) {
            System.out.println("Oops! Your answer can't be empty or just spaces. Please provide a valid response.");
            return;
        }

        if(Database.updateAnswer(targetQuestion.getId(), newAnswer)) {
            System.out.println("Your answer has been successfully updated.");
        } else {
            System.out.println("An error occurred while updating your answer. Please try again.");
        }
    }

    public static void deleteQuestion(){
        System.out.print("Please enter the question ID to delete, or -1 to cancel: ");

        userInputInt = getValidUserInput();
        if(userInputInt == null || userInputInt == -1)
            return;

        Question targetQuestion = Database.getQuestionWithThreadsById(userInputInt, UsersManager.getUserId());
        if(targetQuestion == null) {
            System.out.println("The question ID you entered is either invalid or doesn't belong to you.");
            return;
        }

        UI.printQuestion(targetQuestion);
        UI.listThreadQuestions(targetQuestion.getThreads());

        System.out.println("Are you sure you want to delete this question? This action cannot be undone.");
        System.out.print("Enter 'Y' to confirm, or any other key to cancel: ");
        userInputStr = Utilities.scan.nextLine().trim();

        if (!userInputStr.equalsIgnoreCase("Y")) {
            System.out.println("Deletion canceled.");
            return;
        }


        boolean deletionSuccess = Database.deleteQuestionById(UsersManager.getUserId(), targetQuestion.getId());
        if (deletionSuccess) {
            System.out.println("The question has been successfully deleted.");
        } else {
            System.out.println("An error occurred while attempting to delete the question. Please try again.");
        }
    }

    public static void askQuestion(){
        System.out.print("Please enter the user ID to ask, or -1 to cancel: ");

        userInputInt = getValidUserInput();
        if(userInputInt == null || userInputInt == -1)
            return;

        int userId = userInputInt;

        User targetUser = Database.getUserById(userId);
        if (targetUser == null) {
            System.out.println("The user ID you entered is invalid or does not exist.");
            return;
        }

        if(targetUser.getId() == UsersManager.getUserId()) {
            System.out.println("You cannot select yourself as the target user. Please enter a different user ID.");
            return;
        }

        Question newQuestion = new Question();

        boolean isAllowAnonymousQ = targetUser.isAllowAnonymous();


        if(!isAllowAnonymousQ)
            System.out.println("NOTE: Anonymous questions are not allowed for this user.");
        else {
            Field filed = new AnonymousValidator();
            System.out.print("Do you want to ask anonymously? (yes/no): ");
            String userInput = Utilities.scan.nextLine();
            boolean isValid = filed.isValid(userInput);

            if(!isValid){
                System.out.println("Invalid user input. Please try again later");
                return;
            }

            newQuestion.setIsAnonymous(userInput.equals("yes"));
        }

        // for thread questions
        System.out.print("For thread question: Enter Question id or -1 for new question: ");
        userInputStr = Utilities.scan.nextLine();
        userInputInt = Utilities.strToInt(userInputStr);
        if(userInputInt == null)
            return;

        int qid = userInputInt;
        if(userInputInt != -1) {
            Question mainQuestion = Database.getQuestionById(qid);
            if(mainQuestion == null) {
                System.out.println("No question found with the given ID.");
                return;
            }
            System.out.println("The main question you are adding a thread to:");
            UI.printQuestion(mainQuestion);
            newQuestion.setQid(qid);
        }
        else
            newQuestion.setQid(-1);

        System.out.print("Please enter your question: ");
        String questionText = Utilities.scan.nextLine();
        Field field = new QuestionValidator();

        if(!field.isValid(questionText))
            return;

        newQuestion.setQuestionText(questionText);
        newQuestion.setFrom(UsersManager.getUserId());
        newQuestion.setTo(userId);

        boolean isSuccess = Database.insertQuestion(newQuestion);

        // Check for success
        if (isSuccess) {
            System.out.println("Your question has been successfully submitted to " + targetUser.getUserName() + ".");
        } else {
            System.out.println("An error occurred while submitting your question. Please try again later.");
        }
    }
}
