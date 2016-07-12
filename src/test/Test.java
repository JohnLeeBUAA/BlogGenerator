package test;

import java.util.ArrayList;
import java.util.List;

public class Test
{
    public static void main(String[] args)
    {
        List<String> list = new ArrayList<>();
        list.add("first string");
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }

        list.add(0, "second string");

        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
        System.out.println(list.get(1));
    }

}
