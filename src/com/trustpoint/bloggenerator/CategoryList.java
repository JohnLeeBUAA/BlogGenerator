package com.trustpoint.bloggenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Store all categories
 *
 * @author zli
 *
 */
public class CategoryList
{
    public static List<String> categoryList;
    public static HashMap<String, Integer> categoryCount;

    public static void init()
    {
        // TODO: init list from text file

        categoryList = new ArrayList<String>();
        categoryCount = new HashMap<String, Integer>();

        categoryList.add("");

        categoryList.add("iot");
        categoryCount.put("iot", 0);
        categoryList.add("security");
        categoryCount.put("security", 0);
        categoryList.add("test");
        categoryCount.put("test", 0);
        categoryList.add("anothertest");
        categoryCount.put("anothertest", 0);
    }

    public static void update()
    {
        // TODO: update text file resources from existing blogs
    }
}
