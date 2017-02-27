package au.com.ahbeard.sleepsense.ui.onboarding.base

import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import au.com.ahbeard.sleepsense.R
import kotterknife.bindView

/**
 * Created by luisramos on 30/01/2017.
 */
open class OnboardingDescFragment : OnboardingBaseFragment() {

    val imageView: ImageView by bindView(R.id.imageView)
    val descTextView: TextView by bindView(R.id.descTextView)

    @DrawableRes var imageRes: Int? = null
    @StringRes var descRes: Int? = null

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