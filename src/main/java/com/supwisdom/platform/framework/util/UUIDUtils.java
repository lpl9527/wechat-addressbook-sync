package com.supwisdom.platform.framework.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 本类的作用是通过调 用生成一个随机的主键值
 * @author hush
 */
public class UUIDUtils {

    private static boolean IS_THREADLOCALRANDOM_AVAILABLE = false;
    private static Random random;
    private static final long leastSigBits;
    private static final ReentrantLock lock = new ReentrantLock();
    private static long lastTime;

    static {
        try {
            IS_THREADLOCALRANDOM_AVAILABLE = null != UUIDUtils.class.getClassLoader().loadClass(
                    "java.util.concurrent.ThreadLocalRandom");
        } catch (ClassNotFoundException e) {
        }

        byte[] seed = new SecureRandom().generateSeed(8);
        leastSigBits = new BigInteger(seed).longValue();
        if (!IS_THREADLOCALRANDOM_AVAILABLE) {
            random = new Random(leastSigBits);
        }
    }

    private UUIDUtils() {}

    /**
     * 生成32位随机码
     * @return
     */
    public static String random() {
        byte[] randomBytes = new byte[16];
        if (IS_THREADLOCALRANDOM_AVAILABLE) {
            java.util.concurrent.ThreadLocalRandom.current().nextBytes(randomBytes);
        } else {
            random.nextBytes(randomBytes);
        }

        long mostSigBits = 0;
        for (int i = 0; i < 8; i++) {
            mostSigBits = (mostSigBits << 8) | (randomBytes[i] & 0xff);
        }
        long leastSigBits = 0;
        for (int i = 8; i < 16; i++) {
            leastSigBits = (leastSigBits << 8) | (randomBytes[i] & 0xff);
        }

        return new UUID(mostSigBits, leastSigBits).toString().replaceAll("-", "");
    }

    /**
     * 生成32位随机码
     * @return
     */
    public static String create() {
        long timeMillis = (System.currentTimeMillis() * 10000) + 0x01B21DD213814000L;

        lock.lock();
        try {
            if (timeMillis > lastTime) {
                lastTime = timeMillis;
            } else {
                timeMillis = ++lastTime;
            }
        } finally {
            lock.unlock();
        }

        long mostSigBits = timeMillis << 32;
        mostSigBits |= (timeMillis & 0xFFFF00000000L) >> 16;
        mostSigBits |= 0x1000 | ((timeMillis >> 48) & 0x0FFF); 
        return new UUID(mostSigBits, leastSigBits).toString().replaceAll("-", "");
        
    }
    
    public static void main(String[] args){
        System.out.println(random());
        System.out.println(create());
    }

}
