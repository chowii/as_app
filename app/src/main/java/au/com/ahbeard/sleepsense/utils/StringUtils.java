package au.com.ahbeard.sleepsense.utils;

import android.text.SpannableStringBuilder;


/**
 * Created by neal on 20/10/2015.
 */
public class StringUtils {


    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isNotEmpty(String string) {
        return string != null && !"".equals(string);
    }

    /**
     * Trims excessive whitespace in the content.
     *
     * @param source
     * @return
     */
    public static CharSequence trimExcessiveNewLines(CharSequence source) {

        SpannableStringBuilder spannableSource;

        if (source instanceof SpannableStringBuilder) {
            spannableSource = (SpannableStringBuilder) source;
        } else {
            spannableSource = new SpannableStringBuilder(source);
        }

        int position = 0;

        while (position < spannableSource.length()) {

            if (spannableSource.charAt(position) == '\n') {
                // Ok we found a newline.
                int positionNotNewLine = position + 1;

                while (positionNotNewLine < spannableSource.length()) {
                    // Find the next not whitespace character. Gets rid of '\n' ' ' '\n'
                    if (!Character.isWhitespace(spannableSource.charAt(positionNotNewLine))) {
                        break;
                    }

                    positionNotNewLine++;
                }

                if (positionNotNewLine - position > 2) {
                    // Delete everything except 2 newlines (a parargraph break).
                    spannableSource.delete(position, positionNotNewLine - 2);
                    position++;
                    position++;
                } else if (position == 0) {
                    // Delete newlines at the start.
                    spannableSource.delete(position, positionNotNewLine);
                } else if (positionNotNewLine == spannableSource.length()) {
                    // Delete newlines at the end.
                    spannableSource.delete(position, positionNotNewLine);
                } else {
                    position = positionNotNewLine;
                }
            } else {
                position++;
            }


        }

        return spannableSource;
    }

    public static String ordinal(int integer) {
        String[] suffixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        switch (integer % 100) {
            case 11:
            case 12:
            case 13:
                return integer + "th";
            default:
                return integer + suffixes[integer % 10];

        }
    }


    public static String timeInSecondsSinceEpochToString(Float timeInSecondsSinceEpoch) {
        if (timeInSecondsSinceEpoch != null) {
            int sleepHours = (int) (timeInSecondsSinceEpoch / (60 * 60));
            int sleepMinutes = (int) (timeInSecondsSinceEpoch / (60)) % 60;
            return sleepHours + " h " + sleepMinutes + " min";
        } else {
            return "";
        }
    }
}
