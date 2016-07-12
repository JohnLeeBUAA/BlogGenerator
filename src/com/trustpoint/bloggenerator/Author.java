package com.trustpoint.bloggenerator;

/**
 * Handle a single author.
 *
 * @author zli
 *
 */
public class Author
{
    private String name;
    private String nameCode;

    public Author(String name) {
        this.name = name;
        this.nameCode = AuthorList.getNameCode(name);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
        this.nameCode = AuthorList.getNameCode(name);
    }

    public String getNameCode()
    {
        return nameCode;
    }
}
