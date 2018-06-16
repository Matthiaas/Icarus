package com.app.util;

import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class log {
    public static String getFileLineMethod() {
        StackTraceElement traceElement = new Exception().getStackTrace()[1];
        return new StringBuffer("[").append(traceElement.getFileName()).append(" | ").append(traceElement.getLineNumber()).append(" | ").append(traceElement.getMethodName()).append("()").append("]").toString();
    }

    public static String file() {
        return new Exception().getStackTrace()[1].getFileName();
    }

    public static String func() {
        return new Exception().getStackTrace()[1].getMethodName();
    }

    public static String line() {
        return String.valueOf(new Exception().getStackTrace()[1].getLineNumber());
    }

    public static String curTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(0));
    }

    public static void m3v(String msg) {
        StackTraceElement traceElement = new Exception().getStackTrace()[1];
        Log.v(new StringBuffer("[").append(traceElement.getFileName()).append(" | ").append(traceElement.getLineNumber()).append(" | ").append(traceElement.getMethodName()).append("()").append("]").toString(), msg);
    }

    public static void m0d(String msg) {
        StackTraceElement traceElement = new Exception().getStackTrace()[1];
        Log.d(new StringBuffer("[").append(traceElement.getFileName()).append(" | ").append(traceElement.getLineNumber()).append(" | ").append(traceElement.getMethodName()).append("()").append("]").toString(), msg);
    }

    public static void m2i(String msg) {
        StackTraceElement traceElement = new Exception().getStackTrace()[1];
        Log.i(new StringBuffer("[").append(traceElement.getFileName()).append(" | ").append(traceElement.getLineNumber()).append(" | ").append(traceElement.getMethodName()).append("()").append("]").toString(), msg);
    }

    public static void m4w(String msg) {
        StackTraceElement traceElement = new Exception().getStackTrace()[1];
        Log.w(new StringBuffer("[").append(traceElement.getFileName()).append(" | ").append(traceElement.getLineNumber()).append(" | ").append(traceElement.getMethodName()).append("()").append("]").toString(), msg);
    }

    public static void m1e(String msg) {
        StackTraceElement traceElement = new Exception().getStackTrace()[1];
        Log.e(new StringBuffer("[").append(traceElement.getFileName()).append(" | ").append(traceElement.getLineNumber()).append(" | ").append(traceElement.getMethodName()).append("()").append("]").toString(), msg);
    }
}
