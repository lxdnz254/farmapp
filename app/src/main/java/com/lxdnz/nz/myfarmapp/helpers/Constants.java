package com.lxdnz.nz.myfarmapp.helpers;


/**
 * Created by alex on 26/07/15.
 */
public class Constants {
    public static final String ARG_SECTION_NUMBER = "section_number";
    /*
    public static final String First64 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAijx+uLMofIuFZaTzJgs1+yOegTHaQOyMJBoYoxUsC0iiFI4tg5UjdulzsiV89XCntTSui+XJe+j4cN";
    public static final String Mid64 = "FTwgfqqui/ML1yCFqim45F3nDdaPeSPBsbf5by4ZM5mKzixjF24F8JQqyyCO6KhP69Gew0yRt1mV1wzexwdHlrFaxr+4kO9hTTsJw8TIg9Z7DlYC17+Le2LASW";
    public static final String End64 = "17XjamxF8VeArxSSOamDrW/q5jjEZJScJLIJj7+8f98VpPSvTUCRb4Priy3tOE6caFkzxSDFbzo40gQzNKDWDcgtbDnosP82h2WUgbxF7TJvxfMVsa21RbM/P2un8Oe7h57qq3WbMR6u8wIDAQAB";
    */
    private static int defaultcover = 1200;
    private static int targetresidual = 1450;


    public static int getDefaultcover() {
        return defaultcover;
    }
    public static void setDefaultcover(int dc){
        defaultcover = dc;
    }

    public static int getTargetresidual() {
        return targetresidual;
    }
    public static void setTargetresidual(int tr){
        targetresidual = tr;
    }

}
