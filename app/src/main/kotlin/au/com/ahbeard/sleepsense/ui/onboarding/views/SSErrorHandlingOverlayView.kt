package au.com.ahbeard.sleepsense.ui.onboarding.views

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.TextView
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.services.log.SSLog
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.ErrorHandlingViewListener
import kotterknife.bindView

/**
 * Created by luisramos on 24/02/2017.
 */
class SSErrorHandlingOverlayView : SSBaseOverlayView  {

    val tryAgainButton: Button by bindView(R.id.tryAgainButton)
    val helpMeButton: Button by bindView(R.id.helpMeButton)
    val setUpButton: Button by bindView(R.id.setUpLaterButton)
    val titleTextView: TextView by bindView(R.id.titleTextView)

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun getResLayoutId(): Int = R.layout.overlay_onboarding_error

    init {
        closeButton.visibility = View.GONE

        tryAgainButton.setOnClickListener {
            SSLog.d("====> TRY AGAIN")
            animateExit()
        }
        helpMeButton.setOnClickListener {
            SSLog.d("====> HELP ME ")
            animateExit()
            onErrorHandlingClickListener?.onTroubleshootingClick(titleTextView?.text.toString())
        }
        setUpButton.setOnClickListener {
            SSLog.d("====> SET UP")
            animateExit()
            onErrorHandlingClickListener?.onSetUpLaterClick()
        }
    }

    var onErrorHandlingClickListener: ErrorHandlingViewListener? = null

}