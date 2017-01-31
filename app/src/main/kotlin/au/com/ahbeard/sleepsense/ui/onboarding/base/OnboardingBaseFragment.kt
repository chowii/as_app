package au.com.ahbeard.sleepsense.ui.onboarding.base

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.ui.onboarding.MainOnboardingActivity
import au.com.ahbeard.sleepsense.ui.onboarding.OnboardingState
import au.com.ahbeard.sleepsense.ui.onboarding.animations.OnboardingTransitionAnimatable
import au.com.ahbeard.sleepsense.ui.onboarding.animations.OnboardingTransitionAnimator
import kotterknife.bindOptionalView
import kotterknife.bindView

/**
* Created by luisramos on 23/01/2017.
*/
abstract class OnboardingBaseFragment : Fragment(), OnboardingFragment, OnboardingTransitionAnimatable {

    var state = OnboardingState()

    val onboardingActivity : MainOnboardingActivity
        get() = activity as MainOnboardingActivity

    var backgroundGradient: BackgroundGradient = BackgroundGradient.MATTRESS

    val backButton: ImageButton? by bindOptionalView(R.id.backButton)
    val continueButton: Button? by bindOptionalView(R.id.continueButton)
    val skipButton: Button? by bindOptionalView(R.id.skipButton)

    @StringRes var titleRes: Int? = null
    val titleTextView: TextView? by bindOptionalView(R.id.titleTextView)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setGradient()

        titleRes?.let { titleTextView?.text = getString(it) }

        if (fragmentManager.backStackEntryCount > 0) {
            backButton?.animate()?.alpha(1f)?.setDuration(500L)?.start()
        }
        backButton?.setOnClickListener {
            if (fragmentManager.backStackEntryCount > 0)
                activity?.onBackPressed()
        }

        continueButton?.setOnClickListener { presentNextOnboardingFragment() }
        skipButton?.setOnClickListener { skipToNextOnboardingFragment() }

//        prepareViewsForEntryAnim()
    }

    open fun presentNextOnboardingFragment() {
        onboardingActivity.presentNextOnboardingFragment()
    }

    open fun skipToNextOnboardingFragment() {
        onboardingActivity.presentNextOnboardingFragment()
    }

    private fun setGradient() {
        val colors = arrayOf(backgroundGradient.topColor, backgroundGradient.bottomColor)
        val gradient = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                colors.map { ContextCompat.getColor(activity, it) }.toIntArray())
        view?.background= gradient
    }

    fun prepareViewsForEntryAnim() {
        if (fragmentManager.backStackEntryCount > 0)
            OnboardingTransitionAnimator.prepareViewsForEntryAnim(this)
    }

    enum class BackgroundGradient(val topColor: Int, val bottomColor: Int) {
        MATTRESS(R.color.onboarding_gradient_1_top, R.color.onboarding_gradient_1_bottom),
        TRACKER(R.color.onboarding_gradient_2_top, R.color.onboarding_gradient_2_bottom),
        BASE(R.color.onboarding_gradient_3_top, R.color.onboarding_gradient_3_bottom)
    }
}