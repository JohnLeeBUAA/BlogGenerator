package com.trustpoint.bloggenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

/**
 * Store a list of all abbreviations frequently used in former blogs
 *
 * @author zli
 *
 */
public class AbbrList
{
    public static HashMap<String, String> abbrList;

    /**
     * Initialize the list from text file
     */
    public static void init()
    {
        Path targetDir = Paths.get(Value.BASE_DIR + Value.SELF_DIR + Value.ABBR_DIR);
        if (Files.exists(targetDir)) {
            abbrList = new HashMap<String, String>();

            try {
                BufferedReader br = new BufferedReader(new FileReader(targetDir.toFile()));
                String line;
                while ((line = br.readLine()) != null) {
                    String[] abbrRecord = StringUtils.split(line, ':');
                    abbrList.put(abbrRecord[0], abbrRecord[1]);
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
     * Get the full form of an abbreviation. First try to find in list, if the list does not contain
     * the abbreviation do Google search
     *
     * @param shortForm
     * @return
     */
    public static String getFullForm(String shortForm)
    {
        if (abbrList.containsKey(shortForm)) {
            return abbrList.get(shortForm);
        } else {
            return googleFullForm(shortForm);
        }
    }

    /**
     * Use Google search to find the full form of an abbreviation. The result is the first
     * definition of Wikipedia, not always accurate
     *
     * @param abbr
     * @return
     */
    public static String googleFullForm(String abbr)
    {
        String fullForm = "";
        try {
            URL url = new URL(Value.GOOGLE_SEARCH_URL + abbr);
            HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
            httpcon.addRequestProperty("User-Agent", "Chrome/51.0.2704");
            BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
            String input;
            while ((input = br.readLine()) != null) {
                if (input.contains(Value.GOOGLE_SEARCH_ANCHOR)) {
                    int lastPos = input.indexOf(Value.GOOGLE_SEARCH_ANCHOR);
                    int firstPos = input.lastIndexOf('>', lastPos) + 1;
                    if (firstPos == lastPos) {
                        lastPos = input.lastIndexOf('<', lastPos);
                        firstPos = input.lastIndexOf(">", lastPos) + 1;
                    }
                    fullForm = input.substring(firstPos, lastPos);
                    break;
                }
            }
            br.close();
        } catch (Exception e) {
            Error error = new Error();
            error.initErrorFrame("Exception getting Google search result of \"" + abbr + "\".\n"
                    + e.getMessage());
        }
        return LowercaseWordList.capitalize(fullForm.trim());
    }
}
