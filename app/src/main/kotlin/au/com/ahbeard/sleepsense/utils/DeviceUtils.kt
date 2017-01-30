package au.com.ahbeard.sleepsense.utils

import android.content.Context
import android.content.res.Resources
import au.com.ahbeard.sleepsense.SleepSenseApplication

/**
 * Created by luisramos on 24/01/2017.
 */
object DeviceUtils {
    private val applicationContext: Context
        get() = SleepSenseApplication.instance().applicationContext

    val deviceWidthPx : Int
        get() = Resources.getSystem().displayMetrics.widthPixels

    val deviceHeightPx: Int
        get() = Resources.getSystem().displayMetrics.heightPixels

    /**
     * This method uses application context to access the density
     */
    @JvmStatic fun pxToDp(px: Int) : Float {
        return px / applicationContext.resources.displayMetrics.density
    }

    /**
     * This method uses application context to access the density
     */
    @JvmStatic fun dpToPx(dp: Int) : Float {
        return dp * applicationContext.resources.displayMetrics.density
    }
}

fun Context.dpToPx(dp: Int) : Float {
    return dp * resources.displayMetrics.density
}

fun Context.pxToDp(px: Int) : Float {
    return px / resources.displayMetrics.density
}