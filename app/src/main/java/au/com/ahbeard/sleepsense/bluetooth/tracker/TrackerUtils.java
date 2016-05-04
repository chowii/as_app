package au.com.ahbeard.sleepsense.bluetooth.tracker;

import android.util.Log;

import com.beddit.analysis.BatchAnalysisResult;
import com.beddit.analysis.CalendarDate;
import com.beddit.analysis.TimeValueFragment;
import com.beddit.analysis.TimeValueTrackFragment;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Calendar;

import au.com.ahbeard.sleepsense.model.beddit.Actigram;
import au.com.ahbeard.sleepsense.model.beddit.HeartRate;
import au.com.ahbeard.sleepsense.model.beddit.SleepCycle;
import au.com.ahbeard.sleepsense.model.beddit.SleepStage;
import au.com.ahbeard.sleepsense.services.LogService;
import au.com.ahbeard.sleepsense.utils.ConversionUtils;

/**
 * Created by neal on 19/04/2016.
 */
public class TrackerUtils {

    public static CalendarDate getCalendarDate(long timeInMilliseconds) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMilliseconds);

        // Cutoff time is 9pm if we're after 9pm, we add a day,
        if (calendar.get(Calendar.HOUR_OF_DAY) >= 18) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return new CalendarDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static void logBatchAnalysisResult(BatchAnalysisResult batchAnalysisResult) {

        for (String name : batchAnalysisResult.getPropertyNames()) {
            float value = batchAnalysisResult.getProperty(name);
            LogService.d("BatchAnalysisResult", String.format("%s=%f", name, value));
        }

        for (String timeValueName : batchAnalysisResult.getTimeValueDataNames()) {

            TimeValueTrackFragment timeValueTrackFragment = batchAnalysisResult.getTimeValueData(timeValueName);
            LogService.d("BatchAnalysisResult", "->" + timeValueName + ":" + ConversionUtils.byteArrayToString(timeValueTrackFragment.getData(), " "));

            try {

                if ( "sleep_stages".equals(timeValueName)) {

                    Log.d("BatchAnalysisResult","sleep_stages: " + timeValueTrackFragment.getItemType());

                    int position = 0;
                    byte[] bytes = timeValueTrackFragment.getData();

                    while (position < bytes.length) {

                        SleepStage sleepStage = SleepStage.create(position,bytes);
                        position+=SleepStage.getLength();

                        Log.d("BatchAnalysisResult", String.format("sleep stage: %f - %s", sleepStage.getTimestamp(),sleepStage.getPresenceDescription() ));
                    }


                }

                if ( "sleep_cycles".equals(timeValueName)) {

                    Log.d("BatchAnalysisResult","sleep_cycles: " + timeValueTrackFragment.getItemType());
                    int position = 0;
                    byte[] bytes = timeValueTrackFragment.getData();

                    while (position < bytes.length) {
                        SleepCycle sleepCycle = SleepCycle.create(position,bytes);
                        position+=SleepStage.getLength();

                        Log.d("BatchAnalysisResult", String.format("sleep cycle: %f - %f", sleepCycle.getTimestamp(),sleepCycle.getCycle() ));
                    }


                }

                if ( "heart_rate_curve".equals(timeValueName)) {

                    Log.d("BatchAnalysisResult","heart_rate_curve: " + timeValueTrackFragment.getItemType());
                    int position = 0;
                    byte[] bytes = timeValueTrackFragment.getData();

                    while (position < bytes.length) {


                        HeartRate heartRate =  HeartRate.create(position,bytes);
                        position+=HeartRate.getLength();

                        Log.d("BatchAnalysisResult", String.format("heart rate: %f - %f", heartRate.getTimestamp(),heartRate.getHeartRate() ));
                    }


                }

                if ( "actigram_epochwise".equals(timeValueName)) {

                    Log.d("BatchAnalysisResult","actigram_epochwise: " + timeValueTrackFragment.getItemType());
                    int position = 0;
                    byte[] bytes = timeValueTrackFragment.getData();

                    while (position < bytes.length) {

                        Actigram actigram = Actigram.create(position,bytes);
                        position+=Actigram.getLength();

                        Log.d("BatchAnalysisResult", String.format("detected movements (over 60s): %f - %f", actigram.getTimestamp(),actigram.getActigram() ));
                    }


                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        for (String tag : batchAnalysisResult.getTags()) {
            LogService.d("BatchAnalysisResult", String.format("tag: %s", tag));
        }

    }

    public static void logTimeValueFragment(TimeValueFragment timeValueFragment) {

        for (String trackName: timeValueFragment.getNames()) {

            TimeValueTrackFragment timeValueTrackFragment = timeValueFragment.getTrackFragment(trackName);
            LogService.d("TimeValueFragment", "->" + trackName + ":" + ConversionUtils.byteArrayToString(timeValueTrackFragment.getData(), " "));


        }

    }
}
