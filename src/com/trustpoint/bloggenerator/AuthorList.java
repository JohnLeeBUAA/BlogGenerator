package com.trustpoint.bloggenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Store the list of all authors.
 *
 * @author zli
 *
 */
public class AuthorList
{
    public static List<String> nameList;
    public static HashMap<String, String> nameToCode; // <name, code>
    public static HashMap<String, String> codeToName; // <code, name>

    public static void init()
    {
        // TODO: init authors from text file.

        nameList = new ArrayList<String>();
        nameList.add("");
        nameList.add("Zijin Li");

        nameToCode = new HashMap<String, String>();
        nameToCode.put("Zijin Li", "zli");

        codeToName = new HashMap<String, String>();
        codeToName.put("zli", "Zijin Li");
    }

    public static void update()
    {
        // TODO: update text file form config file.
    }

    public static String getCode(String name)
    {
        return nameToCode.get(name);
    }

    public static String getName(String code)
    {
        return codeToName.get(code);
    }
}
