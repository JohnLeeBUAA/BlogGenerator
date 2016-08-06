package com.trustpoint.bloggenerator;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

/**
 * Store a list of abbreviations in a single blog.
 *
 * @author zli
 *
 */
public class Abbr {
  private HashMap<String, String> list; // <shortForm, fullForm>

  /**
   * Constructor. Initialize the list.
   */
  public Abbr() {
    list = new HashMap<String, String>();
  }

  /**
   * Getter of list.
   *
   * @return list The HashMap of abbreviation list.
   */
  public HashMap<String, String> getList() {
    return list;
  }

  /**
   * Add an abbreviation record to the list.
   * <p>
   * Used in parsing. If the full form is not indicated, it will automatically get full form.
   * </p>
   *
   * @param shortForm The short form of abbreviation to add to list.
   */
  public void add(String shortForm) {
    if (!StringUtils.isBlank(shortForm) && !list.containsKey(shortForm)) {
      list.put(shortForm, AbbrList.getFullForm(shortForm));
    }
  }

  /**
   * Add an abbreviation record to the list.
   * <p>
   * Used in updating. An overload version with full form indicated. Always update the list if the
   * full form is explicitly indicated, even if the short form is already added.
   * </p>
   *
   * @param shortForm The short form of abbreviation to add to list.
   * @param fullForm The full form of abbreviation to add to list.
   */
  public void add(String shortForm, String fullForm) {
    if (!StringUtils.isBlank(shortForm) && !StringUtils.isBlank(fullForm)) {
      list.put(shortForm, fullForm);
    }
  }

  /**
   * For user to add an abbreviation record via GUI.
   *
   * <p>
   * Used in GUI. Will display error message if shortForm is blank or shortForm is already added. If
   * the full form is not indicated, then get from AbbrList.
   * </p>
   *
   * @param shortForm The short form of abbreviation to add to list.
   * @param fullForm The full form of abbreviation to add to list.
   */
  public void addAbbr(String shortForm, String fullForm) {
    if (StringUtils.isBlank(shortForm)) {
      Error error = new Error();
      error.initErrorFrame("Short form of abbreviation to add cannot be empty.");
    } else if (list.containsKey(shortForm)) {
      Error error = new Error();
      error.initErrorFrame("Abbreviaton: \"" + shortForm + "\" has already been added.");
    } else {
      // If full form is not indicated, get from AbbrList
      if (fullForm.equals("")) {
        fullForm = AbbrList.getFullForm(shortForm);
      }
      list.put(shortForm, fullForm);
    }
  }

  /**
   * For user to delete an abbreviation record via GUI.
   *
   * <p>
   * Used in GUI. Will display error message.
   * </p>
   *
   * @param shortForm The short form of abbreviation to delete from list.
   */
  public void removeAbbr(String shortForm) {
    if (StringUtils.isBlank(shortForm)) {
      Error error = new Error();
      error.initErrorFrame("Short form of abbreviation to delete cannot be empty.");
    } else if (!list.containsKey(shortForm)) {
      Error error = new Error();
      error.initErrorFrame("Abbreviaton: \"" + shortForm + "\" is not in the list.");
    } else {
      list.remove(shortForm);
    }
  }

  /**
   * Check if the list contains given short form of abbreviation.
   *
   * @param shortForm The short form of abbreviation to check.
   * @return true If list contains given short form; false If shortForm is blank or list does not
   *         contain given short form.
   */
  public boolean contains(String shortForm) {
    if (StringUtils.isBlank(shortForm)) {
      return false;
    } else {
      return list.containsKey(shortForm);
    }
  }

  /**
   * Return the corresponding full form of given short form.
   *
   * <p>
   * Used in updating the abbreviation tags.
   *
   * @param shortForm The short form of abbreviation.
   * @return The full form of the given short form of abbreviation; or null if short form is blank
   *         or list does not contain short form.
   */
  public String fullForm(String shortForm) {
    if (StringUtils.isBlank(shortForm) || !list.containsKey(shortForm)) {
      return null;
    } else {
      return list.get(shortForm);
    }
  }
}
