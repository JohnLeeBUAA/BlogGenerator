package com.trustpoint.bloggenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A singleton class to store all categories.
 *
 * @author zli
 *
 */
public class CategoryList {
  public static List<String> categoryList;
  public static HashMap<String, Integer> categoryCount;

  /**
   * Initialize list from text file.
   */
  static {
    Path targetDir = Paths.get(Value.BASE_DIR + Value.SELF_DIR + Value.CATEGORIES_DIR);
    if (Files.exists(targetDir)) {
      categoryList = new ArrayList<String>();
      categoryCount = new HashMap<String, Integer>();

      try {
        BufferedReader br = new BufferedReader(new FileReader(targetDir.toFile()));
        String line;
        while ((line = br.readLine()) != null) {
          categoryList.add(line);
          categoryCount.put(line, 0);
        }
        br.close();

        Collections.sort(categoryList);
        categoryList.add(0, "");
      } catch (Exception e) {
        Error error = new Error();
        error.initErrorFrame(
            "Exception reading file: " + targetDir.toString() + ", " + e.toString());
      }
    } else {
      Error error = new Error();
      error.initErrorFrame(targetDir.toString() + " does not exists.");
    }
  }

  /**
   * A private constructor.
   *
   * <p>
   * This class should not have any instances.
   * </p>
   */
  private CategoryList() {

  }
}
