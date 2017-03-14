package au.com.ahbeard.sleepsense.ui.onboarding.base

import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.coordinator.OnboardingCoordinator
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.OnboardingFragmentListener
import kotterknife.bindView

/**
 * Created by luisramos on 30/01/2017.
 */
open class OnboardingDescFragment(listener: OnboardingFragmentListener) : OnboardingBaseFragment(listener) {

    val imageView: ImageView by bindView(R.id.imageView)
    val descTextView: TextView by bindView(R.id.descTextView)

    var imageRes: Int? = null
    var descRes: Int? = null

    override fun viewsToAnimate(): List<View> {
        return arrayListOf(imageView, titleTextView!!, descTextView, continueButton!!)
    }

    override fun getViewLayoutId(): Int = R.layout.fragment_onboarding_desc

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageRes?.let { imageView.setImageResource(it) }
        descRes?.let { descTextView.text = getString(it) }
    }

    fun configureDesc(@StringRes title: Int, @StringRes desc: Int, @DrawableRes image: Int) {
        titleRes = title
        descRes = desc
        imageRes = image
    }
}