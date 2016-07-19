package com.trustpoint.bloggenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Store a list of words whose first letter should not be capitalized if used in a header
 *
 * @author zli
 *
 */
public class LowercaseWordList
{
    public static List<String> lowercaseWordList;

    public static void init()
    {
        // TODO: init list from text file.

        lowercaseWordList = new ArrayList<String>();
        lowercaseWordList.add("in");
        lowercaseWordList.add("and");
        lowercaseWordList.add("the");
        lowercaseWordList.add("a");
        lowercaseWordList.add("to");
    }

    public static void update()
    {
        // TODO: update text file from blogs
    }

}
