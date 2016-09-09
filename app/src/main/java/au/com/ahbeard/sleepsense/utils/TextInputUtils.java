package au.com.ahbeard.sleepsense.utils;

import android.text.TextUtils;

/**
 * Created by luisramos on 9/09/2016.
 */
public class TextInputUtils {
    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
