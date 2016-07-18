package com.trustpoint.bloggenerator;

import java.util.HashMap;

/**
 * Store a list of abbreviations in a single blog.
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
    }

    public int listCt()
    {
        return list.size();
    }

    public HashMap<String, String> getList()
    {
        return list;
    }

    public void addAbbr(String shortForm, String fullForm)
    {
        if (list.containsKey(shortForm)) {
            Error error = new Error();
            error.initErrorFrame("Abbreviaton: \"" + shortForm + "\" has already been added.");
        } else {
            if (fullForm.equals("")) {
                fullForm = AbbrList.getFullForm(shortForm);
            }
            list.put(shortForm, fullForm);
        }
    }

    public void removeAbbr(String shortForm)
    {
        if (!list.containsKey(shortForm)) {
            Error error = new Error();
            error.initErrorFrame("Abbreviaton: \"" + shortForm + "\" is not in the list.");
        }
        else {
            list.remove(shortForm);
        }
    }
}
