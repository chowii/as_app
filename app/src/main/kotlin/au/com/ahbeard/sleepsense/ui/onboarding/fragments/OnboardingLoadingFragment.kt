package au.com.ahbeard.sleepsense.ui.onboarding.fragments

import android.animation.Animator
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.TextView
import au.com.ahbeard.sleepsense.R
import kotterknife.bindView

/**
 * Created by luisramos on 24/01/2017.
 */
class OnboardingLoadingFragment(val titleText: String, val doneText: String) : Fragment() {

    var backgroundToUse: Drawable? = null

    val titleTextView: TextView by bindView(R.id.titleTextView)
    val iconImageView: ImageView by bindView(R.id.iconImageView)
    val whiteCircleImageView: ImageView by bindView(R.id.whiteCircleView)
    val pulseCircleImageView: ImageView by bindView(R.id.pulsingCircleView)
    val backgroundCircleImageView: ImageView by bindView(R.id.backgroundCircleView)
    val checkmarkImageView: ImageView by bindView(R.id.checkmarkImageView)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_onboarding_loading, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleTextView.text = titleText
        view?.background = backgroundToUse

        startLoadingAnimation()
    }

    private var shouldStopAnim = false

    fun stopAnimations(onAnimComplete: () -> Unit) {
        shouldStopAnim = true
        doFinishAnimation(onAnimComplete)
    }

    private fun startLoadingAnimation() {
        doPulseCircleAnim()
        doCircleAnim()
    }

    private fun doPulseCircleAnim() {
        if (shouldStopAnim || activity == null) { return }

        val set = AnimationSet(true)

        val scaleAnim = ScaleAnimation(1f, 2.75f, 1f, 2.75f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        scaleAnim.duration = 1000
        set.addAnimation(scaleAnim)

        val alphaAnim = AlphaAnimation(0f, 1f)
        alphaAnim.duration = 400
        set.addAnimation(alphaAnim)

        val alpha2Anim = AlphaAnimation(1f, 0f)
        alpha2Anim.duration = scaleAnim.duration - alphaAnim.duration
        alpha2Anim.startOffset = alphaAnim.duration
        set.addAnimation(alpha2Anim)

        set.setAnimEndListener {
            doPulseCircleAnim()
        }

        pulseCircleImageView.startAnimation(set)
    }

    private fun doCircleAnim() {
        if (shouldStopAnim || activity == null) { return }

        whiteCircleImageView.scaleX = 1f
        whiteCircleImageView.scaleY = 1f
        val animationSet = AnimationSet(false)

        val scale1Anim = ScaleAnimation(1f, 1.1f, 1f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        scale1Anim.duration = 100
        animationSet.addAnimation(scale1Anim)

        val scale2Anim = ScaleAnimation(1.1f, 1f, 1.1f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        scale2Anim.duration = 250
//        scale2Anim.interpolator = OvershootInterpolator()
        scale2Anim.startOffset = scale1Anim.duration
        animationSet.addAnimation(scale2Anim)

        val delayAnim = ScaleAnimation(1f, 1f, 1f, 1f)
        delayAnim.duration = 650
        delayAnim.startOffset = scale1Anim.duration + scale2Anim.duration
        animationSet.addAnimation(delayAnim)

        animationSet.setAnimEndListener {
            doCircleAnim()
        }

        whiteCircleImageView.startAnimation(animationSet)
    }

    private fun doFinishAnimation(onAnimEnd: () -> Unit) {
        val animDuration = 350L

        pulseCircleImageView.animate()
                .setDuration(animDuration)
                .alpha(0f)
                .start()

        backgroundCircleImageView.scaleX = 0f
        backgroundCircleImageView.scaleY = 0f
        backgroundCircleImageView.animate()
                .setDuration(animDuration)
                .alpha(1f)
                .scaleY(1f)
                .scaleX(1f)
                .start()

        iconImageView.animate()
                .setDuration(animDuration)
                .scaleX(0f)
                .scaleY(0f)
                .start()

        checkmarkImageView.scaleY = 0f
        checkmarkImageView.scaleX = 0f
        checkmarkImageView.animate()
                .setDuration(animDuration)
                .scaleY(1f)
                .scaleX(1f)
                .alpha(1f)
                .start()

        //fake animation to delay onAnimEnd block call
        whiteCircleImageView.animate()
                .setDuration(650)
                .setStartDelay(animDuration)
                .scaleY(1f)
                .setAnimEndListener {
                    onAnimEnd()
                }
                .start()
    }
}

fun AnimationSet.setAnimEndListener(block: (Animation?) -> Unit) {
    this.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationEnd(animation: Animation?) {
            block(animation)
            animation?.setAnimationListener(null)
        }

        override fun onAnimationStart(animation: Animation?) {

        }

        override fun onAnimationRepeat(animation: Animation?) {

        }
    })
}

fun ViewPropertyAnimator.setAnimEndListener(block: (Animator?) -> Unit) : ViewPropertyAnimator {
    setListener(OnAnimEndAnimatorListener(block))
    return this
}

class OnAnimEndAnimatorListener(val block: (Animator?) -> Unit): Animator.AnimatorListener {
    override fun onAnimationRepeat(animation: Animator?) {

    }

    override fun onAnimationEnd(animation: Animator?) {
        block(animation)
        animation?.removeAllListeners()
    }

    override fun onAnimationCancel(animation: Animator?) {

    }

    override fun onAnimationStart(animation: Animator?) {

    }
}