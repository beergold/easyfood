package com.wjj.worker.framework.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常信息处理
 *
 * @author BeerGod
 */
public class ExceptionUtil {

    /**
     * 处理异常信息转换为 String
     *
     * @param throwable 异常信息主体
     * @return
     */
    public static String getStackTraceMessage(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        String var3;
        try {
            throwable.printStackTrace(pw);
            var3 = sw.toString();
        } finally {
            pw.close();
        }

        return var3;
    }
}
