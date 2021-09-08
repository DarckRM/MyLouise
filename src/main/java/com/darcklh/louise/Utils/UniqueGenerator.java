package com.darcklh.louise.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * UniqueCode生成器
 */
public class UniqueGenerator {

    public static Integer randomInt() {
        return 0;
    }

    /**
     *
     * @param format 格式化 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String uniqueDateID(String format) {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat(format);

        return df.format(date);
    }

    /**
     *
     * @param name 线程基本名
     * @return
     */
    public static String uniqueThreadName(String name, String detail) {
        String threadName = name + "-" + uniqueDateID("HH:mm:ss") + "-" + detail;
        return threadName;
    }

}
