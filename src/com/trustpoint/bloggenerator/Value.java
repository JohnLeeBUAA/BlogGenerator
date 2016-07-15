package com.trustpoint.bloggenerator;

/**
 * Store values used globally.
 *
 * @author zli
 *
 */
public class Value
{
    public static String BASE_DIR;

    // TODO: change eclipse_workspace to website
    public static final String RELATIVE_DIR = "eclipse_workspace/BlogGenerator";
    public static final String SELF_DIR = "BlogGenerator";
    public static final String AUTHOR_DIR = "src/www/_config.yml";
    public static final String CATEGORY_DIR = "build/categories/";
    public static final String GENERATE = "generate";
    public static final String EDIT = "edit";
    public static final String GOOGLE_SEARCH_URL = "https://www.google.ca/search?q=";

    public static final String[] NO_CAPITAL_WORD_LIST = { "in", "and", "the", "a", "to" };

    // GUI values
    public static final String TITLE = "Blog Generator";
    public static final String ERROR = "Error";
    public static final String FILENAMEINPUT_PLACEHOLDER = "File name will be generated automatically if title and date are set";
    public static final int EDITOR_INITIAL_LINES = 100;
    public static final int LINE_LENGTH = 100;
    public static final int FLOWLAYOUT_GAP = 10;

}
