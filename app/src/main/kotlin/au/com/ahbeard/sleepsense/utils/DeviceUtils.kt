package au.com.ahbeard.sleepsense.utils

import android.content.Context
import android.content.res.Resources
import au.com.ahbeard.sleepsense.SleepSenseApplication

/**
 * Created by luisramos on 24/01/2017.
 */
object DeviceUtils {
    private val context: Context
        get() = SleepSenseApplication.instance().applicationContext

    val deviceWidthPx : Int
        get() = Resources.getSystem().displayMetrics.widthPixels

    val deviceHeightPx: Int
        get() = Resources.getSystem().displayMetrics.heightPixels

    fun pxToDp(px: Int) : Float {
        return px / context.resources.displayMetrics.density
    }

    fun dpToPx(dp: Int) : Float {
        return dp * context.resources.displayMetrics.density
    }
}