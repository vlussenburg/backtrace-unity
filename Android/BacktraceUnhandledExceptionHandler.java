package backtrace.io.backtrace_unity_android_plugin;

import android.os.Build;
import android.os.Looper;
import android.util.Log;

import com.unity3d.player.UnityPlayer;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Handle unhandled Android exceptions from background threads.
 */
public class BacktraceUnhandledExceptionHandler implements Thread.UncaughtExceptionHandler{
    private final static transient String LOG_TAG = BacktraceUnhandledExceptionHandler.class.getSimpleName();
    private final Thread.UncaughtExceptionHandler rootHandler;

    private final String _gameObject;
    private final String _methodName;

    public BacktraceUnhandledExceptionHandler(String gameObject, String methodName) {
        Log.d(LOG_TAG, "Initializing Android unhandled exception handler");
        this._gameObject = gameObject;
        this._methodName = methodName;

        rootHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable exception) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            if(Looper.getMainLooper().getThread().getId() == thread.getId()) {
                // prevent from sending exception happened to main thread - we will catch them via unity logger
                return;
            }
            NotifyAboutThreadExcepiton(exception.getClass().getName() + " : " + exception.getMessage(), stackTraceToString(exception.getStackTrace()));
        }
    }


    public void NotifyAboutThreadExcepiton(String message, String stackTrace) {        
        UnityPlayer.UnitySendMessage(this._gameObject, this._methodName, message + '\n' + stackTrace);
    }

    private static String stackTraceToString(StackTraceElement[] stackTrace) {
        StringWriter sw = new StringWriter();
        printStackTrace(stackTrace, new PrintWriter(sw));
        return sw.toString();
    }

    private static void printStackTrace(StackTraceElement[] stackTrace, PrintWriter pw) {
        for(StackTraceElement stackTraceEl : stackTrace) {
            pw.println(stackTraceEl);
        }
    }
}