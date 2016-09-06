package au.com.ahbeard.sleepsense.services.log;

/**
 * Created by luisramos on 5/09/16.
 */
public enum LogLevel {
    Verbose(0),
    Debug(1),
    Info(2),
    Warn(3),
    Error(4);

    private int intValue = 0;
    LogLevel(int i) {
        this.intValue = i;
    }

    public boolean lower(LogLevel level) {
        return this.intValue < level.intValue;
    }
}
