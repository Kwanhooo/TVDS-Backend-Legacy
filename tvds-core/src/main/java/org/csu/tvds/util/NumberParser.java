package org.csu.tvds.util;

import org.springframework.stereotype.Component;

@Component
public class NumberParser {
    public String parseTwoDigits(int num) {
        if (num < 10) {
            return "0" + num;
        } else {
            return String.valueOf(num);
        }
    }
}
