package com.trustpoint.bloggenerator;

import java.util.ArrayList;
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

    public static void init()
    {
        // TODO: init list from text file.

        categoryList = new ArrayList<String>();
        categoryList.add("");
        categoryList.add("iot");
        categoryList.add("security");
    }

    public static void update()
    {
        // TODO: update text file from former blogs.
    }
}
