package com.trustpoint.bloggenerator;

import java.util.HashMap;

/**
 * Store a list of all abbreviations used in former blogs
 *
 * @author zli
 *
 */
public class AbbrList
{
    public static HashMap<String, String> abbrList;

    public static void init()
    {
        // TODO: init abbrList from text file

        abbrList = new HashMap<String, String>();
        abbrList.put("IoT", "Internet of Things");
        abbrList.put("M2M", "Machine to Machine");
    }

    public static void update()
    {
        // TODO: update text file resources from existing blogs
    }

    public static String getFullForm(String shortForm)
    {
        if (abbrList.containsKey(shortForm)) {
            return abbrList.get(shortForm);
        } else {
            return googleFullForm(shortForm);
        }
    }

    public static String googleFullForm(String abbr)
    {
        return "google result";
        // String fullForm = "";
        // try {
        // URL url = new URL(Value.GOOGLE_SEARCH_URL + abbr);
        // HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
        // httpcon.addRequestProperty("User-Agent", "Chrome/51.0.2704");
        // BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
        // String input;
        // while ((input = br.readLine()) != null) {
        // // TODO: Get the full form of abbr in html
        // }
        // br.close();
        // } catch (Exception e) {
        // Error error = new Error();
        // error.initErrorFrame(
        // "Exception getting Google search result of \"" + abbr + "\".\n" + e.getMessage());
        // }
        // return fullForm;
    }

}
