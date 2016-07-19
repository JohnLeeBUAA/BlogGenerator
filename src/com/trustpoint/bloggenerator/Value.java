package com.trustpoint.bloggenerator;

import java.util.HashMap;

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
    public static final String OPERATION_GENERATE = "generate";
    public static final String OPERATION_EDIT = "edit";
    public static final String GOOGLE_SEARCH_URL = "https://www.google.ca/search?q=";
    public static final String IMAGE_DIR = "src/www/img/blog-articles/";
    public static final String LINE_PREFIX = "    ";
    public static final String IMAGE_DIR_IN_TAG = "/img/blog-articles/";

    // GUI values
    public static final String TITLE = "Blog Generator";
    public static final String ERROR = "Error";
    public static final int EDITOR_INITIAL_LINES = 100;
    public static final int LINE_LENGTH = 100;
    public static final int FLOWLAYOUT_GAP = 10;

    // Header values
    public static final String HEADER_START = "---";
    public static final String HEADER_LAYOUT = "layout: post";
    public static final String HEADER_TITLE = "title: ";
    public static final int HEADER_TITLE_LINECT = 2;
    public static final String HEADER_DATE = "date: ";
    public static final String HEADER_TIME = " 12:00:00";
    public static final int HEADER_DATE_LINECT = 3;
    public static final String HEADER_AUTHOR = "author: ";
    public static final int HEADER_AUTHOR_LINECT = 4;
    public static final String HEADER_CATEGORIES = "categories: ";
    public static final int HEADER_CATEGORIES_LINECT = 5;
    public static final String HEADER_EXCERPT = "excerpt: >";
    public static final int HEADER_EXCERPT_LINECT = 6;
    public static final String HEADER_IMAGE = "image: ";
    public static final String HEADER_IMAGE_ALT = "image_alt: ";
    public static final String HEADER_IMAGE_DIR = "/img/blog-articles/";
    public static final String HEADER_END = "---";

    // Paragraph values
    public static final String PARAGRAPH_STYLE_HEADER = "header";
    public static final String PARAGRAPH_STYLE_PLAIN = "plain";
    public static final String PARAGRAPH_STYLE_BULLET = "bullet";
    public static final String PARAGRAPH_STYLE_DECIMAL = "decimal";

    // HTML tags
    public static final String HTML_HEADER_OPEN = "<h3>";
    public static final String HTML_HEADER_CLOSE = "</h3>";
    public static final String HTML_PARAGRAPH_OPEN = "<p>";
    public static final String HTML_PATAGRAPH_CLOSE = "</p>";
    public static final String HTML_NUMBER_LIST_OPEN = "<ol>";
    public static final String HTML_NUMBER_LIST_CLOSE = "</ol>";
    public static final String HTML_BULLET_LIST_OPEN = "<ul>";
    public static final String HTML_BULLET_LIST_CLOSE = "</ul>";
    public static final String HTML_LIST_OPEN = "<li>";
    public static final String HTML_LIST_CLOSE = "</li>";
    public static final String HTML_SUPER_SCRIPT_OPEN = "<sup>";
    public static final String HTML_SUPER_SCRIPT_CLOSE = "</sup>";
    public static final String HTML_SUB_SCRIPT_OPEN = "<sub>";
    public static final String HTML_SUB_SCRIPT_CLOSE = "</sub>";
    public static final String HTML_LINK_CLOSE = "</a>";
    public static final String HTML_ABBR_CLOSE = "</abbr>";

    public static String HTML_LINK_OPEN(String url)
    {
        return "<a href=\"" + url + "\">";
    }

    public static String HTML_ABBR_OPEN(String abbr)
    {
        return "<abbr title=\"" + abbr + "\">";
    }

    public static String HTML_IMAGE(String src, String alt)
    {
        return "<figure><img src=\"" + src + "\" alt=\"" + alt + "\"/><figure>";
    }

    // HTML chars
    // '&' is ascii 38, need to add an extra space so that '&'s in other html chars are not
    // replaced.
    // For this program, it will replace different chars with corresponding html chars, regardless
    // of whether it is used correctly or not. So the responsibility of using correct chars is on
    // the writer's side.
    public static final String HTML_CHAR_HELLIP = "&hellip;";
    public static final HashMap<String, String> htmlCharsTable;
    static {
        htmlCharsTable = new HashMap<String, String>();
        htmlCharsTable.put(Character.toString((char) 34), "&quot;");
        htmlCharsTable.put("& ", "&amp; ");
        htmlCharsTable.put(Character.toString((char) 8211), "&ndash;");
        htmlCharsTable.put(Character.toString((char) 8212), "&mdash;");
        htmlCharsTable.put(Character.toString((char) 8216), "'");
        htmlCharsTable.put(Character.toString((char) 8217), "'");
        htmlCharsTable.put(Character.toString((char) 8220), "&ldquo;");
        htmlCharsTable.put(Character.toString((char) 8221), "&rdquo;");
    }
}
