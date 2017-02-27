package au.com.ahbeard.sleepsense.ui.onboarding.views

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import au.com.ahbeard.sleepsense.R

/**
 * Created by luisramos on 20/02/2017.
 */
class SSNotSureOverlayView : SSBaseOverlayView {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun getResLayoutId(): Int = R.layout.overlay_onboarding_not_sure

}