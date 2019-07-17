package com.huiju.srm.commons.utils;

/**
 * 中文大写金额转换工具类
 * 
 * @author administrator
 * 
 */
public class CnMoneyFormat {
    
    public static String[] chineseDigits = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };

    /**
     * 格式转换
     * @param amount double类型的金额
     * @return 大写金额
     */
    public static String format(double amount) {
        if ((amount > 1.0E+018D) || (amount < -1.0E+018D))
            throw new IllegalArgumentException("参数值超出允许范围 (-999999999999999999.99 ～ 999999999999999999.99)！");
        boolean negative = false;
        if (amount < 0.0D) {
            negative = true;
            amount *= -1.0D;
        }
        long temp = Math.round(amount * 100.0D);
        int numFen = (int) (temp % 10L);
        temp /= 10L;
        int numJiao = (int) (temp % 10L);
        temp /= 10L;
        int[] parts = new int[20];
        int numParts = 0;
        for (int i = 0; temp != 0L; i++) {
            int part = (int) (temp % 10000L);
            parts[i] = part;
            numParts++;
            temp /= 10000L;
        }

        boolean beforeWanIsZero = true;
        String chineseStr = "";
        for (int i = 0; i < numParts; i++) {
            String partChinese = partTranslate(parts[i]);
            if (i % 2 == 0)
                if ("".equals(partChinese))
                    beforeWanIsZero = true;
                else
                    beforeWanIsZero = false;
            if (i != 0)
                if (i % 2 == 0) {
                    chineseStr = "亿" + chineseStr;
                } else if (("".equals(partChinese)) && (!beforeWanIsZero)) {
                    chineseStr = "零" + chineseStr;
                } else {
                    if ((parts[(i - 1)] < 1000) && (parts[(i - 1)] > 0))
                        chineseStr = "零" + chineseStr;
                    chineseStr = "万" + chineseStr;
                }
            chineseStr = partChinese + chineseStr;
        }

        if ("".equals(chineseStr)) {
            chineseStr = chineseDigits[0];
        } else if (negative)
            chineseStr = "负" + chineseStr;
        chineseStr = chineseStr + "元";
        if ((numFen == 0) && (numJiao == 0)) {
            chineseStr = chineseStr + "整";
        } else if (numFen == 0) {
            chineseStr = chineseStr + chineseDigits[numJiao] + "角";
        } else if (numJiao == 0)
            chineseStr = chineseStr + "零" + chineseDigits[numFen] + "分";
        else
            chineseStr = chineseStr + chineseDigits[numJiao] + "角" + chineseDigits[numFen] + "分";
        return chineseStr;
    }

    private static String partTranslate(int amountPart) {
        if ((amountPart < 0) || (amountPart > 10000))
            throw new IllegalArgumentException("参数必须是大于等于 0，小于 10000 的整数！");
        String[] units = { "", "拾", "佰", "仟" };

        int temp = amountPart;
        String amountStr = new Integer(amountPart).toString();
        int amountStrLength = amountStr.length();
        boolean lastIsZero = true;
        String chineseStr = "";
        for (int i = 0; (i < amountStrLength) && (temp != 0); i++) {
            int digit = temp % 10;
            if (digit == 0) {
                if (!lastIsZero)
                    chineseStr = "零" + chineseStr;
                lastIsZero = true;
            } else {
                chineseStr = chineseDigits[digit] + units[i] + chineseStr;
                lastIsZero = false;
            }
            temp /= 10;
        }

        return chineseStr;
    }
}