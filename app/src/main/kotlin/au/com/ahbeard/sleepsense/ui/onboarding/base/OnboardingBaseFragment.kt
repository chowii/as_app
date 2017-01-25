package au.com.ahbeard.sleepsense.ui.onboarding.base

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.ui.onboarding.MainOnboardingActivity
import au.com.ahbeard.sleepsense.ui.onboarding.OnboardingState
import au.com.ahbeard.sleepsense.ui.onboarding.animations.OnboardingTransitionAnimatable
import au.com.ahbeard.sleepsense.ui.onboarding.animations.OnboardingTransitionAnimator
import kotterknife.bindOptionalView

/**
* Created by luisramos on 23/01/2017.
*/
abstract class OnboardingBaseFragment : Fragment(), OnboardingFragment, OnboardingTransitionAnimatable {

    var state = OnboardingState()

    val onboardingActivity : MainOnboardingActivity
        get() = activity as MainOnboardingActivity

    val backButton: ImageButton? by bindOptionalView(R.id.backButton)

    @StringRes var titleRes: Int = R.string.base_position_screen_title
    val titleTextView: TextView? by bindOptionalView(R.id.titleTextView)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBackgroundGradient(R.color.onboarding_gradient_1_top, R.color.onboarding_gradient_1_bottom)

        titleTextView?.text = getString(titleRes)

        if (fragmentManager.backStackEntryCount > 0) {
            backButton?.animate()?.alpha(1f)?.setDuration(500L)?.start()
        }
        backButton?.setOnClickListener { activity?.onBackPressed() }

//        prepareViewsForEntryAnim()
    }

    fun presentNextOnboardingFragment() {
        onboardingActivity.presentNextOnboardingFragment()
    }

    fun setBackgroundGradient(vararg @ColorRes colors: Int) {
        val gradient = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                colors.map { ContextCompat.getColor(activity, it) }.toIntArray())
        view?.background= gradient
    }

    fun prepareViewsForEntryAnim() {
        if (fragmentManager.backStackEntryCount > 0)
            OnboardingTransitionAnimator.prepareViewsForEntryAnim(this)
    }
}