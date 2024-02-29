package com.example.drops.utils;

import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;

import android.content.Context;
import android.widget.Toast;

public class CheckPassword {

    private static Toast mToast;

    public static boolean checkPassword(Context context, String password, String account) {

        /*
        Password must not contain the user’s account name or parts of the user’s full name that exceed two consecutive characters
        It should be at least eight characters in length
        It must contain characters from the following four categories:
        English uppercase characters (A through Z)
        English lowercase characters (a through z)
        Base 10 digits (0 through 9)
        Non-alphabetic characters (for example, !, $, #, %)
         */

        // Check for account name
        if (password.contains(account)) {
            mToast = Toast.makeText(context, "Password should not contain account name", Toast.LENGTH_LONG);
            mToast.show();
            return false;
        }

        // Check for password length
        if (password.length() < 8) {
            Toast.makeText(context, "Password should be at least 8 characters", Toast.LENGTH_LONG).show();
            return false;
        }

        // Check for upper and lower case characters
        if (!(containsUpper(password) && containsLower(password))) {
            Toast.makeText(context, "Password should have at least one lower and one uppercase character", Toast.LENGTH_LONG).show();
            return false;
        }

        // Check for special characters
        if (!containsSpecial(password)) {
            Toast.makeText(context, "Password should have at least one special character", Toast.LENGTH_LONG).show();
            return false;
        }

        // Check for base 10 digits
        if (!containsDigit(password)) {
            Toast.makeText(context, "Password should have at least one digit", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    // Function to check a string for special characters
    private static boolean containsSpecial(String word) {
        // Check every character of the string
        for (int i = 0; i < word.length(); i++) {

            // Special characters are defined as any character that is not a
            // letter, digit or whitespace character
            if (!Character.isDigit(word.charAt(i))
                    && !Character.isLetter(word.charAt(i))
                    && !Character.isWhitespace(word.charAt(i))) {
                // If special character return true
                return true;
            }
        }

        // Return false if there are no special characters
        return false;
    }

    // Function to check a string for base 10 digits
    private static boolean containsDigit(String word) {

        // Check every character of the string
        for (int i = 0; i < word.length(); i++) {
            if (Character.isDigit(word.charAt(i))) {
                // If digit return true
                return true;
            }
        }

        // Return false when no digits found
        return false;
    }

    // Function to check a string for uppercase letters
    private static boolean containsUpper(String word) {

        // Check every character of the string
        for (int i = 0; i < word.length(); i++) {
            if (isUpperCase(word.charAt(i))) {
                // If uppercase return true
                return true;
            }
        }

        // Return false when no uppercase letters found
        return false;
    }

    // Function to check a string for lowercase letters
    private static boolean containsLower(String word) {

        // Check every character of the string
        for (int i = 0; i < word.length(); i++) {
            if (isLowerCase(word.charAt(i))) {
                // If lowercase return true
                return true;
            }
        }

        // Return false when no lowercase letters found
        return false;
    }
}
