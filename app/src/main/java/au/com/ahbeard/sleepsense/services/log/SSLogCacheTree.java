package au.com.ahbeard.sleepsense.services.log;

import java.util.LinkedList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by luisramos on 7/09/2016.
 */
public class SSLogCacheTree extends Timber.DebugTree {

    private static int MAX_SIZE = 128;
    private LinkedList<String> logs = new LinkedList<>();

    public List<String> getLogs() {
        return logs;
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        String logMessage = tag + ": " + message;
        if (logs.size() >= MAX_SIZE) {
            logs.removeLast();
        }
        logs.addFirst(logMessage);
    }
}
