package ua.com.juja.microservices.teams.service;

import java.io.IOException;
import java.io.Reader;

/**
 * @author Ivan Shapovalov
 */
public class Utils {

    public static String convertToString(Reader reader) throws IOException {
        char[] arr = new char[8 * 1024];
        StringBuilder buffer = new StringBuilder();
        int numCharsRead;
        while ((numCharsRead = reader.read(arr, 0, arr.length)) != -1) {
            buffer.append(arr, 0, numCharsRead);
        }
        return buffer.toString();
    }
}
