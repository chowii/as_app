package au.com.ahbeard.sleepsense.ui.onboarding.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingBaseFragment
import au.com.ahbeard.sleepsense.widgets.SSButton
import kotterknife.bindView

/**
 * Created by Naveenu on 14/03/2017.
 */
class SetupNotCompletedOnboardingFragment (listener: OnboardingFragmentListener) : OnboardingBaseFragment(listener) {

    val imageView: ImageView by bindView(R.id.imageView)
    val rightButton: SSButton by bindView(R.id.rightButton)

    override fun viewsToAnimate(): List<View> {
        return arrayListOf(imageView, titleTextView!!)
    }

    override fun getViewLayoutId(): Int = R.layout.fragment_onboarding_setup_not_completed

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rightButton.setOnClickListener {
            presentNextOnboardingFragment()
        }
    }
}