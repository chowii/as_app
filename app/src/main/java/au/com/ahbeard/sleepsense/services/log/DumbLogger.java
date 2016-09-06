package au.com.ahbeard.sleepsense.services.log;

import android.util.Log;

/**
 * Created by luisramos on 6/09/16.
 */
public class DumbLogger implements Logger {
    @Override
    public void log(LogLevel level, String tag, String message) {
        switch (level) {
            case Verbose:
                Log.v(tag, message);
                break;
            case Debug:
                Log.d(tag, message);
                break;
            case Info:
                Log.i(tag, message);
                break;
            case Warn:
                Log.w(tag, message);
                break;
            case Error:
                Log.e(tag, message);
                break;
        }
    }
}
