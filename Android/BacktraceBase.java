package backtraceio.library.base;

import android.os.Looper;
import android.util.Log;


import com.unity3d.player.UnityPlayer;


import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Base Backtrace Android client
 */
public class BacktraceBase {


    public native void HandleNdkCrashes();

    public native void ForwardSignal();

    public native void Throw();

    public native void Crash();


    static {
        System.loadLibrary("backtrace-android-native");
    }

    private static transient String LOG_TAG = BacktraceBase.class.getSimpleName();

    /**
     * Game object name - required by JNI to learn how to call Backtrace Unity plugin
     * when library will detect ANR
     */
    private String gameObjectName;


    /**
     * Unity library callback method name
     */
    private String methodName;

    private static BacktraceBase _instance;
    static public void init (String gameObjectName, String methodName){
        BacktraceBase._instance = new BacktraceBase(gameObjectName,methodName);
    }

    public BacktraceBase(String gameObjectName, String methodName) {
        Log.d(LOG_TAG, "Initializing Backtrace NDK handler");
        this.methodName = methodName;
        this.gameObjectName = gameObjectName;
        startNdkCrashHandler();
    }

    public void startNdkCrashHandler() {
        Log.d(LOG_TAG, "Starting NDK integration");
        HandleNdkCrashes();
    }

    public void CatchNdkCrash(String message, final int sigNo, final String classifier) {
        Log.d(LOG_TAG, "Detected NDK Crash. Information: " + message + " " + sigNo);
        NotifyUnityAboutNdkCrash(message,sigNo,classifier);
        //ForwardSignal();
    }

    public void NativeCrash() {
        Log.d(LOG_TAG, "Crashing application");
        Crash();
    }

    public void NativeThrow() {
        Log.d(LOG_TAG, "Throwing an exception from java NDK");
        Throw();
    }

    public void NotifyUnityAboutNdkCrash(String message, final int sigNo, final String classifier) {
        String stackTrace = stackTraceToString(Looper.getMainLooper().getThread().getStackTrace());
        Log.d(LOG_TAG, stackTrace);
        UnityPlayer.UnitySendMessage(this.gameObjectName, this.methodName, stackTrace + "^" + message + "^" +sigNo + "^"+ classifier);
    }

    public static String stackTraceToString(StackTraceElement[] stackTrace) {
        StringWriter sw = new StringWriter();
        printStackTrace(stackTrace, new PrintWriter(sw));
        return sw.toString();
    }
    public static void printStackTrace(StackTraceElement[] stackTrace, PrintWriter pw) {
        for(StackTraceElement stackTraceEl : stackTrace) {
            pw.println(stackTraceEl);
        }
    }
}
