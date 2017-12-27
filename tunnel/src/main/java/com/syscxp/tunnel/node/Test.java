package com.syscxp.tunnel.node;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        List<String> list1 = new ArrayList();
        List<String> list2 = new ArrayList();
        System.out.println(list1.add("11"));
        list1.add("22");

        list2.add("2");
        list2.add("3");
        System.out.println(list2.addAll(list1));

        System.out.println(list1.toString());
        System.out.println(list2.toString());

    }
}
