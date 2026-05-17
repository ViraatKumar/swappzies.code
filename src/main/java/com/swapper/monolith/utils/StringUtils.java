package com.swapper.monolith.utils;

public class StringUtils {

    public static long parseStringToLong(String value){
        try{
            return Long.parseLong(value);
        }
        catch(NumberFormatException e){
            throw new NumberFormatException("Failed to parse String to Long" + value);
        }
    }
}

