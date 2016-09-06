package au.com.ahbeard.sleepsense.services.log;

/**
 * Created by luisramos on 6/09/16.
 */
public interface Logger {
    void log (LogLevel level, String tag, String message);
}
