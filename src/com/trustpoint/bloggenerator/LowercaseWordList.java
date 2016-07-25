package com.trustpoint.bloggenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Store a list of words whose first letter should not be capitalized if used in a header
 *
 * @author zli
 *
 */
public class LowercaseWordList
{
    public static List<String> lowercaseWordList;

    /**
     * Initialize the list from text file
     */
    public static void init()
    {
        Path targetDir = Paths.get(Value.BASE_DIR + Value.SELF_DIR + Value.LOWERCASEWORDLIST_DIR);
        if (Files.exists(targetDir)) {
            lowercaseWordList = new ArrayList<String>();

            try {
                BufferedReader br = new BufferedReader(new FileReader(targetDir.toFile()));
                String line;
                while ((line = br.readLine()) != null) {
                    lowercaseWordList.add(line);
                }
                br.close();
            } catch (Exception e) {
                Error error = new Error();
                error.initErrorFrame(
                        "Exception reading file: " + targetDir.toString() + ", " + e.toString());
            }
        } else {
            Error error = new Error();
            error.initErrorFrame(targetDir.toString() + " does not exists.");
        }
    }

    /**
     * Capitalize the first letter in words in given string if the word in not in LowercaseWordList
     *
     * @param str
     *            The String containing words
     * @return Capitalized string
     */
    public static String capitalize(String str)
    {
        String result = "";
        if (!StringUtils.isBlank(str)) {
            if (str.charAt(0) == ' ') {
                result += " ";
            }
            String[] list = StringUtils.split(str);
            for (int i = 0; i < list.length; i++) {
                if (!lowercaseWordList.contains(list[i])) {
                    list[i] = list[i].substring(0, 1).toUpperCase() + list[i].substring(1);
                }
                result += list[i] + " ";
            }
            if (str.charAt(str.length() - 1) != ' ') {
                result = StringUtils.stripEnd(result, " ");
            }
        }
        return result;
    }
}
