package com.trustpoint.bloggenerator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Handle a list of abbreviation of a single blog.
 *
 * @author zli
 *
 */
public class Abbr
{
    private HashMap<String, String> list;

    public Abbr()
    {
        list = new HashMap<String, String>();

        // TODO: get rid of this later
        list.put("M2M", "Machine to Machine");

    }

    public int listCt()
    {
        return list.size();
    }

    public HashMap<String, String> getList()
    {
        return list;
    }

    public void setList(HashMap<String, String> list)
    {
        this.list = list;
    }

    public void addAbbr(String abbr)
    {
        // String fullForm = AbbrList.getFullForm(abbr);
        String fullForm = "test";

    }

    private String googleFullForm(String abbr)
    {
        String fullForm = "";
        try {
            URL url = new URL(Value.GOOGLE_SEARCH_URL + abbr);
            HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
            httpcon.addRequestProperty("User-Agent", "Chrome/51.0.2704");
            BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
            String input;
            while ((input = br.readLine()) != null) {
                // TODO: Get the full form of abbr in html
            }
            br.close();
        } catch (Exception e) {
            Error error = new Error();
            error.initErrorFrame(
                    "Exception getting Google search result of " + abbr + ".\n" + e.getMessage());
        }
        return fullForm;
    }
}
