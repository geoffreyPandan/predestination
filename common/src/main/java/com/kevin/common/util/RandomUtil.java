package com.kevin.common.util;

import java.security.SecureRandom;

/**
 * RandomUtil
 *
 * @author: sunjie
 * @date: 15/9/24
 */


public abstract class RandomUtil {

    private static char[] characts = " ABCDEFGHJKLMNPQRSTUVWXYZ0123456789".toCharArray();

    /**
     * 取得指定长度的随机数字
     *
     * @param len
     * @return
     * @throws Exception
     */
    public static String getRandNum(int len) throws Exception {
        if (len <= 0) throw new Exception("len must bigger than 0.");
        String s = String.valueOf(Math.random());
        return s.substring(4, 4 + len);
    }

    private static SecureRandom random = new SecureRandom();

    public static synchronized int getRand(int max) {
        return random.nextInt(max);
    }

    /**
     * 取得4位随机数字
     *
     * @return
     * @throws Exception
     */
    public static String getRand4Num() {
        try {
            return getRandNum(4);
        } catch (Exception exp) {
            return "7907";
        }
    }

    public static String getRandChar(int len) {
        String sResult = "";
        int i = 0;
        while (true) {
            String s = String.valueOf(Math.random());
            i = Integer.parseInt(s.substring(4, 6));
            if (i <= 0 || i > 34) continue;
            sResult += characts[i];
            if (sResult.length() >= len) break;
        }
        return sResult;
    }
}

