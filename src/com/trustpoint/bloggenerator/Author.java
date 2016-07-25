package com.trustpoint.bloggenerator;

/**
 * Handle a single author
 *
 * @author zli
 *
 */
public class Author
{
    private String name;
    private String code;

    public Author()
    {
        this.name = "";
        this.code = "";
    }

    public Author(String name)
    {
        this.name = name;
        this.code = AuthorList.getCode(name);
    }

    public String getName()
    {
        return this.name;
    }

    public String getCode()
    {
        return this.code;
    }

    public void setName(String name)
    {
        this.name = name;
        this.code = AuthorList.getCode(name);
    }
}
