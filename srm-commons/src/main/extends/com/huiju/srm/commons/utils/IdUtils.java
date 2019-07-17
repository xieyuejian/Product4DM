package com.huiju.srm.commons.utils;

import java.util.Random;

/**
 * @author Huibaba
 */
public class IdUtils {
    /**
     * 商品id生成
     */
    public static long genNextId() {
        //取当前时间的长整形值包含毫秒
        long millis = System.currentTimeMillis();
        //加上两位位随机数
        Random random = new Random();
        int end2 = random.nextInt(99);
        //如果不足两位前面补0
        String str = millis + String.format("%02d", end2);
        long id = new Long(str);
        return id;
    }

    public static void main(String[] args) {
        System.out.println("" + genNextId());
    }

}
