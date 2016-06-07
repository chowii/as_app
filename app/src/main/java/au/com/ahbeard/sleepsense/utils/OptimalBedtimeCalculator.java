package au.com.ahbeard.sleepsense.utils;

import android.util.Log;

import java.util.Calendar;

/**
 * Created by neal on 6/06/2016.
 */
public class OptimalBedtimeCalculator {

    private Calendar mCalendar = Calendar.getInstance();

    // 48 "slots" with slot 0 being midnight.
    private int[] mSleepScoreCounts = new int[48];
    private float[] mSleepScoreSums = new float[48];
    private float[] mSleepScoreAverages = new float[48];

    public void add(double startTime, float sleepScore) {

        Log.d("OptimalBeditimeCalc",String.format("startTime: %f sleepScore: %f",startTime,sleepScore));

        if ( startTime > 0 ) {

            mCalendar.setTimeInMillis((long) (startTime * 1000));
            mCalendar.add(Calendar.MINUTE, -15);

            int hourOfDay = mCalendar.get(Calendar.HOUR_OF_DAY);
            int minutes = mCalendar.get(Calendar.MINUTE);

            int slot = hourOfDay * 2 + (minutes >= 30 ? 1 : 0);

            Log.d("OptimalBeditimeCalc",String.format("slot: %d sleepScore: %f",slot,sleepScore));

            mSleepScoreCounts[slot]++;
            mSleepScoreSums[slot]+=sleepScore;

            if ( mSleepScoreCounts[slot] > 0 ) {
                mSleepScoreAverages[slot]=mSleepScoreSums[slot]/mSleepScoreCounts[slot];
            } else {
                mSleepScoreAverages[slot]=0;
            }

        }

    }

    public Calendar getResult() {

        float highestAverageSleepScore = Float.MIN_VALUE;
        int highestSlot = -1;

        for (int i = 0; i < 48; i++) {
            if (mSleepScoreCounts[i] > 0 && mSleepScoreAverages[i] > highestAverageSleepScore) {
                highestAverageSleepScore = mSleepScoreAverages[i];
                highestSlot = i;
            }
        }

        if (highestSlot >= 0) {

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,highestSlot/2);
            calendar.set(Calendar.MINUTE,highestSlot%2==0?0:30);
            return calendar;

        } else {
            return null;
        }

    }

}
