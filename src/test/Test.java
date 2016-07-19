package test;

import org.apache.commons.lang3.StringUtils;

public class Test
{

    public static void main(String[] args)
    {
        String teString = " a  b c    d ";
        String[] aStrings = StringUtils.split(teString);
        System.out.println(aStrings.length);
        for (int i = 0; i < aStrings.length; i++) {
            System.out.println(aStrings[i]);
        }
    }

}
