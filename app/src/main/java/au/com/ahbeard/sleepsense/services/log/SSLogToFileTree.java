package au.com.ahbeard.sleepsense.services.log;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import au.com.ahbeard.sleepsense.BuildConfig;
import timber.log.Timber;

/**
 * Created by luisramos on 7/09/16.
 */
public class SSLogToFileTree extends Timber.DebugTree {

    static String LOG_DIR = BuildConfig.APPLICATION_ID;
    static String LOG_FILE = LOG_DIR + "/sleepsense.log";

    int currPriority;
    File logFile;
    SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss", Locale.US);

    SSLogToFileTree(int priority) {
        currPriority = priority;

        File logDir = new File(Environment.getExternalStorageDirectory(), LOG_DIR);
        logFile = new File(Environment.getExternalStorageDirectory(), LOG_FILE);
        try {
            if (!logFile.exists()) {
                logDir.mkdirs();
                logFile.createNewFile();
            }
        } catch (IOException e) {
            //Error file not created
            e.printStackTrace();
        }
    }

    public static String getLogFilePath() {
        return Environment.getExternalStorageDirectory() + "/" + LOG_FILE;
    }

    @Override
    protected boolean isLoggable(int priority) {
        return priority >= currPriority;
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        String level = "D";
        switch (priority) {
            case Log.DEBUG:
                level = "D";
                break;
            case Log.INFO:
                level = "I";
                break;
            case Log.WARN:
                level = "W";
                break;
            case Log.ERROR:
                level = "E";
                break;
        }
        String logMessage = String.format("%s %s/%s: %s", df.format(new Date()), level, tag, message);
        writeToFile(logMessage);
    }

    private void writeToFile(String message) {
        if (!logFile.exists()) { return; }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
            writer.append(message);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Couldn't write to file
        }
    }
}
