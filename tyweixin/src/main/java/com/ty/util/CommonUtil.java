package com.ty.util;

import org.apache.commons.codec.binary.Base32;

public class CommonUtil {
    public static String base32Encode(String input){
        Base32 base32 = new Base32();
        return base32.encodeAsString(input.getBytes()).replace("=", "");
    }
    public static String base32Decode(String input){
        Base32 base32 = new Base32();
        return  new String( base32.decode(input));
    }
}
