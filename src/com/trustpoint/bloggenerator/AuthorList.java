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

import org.apache.commons.lang3.StringUtils;

/**
 * Store the list of all authors
 *
 * @author zli
 *
 */
public class AuthorList
{
    public static List<String> nameList;
    public static HashMap<String, String> nameToCode; // <name, code>

    /**
     * Initialize the list from text file
     */
    public static void init()
    {
        Path targetDir = Paths.get(Value.BASE_DIR + Value.SELF_DIR + Value.AUTHORS_DIR);
        if (Files.exists(targetDir)) {
            nameList = new ArrayList<String>();
            nameToCode = new HashMap<String, String>();

            try {
                BufferedReader br = new BufferedReader(new FileReader(targetDir.toFile()));
                String line;
                while ((line = br.readLine()) != null) {
                    String[] authorRecord = StringUtils.split(line, ':');
                    nameList.add(authorRecord[1]);
                    nameToCode.put(authorRecord[1], authorRecord[0]);
                }
                br.close();

                Collections.sort(nameList);
                nameList.add(0, "");
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
     * Get the name code of a name
     *
     * @param name
     * @return
     */
    public static String getCode(String name)
    {
        return nameToCode.get(name);
    }
}
