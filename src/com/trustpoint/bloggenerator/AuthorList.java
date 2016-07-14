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
    public static HashMap<String, String> nameToCode; // <name, code>
    public static HashMap<String, String> codeToName; // <code, name>

    public static void init()
    {
        // TODO: init authors from file.

        nameList = new ArrayList<String>();
        nameList.add("");
        nameList.add("Zijin Li");

        nameToCode = new HashMap<>();
        nameToCode.put("Zijin Li", "zli");
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
