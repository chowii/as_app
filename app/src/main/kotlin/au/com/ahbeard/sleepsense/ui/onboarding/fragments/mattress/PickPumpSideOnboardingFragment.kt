package au.com.ahbeard.sleepsense.ui.onboarding.fragments.mattress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.bluetooth.pump.PumpDevice
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingBaseFragment
import au.com.ahbeard.sleepsense.widgets.SSButton
import kotterknife.bindView

/**
 * Created by luisramos on 30/01/2017.
 */
class PickPumpSideOnboardingFragment : OnboardingBaseFragment() {

    val imageView: ImageView by bindView(R.id.imageView)
    val leftButton: SSButton by bindView(R.id.leftButton)
    val rightButton: SSButton by bindView(R.id.rightButton)

    override fun viewsToAnimate(): List<View> {
        return arrayListOf(imageView, titleTextView!!, leftButton, rightButton)
    }

    override fun getViewLayoutId(): Int = R.layout.fragment_onboarding_pick_pump_side

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        leftButton.setOnClickListener {
            state.pumpSide = PumpDevice.Side.Left
            presentNextOnboardingFragment()
        }

        rightButton.setOnClickListener {
            state.pumpSide = PumpDevice.Side.Right
            presentNextOnboardingFragment()
        }
    }
}