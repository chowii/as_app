package au.com.ahbeard.sleepsense.services.log;

import android.util.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import au.com.ahbeard.sleepsense.BuildConfig;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import timber.log.Timber;

/**
 * Created by luisramos on 7/09/16.
 */
public class SSLogToFileTree extends Timber.DebugTree {

    Logger logger;
    int currPriority;

    SSLogToFileTree(String filePath, String fileName, int priority) {
        currPriority = priority;
        logger = LoggerFactory.getLogger(SSLogToFileTree.class);
        configureLogbackByString(filePath, fileName);
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

    static final String LOGBACK_XML =
            "<configuration"+ (BuildConfig.DEBUG ? "debug='true'" : "") + ">" +
                "<appender name=\"ROLLING\" class=\"ch.qos.logback.core.rolling.RollingFileAppender\">" +
                    "<file>${LOG_DIR}/${LOG_FILENAME}</file>" +
                    "<rollingPolicy class=\"ch.qos.logback.core.rolling.TimeBasedRollingPolicy\">" +
                        "<fileNamePattern>${LOG_DIR}/sleepsense.%d{yyyy-MM-dd}.%i.log</fileNamePattern>" +
                        "<timeBasedFileNamingAndTriggeringPolicy" +
                            "class=\"ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP\">" +
                            "<maxFileSize>5MB</maxFileSize>" +
                        "</timeBasedFileNamingAndTriggeringPolicy>" +
                        "<maxHistory>15</maxHistory>" +
                    "</rollingPolicy>" +
                "</appender>" +
                "<root level=\"DEBUG\">" +
                    "<appender-ref ref=\"ROLLING\" />" +
                "</root>" +
            "</configuration>";

    private void configureLogbackByString(String logFileDir, String logFileName) {
        // reset the default context (which may already have been initialized)
        // since we want to reconfigure it
        LoggerContext lc = (LoggerContext)LoggerFactory.getILoggerFactory();
        lc.reset();

        JoranConfigurator config = new JoranConfigurator();
        config.setContext(lc);

        String xml = LOGBACK_XML.replace("${LOG_DIR}", logFileDir).replace("${LOG_FILENAME}", logFileName);
        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        try {
            config.doConfigure(stream);
        } catch (JoranException e) {
            e.printStackTrace();
        }
    }
}
