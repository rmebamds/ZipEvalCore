package com.rcmccormick.williamssonoma.app;

import com.rcmccormick.williamssonoma.domain.ZipRangeList;

public class ZipCodeEvalApp {

    public static void main(String[] args) {
        //take input and make a range, perhaps output the resulting range
        try {
            System.out.println("Results: " + new ZipRangeList(args).combine().toString());
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
