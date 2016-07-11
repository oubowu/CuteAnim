package com.oushangfeng.cuteanim;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);

        String date = "2016年12月11日";
        String dates = date.replace("年","-").replace("月","-").replace("日","");

        System.out.println(dates);

    }
}