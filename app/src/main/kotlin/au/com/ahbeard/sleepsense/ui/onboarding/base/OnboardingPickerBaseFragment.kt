package au.com.ahbeard.sleepsense.ui.onboarding.base

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SnapHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.coordinator.OnboardingCoordinator
import au.com.ahbeard.sleepsense.services.log.SSLog
import au.com.ahbeard.sleepsense.ui.extensions.getVisibleViews
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.OnboardingFragmentListener
import au.com.ahbeard.sleepsense.ui.onboarding.views.SSNumberPickerView
import kotterknife.bindView

/**
 * Created by luisramos on 16/02/2017.
 */
open class OnboardingPickerBaseFragment(listener: OnboardingFragmentListener) : OnboardingBaseFragment(listener) {

    private var minValue = 0
    private var maxValue = 0
    private var format = ""

    val pickerView: SSNumberPickerView by bindView(R.id.pickerView)

    override fun viewsToAnimate(): List<View> {
        return pickerView.getVisibleViews()
    }

    override fun getViewLayoutId(): Int = R.layout.fragment_onboarding_base_picker

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pickerView.configure(minValue, maxValue, format)
    }

    fun configurePicker(@StringRes title: Int, format: String, min: Int, max: Int) {
        titleRes = title
        this.format = format
        minValue = min
        maxValue = max
    }

}