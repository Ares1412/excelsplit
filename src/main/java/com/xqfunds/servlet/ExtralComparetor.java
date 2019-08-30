package com.xqfunds.servlet;

import java.util.Comparator;
import java.util.List;

public class ExtralComparetor implements Comparator<List> {

    Integer[] compareFields;

    public ExtralComparetor(Integer[] args){
        this.compareFields = args;
    }

    @Override
    public int compare(List o1,List o2){
        int result = 0;

        List<String> l1 = o1;
        List<String> l2 = o2;
        for (int i = 0; i < compareFields.length; i++) {
            try {
                String temp1 = l1.get(compareFields[i]);
                String temp2 = l2.get(compareFields[i]);

                result = temp1.compareTo(temp2);
                if (0 == result) {
                    continue;
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
