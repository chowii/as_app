package au.com.ahbeard.sleepsense.services.log;

import android.util.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import timber.log.Timber;

/**
 * Created by luisramos on 7/09/16.
 */
public class SSLogToFileTree extends Timber.DebugTree {

    Logger logger;
    int currPriority;

    SSLogToFileTree(String filePath, int priority) {
        currPriority = priority;
        logger = LoggerFactory.getLogger(SSLogToFileTree.class);
        setupLogger(filePath);
    }

    @Override
    protected boolean isLoggable(int priority) {
        return priority >= currPriority;
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        String logMessage = tag + ": " + message;
        switch (priority) {
            case Log.DEBUG:
                logger.debug(logMessage);
                break;
            case Log.INFO:
                logger.info(logMessage);
                break;
            case Log.WARN:
                logger.warn(logMessage);
                break;
            case Log.ERROR:
                logger.error(logMessage);
                break;
        }
    }

    private void setupLogger(String filePath) {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.reset();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(lc);
        encoder.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
        encoder.start();

        SizeAndTimeBasedFNATP<ILoggingEvent> sizeAndTimeBasedFNATP= new SizeAndTimeBasedFNATP<>();
        sizeAndTimeBasedFNATP.setMaxFileSize("5MB");

        TimeBasedRollingPolicy<ILoggingEvent> timeBasedRollingPolicy = new TimeBasedRollingPolicy<>();
        timeBasedRollingPolicy.setFileNamePattern(filePath + ".%d{yyyy-MM-dd}.%i.html");
        timeBasedRollingPolicy.setTimeBasedFileNamingAndTriggeringPolicy(sizeAndTimeBasedFNATP);
        timeBasedRollingPolicy.setMaxHistory(15);

        RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<>();
        fileAppender.setContext(lc);
        fileAppender.setFile(filePath);
        fileAppender.setRollingPolicy(timeBasedRollingPolicy);

        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.addAppender(fileAppender);
    }
}
