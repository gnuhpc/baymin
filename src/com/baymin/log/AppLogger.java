package com.baymin.log;

import android.util.Log;

public class AppLogger {
    protected static final String TAG = "BayMin";
    public static boolean DEBUG = true;

    public AppLogger() {
    }

    /**
     * Send a VERBOSE log message.
     *
     * @param msg The message you would like logged.
     */
    public static void v(String msg) {
    	if (DEBUG) {
	        Log.v(TAG, buildMessage(msg));
		}
    }

    /**
     * Send a VERBOSE log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void v(String msg, Throwable thr) {
    	if (DEBUG) {
	        Log.v(TAG, buildMessage(msg), thr);
		}
    }

    /**
     * Send a DEBUG log message.
     *
     * @param msg
     */
    public static void d(String msg) {
    	if (DEBUG) {
	        Log.d(TAG, buildMessage(msg));
		}
    }

    /**
     * Send a DEBUG log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param thr  An exception to log
     */
    public static void d(String msg, Throwable thr) {
    	if (DEBUG) {
	        Log.d(TAG, buildMessage(msg), thr);
		}
    }

    /**
     * Send an INFO log message.
     *
     * @param msg The message you would like logged.
     */
    public static void i(String msg) {
    	if (DEBUG) {
	        Log.i(TAG, buildMessage(msg));
		}
    }

    /**
     * Send a INFO log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void i(String msg, Throwable thr) {
    	if (DEBUG) {
	        Log.i(TAG, buildMessage(msg), thr);
		}
    }

    /**
     * Send an ERROR log message.
     *
     * @param msg The message you would like logged.
     */
    public static void e(String msg) {
    	if (DEBUG) {
	        Log.e(TAG, buildMessage(msg));
		}
    }

    /**
     * Send a WARN log message
     *
     * @param msg The message you would like logged.
     */
    public static void w(String msg) {
    	if (DEBUG) {
	        Log.w(TAG, buildMessage(msg));
		}
    }

    /**
     * Send a WARN log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void w(String msg, Throwable thr) {
    	if (DEBUG) {
	        Log.w(TAG, buildMessage(msg), thr);
		}
    }

    /**
     * Send an empty WARN log message and log the exception.
     *
     * @param thr An exception to log
     */
    public static void w(Throwable thr) {
    	if (DEBUG) {
	        Log.w(TAG, buildMessage(""), thr);
		}
    }

    /**
     * Send an ERROR log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void e(String msg, Throwable thr) {
    	if (DEBUG) {
	        Log.e(TAG, buildMessage(msg), thr);
		}
    }

    /**
     * Building Message
     *
     * @param msg The message you would like logged.
     * @return Message String
     */
    protected static String buildMessage(String msg) {
        StackTraceElement caller = new Throwable().fillInStackTrace().getStackTrace()[2];
        String[] infos = getAutoJumpLogInfos();
        return new StringBuilder()
                .append(infos[0])
                .append(".")
                .append(infos[1])
                .append("(): ")
                .append(msg)
                .append(" Line: ")
                .append(infos[2]).toString();
    }
    
	private static String[] getAutoJumpLogInfos() {
		String[] infos = new String[] { "", "", "" };
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		if (elements.length < 5) {
			Log.e(TAG, "Stack is too shallow!!!");
			return infos;
		} else {
			infos[0] = elements[5].getClassName().substring(
					elements[5].getClassName().lastIndexOf(".") + 1);
			infos[1] = elements[5].getMethodName() + "()";
			infos[2] = " at (" + elements[5].getClassName() + ".java:"
					+ elements[5].getLineNumber() + ")";
			return infos;
		}
	}
}

