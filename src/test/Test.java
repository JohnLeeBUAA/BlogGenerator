package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Test
{

    public static void main(String[] args)
    {
        List<String> list = new ArrayList<String>();
        list.add("a");
        list.add("bbb");
        list.add("b");
        Collections.sort(list);

        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }

        String result = StringUtils.join(list, " ");
        System.out.println(result);

        String[] stringlist = new String[]{"aa","bb","cc"};
        list = Arrays.asList(stringlist);
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }

        list.clear();
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
    }

}
