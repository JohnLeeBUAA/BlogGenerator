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
    public static final String OPERATION_GENERATE = "generate";
    public static final String OPERATION_EDIT = "edit";
    public static final String GOOGLE_SEARCH_URL = "https://www.google.ca/search?q=";
    public static final String IMAGE_DIR = "src/www/img/blog-articles/";
    public static final String LINE_PREFIX = "    ";
    public static final String IMAGE_DIR_IN_TAG = "/img/blog-articles/";

    // GUI values
    public static final String TITLE = "Blog Generator";
    public static final String ERROR = "Error";
    public static final String FILENAMEINPUT_PLACEHOLDER = "File name will be generated automatically if title and date are set";
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
}
