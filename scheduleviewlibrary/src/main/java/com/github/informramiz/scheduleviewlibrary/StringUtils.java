package com.github.informramiz.scheduleviewlibrary;

import java.util.List;

/**
 * Created by ramiz on 2/21/18.
 */
public class StringUtils {
    public static boolean areEqual(List<String> list1, List<String> list2) {
        if (list1.size() != list2.size()) {
            return false;
        } else if (list1.isEmpty()) {
            //both lists are empty so they are equal
            return true;
        }

        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i).equalsIgnoreCase(list2.get(i))) {
                return false;
            }
        }

        return true;
    }
}
