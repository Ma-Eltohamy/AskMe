package com.AskMe;

import java.util.*;

public class UI {
    public static final ArrayList<String> guestMenu = new ArrayList<>(Arrays.asList("Login", "Sign Up", "Exit"));
    public static final ArrayList<String> userMenu = new ArrayList<>(Arrays.asList(
            "Print questions to me",
            "Print questions from me",
            "Answer a question",
            "Delete a question",
            "Ask a question",
            "List system users",
            "Feed",
            "Logout",
            "Exit"
    ));

    public static void printMenu(ArrayList<String> Menu){
        System.out.println("Menu");
        int i = 0, menuSize = Menu.size();
        for(i = 0;i < menuSize; ++i){
            System.out.println("\t" +
                    (i + 1) + ": " +
                    Menu.get(i)
            );
        }
    }

    public static void listSystemUsers(){
        HashMap<Integer, ArrayList<Object>> usersData = Database.getSystemUsersData();
        if(usersData == null)
            return;

        ArrayList<Object> value;
        for (Map.Entry entry : usersData.entrySet()) {
            value = (ArrayList<Object>) entry.getValue();
            System.out.println("ID: " + entry.getKey() + "\t user name: " + value.getFirst());
        }
    }

    public static void listThreadQuestions(ArrayList<Question> threadQuestions){
        for(Question question : threadQuestions) {
            System.out.print("\tThread: ");
            printQuestion(question);
        }
    }

    public static void listFromMeQuestions(){
        ArrayList<Question> questions = Database.getFromMeQuestions(UsersManager.getUserId());

        if(questions == null || questions.isEmpty()){
            System.out.println("You didn't ask anyone try to ask someone first :).");
            return;
        }

        for(Question question : questions){
            System.out.print("Question Id(" + question.getId() + ") ");
            if(!question.getIsAnonymous())
                System.out.print("!AQ ");
            System.out.print("to user id (" + question.getTo() + ")");
            System.out.print('\t');
            System.out.print("Question: " + question.getQuestionText());
            System.out.print('\t');

            String answer = question.getAnswerText();

            if(answer == null || answer.isEmpty())
                System.out.print("Not Answered Yet.");
            else
                System.out.print("Answer: " + answer);
            System.out.println();
        }

    }

    public static void printQuestion(Question question){
        System.out.print("Question Id (" + question.getId() + ")");

        if (!question.getIsAnonymous())
            System.out.print("from user id(" + question.getFrom() + ")");

        System.out.print("\tQuestion: " + question.getQuestionText());
        String answer = question.getAnswerText();
        if (answer != null && !answer.isEmpty()) {
            System.out.print("\n\t");
            if(question.getQid() != 0) // is a thread question
                System.out.print("Thread: \t");
            System.out.print("Answer: " + answer);
        }
        System.out.print('\n');
    }

    public static void listFeeds(boolean onlyToMe){
        HashMap<Integer, Question> questions;
        if(onlyToMe) {
            questions = Database.getFeeds(UsersManager.getUserId());
            if(questions.isEmpty()) {
                System.out.println("You don't have any questions at the moment.");
            }
        } else {
            questions = Database.getFeeds(null);
            if(questions.isEmpty()) {
                System.out.println("There are currently no questions available in the feed.");
            }
        }
        for(Map.Entry<Integer, Question> entry : questions.entrySet()){
            System.out.print('\n');
            Question mainQuestion = entry.getValue();
            printQuestion(mainQuestion);

            if(mainQuestion.getThreads() != null)
                listThreadQuestions(mainQuestion.getThreads());
        }
    }
}
