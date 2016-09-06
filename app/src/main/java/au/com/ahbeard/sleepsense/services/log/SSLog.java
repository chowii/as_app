package au.com.ahbeard.sleepsense.services.log;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luisramos on 5/09/16.
 */
public class SSLog {

    static LogLevel logLevel = LogLevel.Verbose;
    static String TAG = "Sleepsense";
    static Logger[] loggers;

    public static void initialize(Application app) {
        loggers = new Logger[] {
                new DumbLogger()
        };
    }

    public static void v(String format, Object... args) {
        v(String.format(format, args));
    }

    public static void v(String message) {
        if (!LogLevel.Verbose.lower(logLevel)) {
            for(Logger logger : loggers) {
                logger.log(LogLevel.Verbose, TAG, message);
            }
        }
    }

    public static void d(String format, Object... args) {
        d(String.format(format, args));
    }

    public static void d(String message) {
        if (!LogLevel.Debug.lower(logLevel)) {
            for(Logger logger : loggers) {
                logger.log(LogLevel.Debug, TAG, message);
            }
        }
    }

    public static void i(String format, Object... args) {
        i(String.format(format, args));
    }

    public static void i(String message) {
        if (!LogLevel.Info.lower(logLevel)) {
            for(Logger logger : loggers) {
                logger.log(LogLevel.Info, TAG, message);
            }
        }
    }

    public static void w(String format, Object... args) {
        w(String.format(format, args));
    }

    public static void w(String message) {
        if (!LogLevel.Warn.lower(logLevel)) {
            for(Logger logger : loggers) {
                logger.log(LogLevel.Warn, TAG, message);
            }
        }
    }

    public static void e(String format, Object... args) {
        e(String.format(format, args));
    }

    public static void e(String message) {
        if (!LogLevel.Error.lower(logLevel)) {
            for(Logger logger : loggers) {
                logger.log(LogLevel.Error, TAG, message);
            }
        }
    }
}
