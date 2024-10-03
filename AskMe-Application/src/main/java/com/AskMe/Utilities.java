package com.AskMe;

import java.util.Scanner;

public class Utilities {
    public static Scanner scan = new Scanner(System.in);

    public static Integer strToInt(String str){
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e){
            return null;
        }
    }

    public static int getMenuChoice(int ciel){
        System.out.println();

        boolean isValid;
        Integer choiceValue;
        do {
            isValid = true;
            System.out.println("Enter number in range 1 - " + ciel + ": ");
            String userInput = scan.nextLine().trim();
            choiceValue = strToInt(userInput);
            if(choiceValue == null) {
                System.out.println("Please enter a valid number.");
                isValid = false;
            }

            if(isValid && (choiceValue > ciel || choiceValue < 1)){
                System.out.println("Out of range input. Please try again :).");
                isValid = false;
            }
        }while(!isValid);

        return choiceValue;
    }
}
