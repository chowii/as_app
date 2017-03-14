package au.com.ahbeard.sleepsense.ui.onboarding.base

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.coordinator.OnboardingCoordinator
import au.com.ahbeard.sleepsense.fragments.BaseFragment
import au.com.ahbeard.sleepsense.ui.onboarding.MainOnboardingActivity
import au.com.ahbeard.sleepsense.ui.onboarding.OnboardingState
import au.com.ahbeard.sleepsense.ui.onboarding.animations.OnboardingTransitionAnimatable
import au.com.ahbeard.sleepsense.ui.onboarding.animations.OnboardingTransitionAnimator
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.OnboardingFragmentListener
import au.com.ahbeard.sleepsense.ui.onboarding.views.SSBaseOverlayView
import kotterknife.bindOptionalView

/**
* Created by luisramos on 23/01/2017.
*/

abstract class OnboardingBaseFragment(
        internal val listener: OnboardingFragmentListener
) :
        BaseFragment(), OnboardingTransitionAnimatable
{

    val onboardingActivity: MainOnboardingActivity
        get() = (activity as MainOnboardingActivity)

    var state = OnboardingState()

    var backgroundGradient: BackgroundGradient = BackgroundGradient.MATTRESS
    var titleRes: Int? = null
    val titleTextView: TextView? by bindOptionalView(R.id.titleTextView)
    val backButton: ImageButton? by bindOptionalView(R.id.backButton)
    val continueButton: Button? by bindOptionalView(R.id.continueButton)
    val skipButton: Button? by bindOptionalView(R.id.skipButton)

    val overlayView: SSBaseOverlayView? = null

    abstract fun getViewLayoutId() : Int

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(getViewLayoutId(), container, false)!!
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setGradient()

        titleRes?.let { titleTextView?.text = getString(it) }

        if (listener.shouldShowBackButton(this)) {
            backButton?.animate()?.alpha(1f)?.setDuration(500L)?.start()
        }
        backButton?.setOnClickListener {
            presentPreviousOnboardingFragment()
        }

        continueButton?.setOnClickListener {
            presentNextOnboardingFragment()
        }
        skipButton?.visibility = View.INVISIBLE
        skipButton?.setOnClickListener {
            skipToNextOnboardingFragment()
        }

//        prepareViewsForEntryAnim()
    }

    open fun presentPreviousOnboardingFragment() {
        listener.presentPreviousOnboardingFragment()
    }

    open fun presentNextOnboardingFragment() {
        listener.presentNextOnboardingFragment()
    }

    // This method exists to enable us to skip screens without saving anything
    open fun skipToNextOnboardingFragment() {
        listener.presentNextOnboardingFragment()
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