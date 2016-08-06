package com.trustpoint.bloggenerator;

/**
 * Handle a single author.
 *
 * @author zli
 *
 */
public class Author {
  private String name;
  private String code;

  /**
   * Constructor.
   */
  public Author() {
    this.name = "";
    this.code = "";
  }

  /**
   * Getter of name.
   *
   * @return Name of author.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Getter of code.
   *
   * @return Name code of author.
   */
  public String getCode() {
    return this.code;
  }

  /**
   * Set the name and name code of author.
   *
   * @param name Name of author.
   */
  public void setName(String name) {
    this.name = name;
    this.code = AuthorList.getCode(name);
  }
}
