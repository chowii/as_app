package au.com.ahbeard.sleepsense.services.log;

import android.app.Application;
import android.util.Log;

import java.util.List;

import au.com.ahbeard.sleepsense.BuildConfig;
import timber.log.Timber;

/**
 * Created by luisramos on 5/09/16.
 */
public class SSLog {

    static int priority = Log.DEBUG;

    private static SSLogCacheTree logCache;

    public static void initialize(Application app) {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            logCache = new SSLogCacheTree();
            Timber.plant(logCache);
        }
        Timber.plant(new SSLogToFileTree(priority));
    }

    public static void d(String format, Object... args) {
        Timber.d(format, args);
    }

    public static void i(String format, Object... args) {
        Timber.i(format, args);
    }

    public static void w(String format, Object... args) {
        Timber.w(format, args);
    }

    public static void e(String format, Object... args) {
        Timber.e(format, args);
    }

    public static List<String> getLogMessages() {
        return logCache.getLogs();
    }

    public static String getLogFilePath() {
        return SSLogToFileTree.getLogFilePath();
    }
}
