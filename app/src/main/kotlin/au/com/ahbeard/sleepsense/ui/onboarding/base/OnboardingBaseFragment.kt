package au.com.ahbeard.sleepsense.ui.onboarding.base

import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.annotation.ColorRes

import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.ui.onboarding.MainOnboardingActivity
import kotterknife.bindOptionalView

/**
 * Created by luisramos on 23/01/2017.
 */
open class OnboardingBaseFragment : Fragment() {

    val onboardingActivity : MainOnboardingActivity
        get() = activity as MainOnboardingActivity

    val backButton: ImageButton? by bindOptionalView(R.id.backButton)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBackgroundGradient(R.color.onboarding_gradient_1_top, R.color.onboarding_gradient_1_bottom)

        backButton?.visibility = if (fragmentManager.backStackEntryCount == 0) View.GONE else View.VISIBLE
        backButton?.setOnClickListener { fragmentManager.popBackStack() }
    }

    fun presentNextOnboardingFragment() {
        onboardingActivity.presentNextOnboardingFragment()
    }

    fun setBackgroundGradient(vararg @ColorRes colors: Int) {
        val gradient = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                colors.map { ContextCompat.getColor(activity, it) }.toIntArray())
        view?.background= gradient
    }
}