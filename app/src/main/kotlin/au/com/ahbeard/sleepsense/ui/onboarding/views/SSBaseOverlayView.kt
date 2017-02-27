package au.com.ahbeard.sleepsense.ui.onboarding.views

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.support.v4.animation.AnimatorCompatHelper
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.setAnimEndListener
import jp.wasabeef.blurry.Blurry
import kotterknife.bindView

/**
 * Created by luisramos on 24/02/2017.
 */
abstract class SSBaseOverlayView : FrameLayout {

    private val animationDuration = 250L

    val backgroundFadeView: View by bindView(R.id.notSureBackgroundFadeView)
    val blurImageView: ImageView by bindView(R.id.blurImageView)
    val closeButton: ImageButton by bindView(R.id.closeButton)
    val contentView: LinearLayout by bindView(R.id.overlayContainerView)
    val blueContainerView: LinearLayout by bindView(R.id.blueContainerView)

    constructor(context: Context?) : super(context) {
        setup()
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setup()
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setup()
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        setup()
    }

    protected abstract fun getResLayoutId() : Int

    protected fun setup() {
        View.inflate(context, R.layout.overlay_onboarding_base_layout, this)

        View.inflate(context, getResLayoutId(), blueContainerView)

        //intercep all events
        backgroundFadeView.setOnTouchListener { view, motionEvent -> true }
    }

    fun animateEntry(viewToBlur: View) {
        blurImageView.alpha = 0f
        Blurry.with(context)
                .radius(10)
                .sampling(5)
                .async()
                .capture(viewToBlur)
                .into(blurImageView)

        visibility = View.VISIBLE

        blurImageView.animate()
                .setDuration(animationDuration)
                .alpha(1f)
                .start()

        backgroundFadeView.alpha = 0f
        backgroundFadeView.animate()
                .setDuration(animationDuration)
                .alpha(0.5f)
                .start()

        animateMarginBottom(-contentView.height, 0)
    }

    fun animateExit() {
        animateMarginBottom(0, -contentView.height)

        blurImageView.animate()
                .setDuration(animationDuration)
                .alpha(0f)
                .start()

        backgroundFadeView.animate()
                .setDuration(animationDuration)
                .alpha(0f)
                .setAnimEndListener {
                    visibility = View.INVISIBLE
                }
                .start()
    }

    private fun animateMarginBottom(startMargin: Int, endMargin: Int) {
        val anim = AnimatorCompatHelper.emptyValueAnimator()
        anim.setDuration(animationDuration)
        anim.addUpdateListener {
            val bottomMargin = startMargin + it.animatedFraction * (endMargin - startMargin)
            val layoutParams = contentView.layoutParams as FrameLayout.LayoutParams
            layoutParams.bottomMargin = bottomMargin.toInt()
            contentView.layoutParams = layoutParams
        }
        anim.start()
    }

}