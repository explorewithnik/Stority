package com.app.homeSpace.helper;

import android.util.Log;

import com.google.gson.Gson;

public class Logger {

    private static final int LIMIT_LOG = 200;

    public static void e(String TAG, String message) {
        if (Constants.DEBUG_MODE) {
            StringBuilder msg = new StringBuilder();

            int prevIndex = 0, lastIndex;
            for (int i = 0; i < message.length() / LIMIT_LOG + 1; i++) {
                lastIndex = ((((i + 1) * LIMIT_LOG) > message.length()) ? message.length() : (i + 1) * LIMIT_LOG);
                msg.append(message.substring(prevIndex, lastIndex)).append("\n");
                prevIndex = lastIndex;
            }

            Log.e(TAG, msg.toString());
        }
    }

    public static void e(Thread current, String message) {
        if (Constants.DEBUG_MODE) {
            StackTraceElement[] stack = current.getStackTrace();
            StackTraceElement element = stack[3];
            if (!element.isNativeMethod()) {

                String fileName = element.getFileName();
                if (fileName.contains("."))
                    fileName = fileName.substring(0, fileName.indexOf("."));

                int lineNumber = element.getLineNumber();
                String methodName = element.getMethodName();

                Logger.e(fileName + "(" + lineNumber + ")",
                        methodName + "()" + " : " + message);
            }
        }
    }

    public static void i(String TAG, String message) {

        if (Constants.DEBUG_MODE) {
            StringBuilder msg = new StringBuilder();

            int prevIndex = 0, lastIndex;
            for (int i = 0; i < message.length() / LIMIT_LOG + 1; i++) {

                lastIndex = ((((i + 1) * LIMIT_LOG) > message.length()) ? message.length() : (i + 1) * LIMIT_LOG);
                msg.append(message.substring(prevIndex, lastIndex)).append("\n");
                prevIndex = lastIndex;
            }

            Log.i(TAG, msg.toString());
        }
    }

    public static void i(Thread current, String message) {
        if (Constants.DEBUG_MODE) {
            StackTraceElement[] stack = current.getStackTrace();
            StackTraceElement element = stack[3];
            if (!element.isNativeMethod()) {

                String fileName = element.getFileName();
                if (fileName.contains("."))
                    fileName = fileName.substring(0, fileName.indexOf("."));

                int lineNumber = element.getLineNumber();
                String methodName = element.getMethodName();

                Logger.i(fileName + "(" + lineNumber + ")",
                        methodName + "()" + " : " + message);
            }
        }
    }

    private static void d(String TAG, String message) {
        if (Constants.DEBUG_MODE)
            Log.d(TAG, message);
    }

    public static void d(Thread current, String message) {
        if (Constants.DEBUG_MODE) {
            StackTraceElement[] stack = current.getStackTrace();
            StackTraceElement element = stack[3];
            if (!element.isNativeMethod()) {

                String fileName = element.getFileName();
                if (fileName.contains("."))
                    fileName = fileName.substring(0, fileName.indexOf("."));

                int lineNumber = element.getLineNumber();
                String methodName = element.getMethodName();

                Logger.d(fileName + "(" + lineNumber + ")",
                        methodName + "()" + " : " + message);
            }
        }
    }

    public static void o(Thread current, Object obj) {
        try {
            if (Constants.DEBUG_MODE) {
                StackTraceElement[] stack = current.getStackTrace();
                StackTraceElement element = stack[3];
                if (!element.isNativeMethod()) {

                    String fileName = element.getFileName();
                    if (fileName.contains("."))
                        fileName = fileName.substring(0, fileName.indexOf("."));

                    int lineNumber = element.getLineNumber();
                    String methodName = element.getMethodName();

                    if (obj == null) {
                        Logger.i(fileName + "(" + lineNumber + ")",

                                methodName + "()" + " : NULL : " + new Gson().toJson(obj));
                    } else {
                        Logger.i(fileName + "(" + lineNumber + ")",

                                methodName + "()" + " : " + obj.getClass() + " : " + new Gson().toJson(obj));
                    }
                }
            }
        } catch (Exception e) {
            if (Constants.DEBUG_MODE) e.printStackTrace();
        }
    }
}

