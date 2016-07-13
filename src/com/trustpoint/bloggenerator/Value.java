package com.trustpoint.bloggenerator;

/**
 * Store values used in other files.
 *
 * @author zli
 *
 */
public class Value
{
    public static String BASE_DIR;

    public static final String TITLE = "Blog Generator";
    public static final String ERROR = "Error";
    public static final String RELATIVE_DIR = "eclipse_workspace/BlogGenerator";
    public static final String SELF_DIR = "BlogGenerator";
    public static final String AUTHOR_DIR = "src/www/_config.yml";
    public static final String CATEGORY_DIR = "build/categories/";
    public static final String GENERATE = "generate";
    public static final String EDIT = "edit";

    public static final String[] NO_CAPITAL_WORD_LIST = {
            "in",
            "and",
            "the",
            "a",
            "to"
    };

    // GUI values
    public static final int EDITOR_INITIAL_LINES = 100;
    public static int LABEL_WIDTH;
    public static int TEXTFIELD_WIDTH;

}
