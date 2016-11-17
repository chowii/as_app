package au.com.ahbeard.sleepsense.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by nealmaloney on 4/03/2014.
 */
public class ConversionUtils {

    public static String byteArrayToString(byte[] bytes, String seperator) {

        if (bytes == null) {
            return "[NULL]";
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < bytes.length; i++) {
            if (i != 0) {
                result.append(seperator);
            }
            result.append(String.format("%02X", bytes[i]));
        }

        return result.toString();
    }

    public static byte[] stringToByteArray(String string) {

        String bytesOnlyString = string.toLowerCase().replaceAll("[^0123456789abcdef]+", "");

        byte[] bytes = new byte[bytesOnlyString.length() / 2];

        for (int i = 0; i < bytes.length; i++) {

            String substring = bytesOnlyString.substring(i * 2, i * 2 + 2);

            bytes[i] = (byte) (0xff & Integer.parseInt(substring, 16));

        }

        return bytes;

    }


    public static boolean matchBytes(byte[] bytes, byte[] buffer, int start) {

        for (int i = 0; i < bytes.length; i++) {
            if (start + i > buffer.length || bytes[i] != buffer[start + i]) {
                return false;
            }
        }

        return true;
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
}
