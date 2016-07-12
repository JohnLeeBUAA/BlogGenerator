package com.trustpoint.bloggenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Store a list of authors.
 *
 * @author zli
 *
 */
public class AuthorList
{
    public static List<String> nameList;
    public static HashMap<String, String> nameCode; // <name, code>

    public static void init()
    {
        // TODO: init authors from file.

        nameList = new ArrayList<String>();
        nameList.add("Zijin Li");

        nameCode = new HashMap<>();
        nameCode.put("Zijin Li", "zli");
    }

    public static String getNameCode(String name)
    {
        return nameCode.get(name);
    }
}
