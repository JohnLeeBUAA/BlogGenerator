package com.trustpoint.bloggenerator;

import java.util.HashMap;

/**
 * Store a list of abbreviations in a single blog
 *
 * @author zli
 *
 */
public class Abbr
{
    public HashMap<String, String> list;

    public Abbr()
    {
        list = new HashMap<String, String>();
    }

    /**
     * Used when parsing
     * @param shortForm
     */
    public void add(String shortForm)
    {
        if (!list.containsKey(shortForm)) {
            list.put(shortForm, AbbrList.getFullForm(shortForm));
        }
    }

    /**
     * Used in GUI
     * @param shortForm
     * @param fullForm
     */
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

    public boolean contains(String shortForm)
    {
        return list.containsKey(shortForm);
    }

    public String fullForm(String shortForm)
    {
        if (list.containsKey(shortForm)) {
            return list.get(shortForm);
        }
        else {
            return "";
        }
    }
}
