package it.jaschke.alexandria.controller.extras;

/**
 * Created by Elorri on 07/01/2016.
 */
public class Tools {
    public static String fixIsbn(String isbnValue) {
        //catch isbn10 numbers
        if (isbnValue.length() == 10 && !isbnValue.startsWith("978")) {
            return  "978" + isbnValue;
        }
        return isbnValue;
    }
}
