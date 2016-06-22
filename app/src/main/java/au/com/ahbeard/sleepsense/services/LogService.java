package au.com.ahbeard.sleepsense.services;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by neal on 8/04/2016.
 */
public class LogService {

    private static LogService sInstance;

    private int mMaximumLogBufferSize;

    private final List<LogMessage> mLogMessages = new ArrayList<>();


    public enum Level {
        Verbose,
        Debug,
        Info,
        Warn,
        Error
    }

    private PublishSubject<LogMessage> mLogMessagePublishSubject = PublishSubject.create();

    public Observable<LogMessage> getLogObservable() {
        return mLogMessagePublishSubject.onBackpressureBuffer();
    }

    public static void initialize(int maximumLogBufferSize) {
        sInstance = new LogService(maximumLogBufferSize);
    }

    public static LogService instance() {
        return sInstance;
    }

    public LogService(int maximumLogBufferSize) {
        mMaximumLogBufferSize = maximumLogBufferSize;
    }

    public static void verbose(String tag, String message) {
        sInstance.log(Level.Verbose, tag, message, null);
    }

    public static void verbose(String tag, String message, Throwable t) {
        sInstance.log(Level.Verbose, tag, message, t);
    }

    public static void d(String tag, String message) {
        sInstance.log(Level.Debug, tag, message, null);
    }

    public static void d(String tag, String message, Throwable t) {
        sInstance.log(Level.Debug, tag, message, t);
    }

    public static void i(String tag, String message) {
        sInstance.log(Level.Info, tag, message, null);
    }

    public static void i(String tag, String message, Throwable t) {
        sInstance.log(Level.Info, tag, message, t);
    }

    public static void w(String tag, String message) {
        sInstance.log(Level.Warn, tag, message, null);
    }

    public static void w(String tag, String message, Throwable t) {
        sInstance.log(Level.Warn, tag, message, t);
    }

    public static void e(String tag, String message) {
        sInstance.log(Level.Error, tag, message, null);
    }

    public static void e(String tag, String message, Throwable t) {
        sInstance.log(Level.Error, tag, message, t);
    }

    public static void s(String tag, String message) {
        sInstance.log(Level.Info, tag, message, null);
    }

    public static void s(String tag, String message, Throwable t) {
        sInstance.log(Level.Info, tag, message, t);
    }

    public void log(Level level, String tag, String message, Throwable t) {

        LogMessage logMessage = new LogMessage();

        logMessage.setTimestamp(System.currentTimeMillis());
        logMessage.setLevel(level);
        logMessage.setTag(tag);

        if (message.length() <= 1024) {
            logMessage.setMessage(message);
        } else {
            logMessage.setMessage(message.substring(0, 1024));
        }

        logMessage.setT(t);

        synchronized (mLogMessages) {
            if (mLogMessages.size() >= mMaximumLogBufferSize) {
                mLogMessages.remove(0);
            }

            mLogMessages.add(logMessage);
        }

        mLogMessagePublishSubject.onNext(logMessage);

    }

    public List<LogMessage> getLogMessages() {
        List<LogMessage> logMessages = new ArrayList<>();
        synchronized (mLogMessages) {
            logMessages.addAll(mLogMessages);
        }
        return logMessages;
    }

    public List<LogMessage> getLogMessages(int maxLogItems) {
        List<LogMessage> logMessages = new ArrayList<>();

        synchronized (mLogMessages){
            if ( logMessages.size() <= maxLogItems) {
                logMessages.addAll(mLogMessages);
            } else {
                for ( int i=mLogMessages.size()-maxLogItems; i < mLogMessages.size(); i++ ) {
                    logMessages.add(mLogMessages.get(i));
                }
            }
        }
        return logMessages;
    }


    public static class LogMessage {

        private long timestamp;
        private Level level;
        private String tag;
        private String message;
        private Throwable t;

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public Level getLevel() {
            return level;
        }

        public void setLevel(Level level) {
            this.level = level;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Throwable getT() {
            return t;
        }

        public void setT(Throwable t) {
            this.t = t;
        }
    }


}
