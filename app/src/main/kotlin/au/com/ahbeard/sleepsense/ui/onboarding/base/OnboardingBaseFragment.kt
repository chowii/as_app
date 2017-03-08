package au.com.ahbeard.sleepsense.ui.onboarding.base

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.coordinator.OnboardingCoordinator
import au.com.ahbeard.sleepsense.ui.onboarding.MainOnboardingActivity
import au.com.ahbeard.sleepsense.ui.onboarding.OnboardingState
import au.com.ahbeard.sleepsense.ui.onboarding.animations.OnboardingTransitionAnimatable
import au.com.ahbeard.sleepsense.ui.onboarding.animations.OnboardingTransitionAnimator
import au.com.ahbeard.sleepsense.ui.onboarding.views.SSBaseOverlayView
import au.com.ahbeard.sleepsense.ui.onboarding.views.SSErrorHandlingOverlayView
import au.com.ahbeard.sleepsense.ui.onboarding.views.SSNotSureOverlayView
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.LifecycleTransformer
import com.trello.rxlifecycle2.RxLifecycle
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.android.RxLifecycleAndroid
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotterknife.bindOptionalView

/**
* Created by luisramos on 23/01/2017.
*/
abstract class LifecycleFragment: Fragment(), LifecycleProvider<FragmentEvent> {

    private val lifecycleSubject = BehaviorSubject.create<FragmentEvent>()

    override fun <T : Any?> bindToLifecycle(): LifecycleTransformer<T> {
        return RxLifecycleAndroid.bindFragment(lifecycleSubject)
    }

    override fun <T : Any?> bindUntilEvent(event: FragmentEvent): LifecycleTransformer<T> {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event)
    }

    override fun lifecycle(): Observable<FragmentEvent> = lifecycleSubject.hide()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        lifecycleSubject.onNext(FragmentEvent.ATTACH)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleSubject.onNext(FragmentEvent.CREATE)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW)
    }

    override fun onStart() {
        super.onStart()
        lifecycleSubject.onNext(FragmentEvent.START)
    }

    override fun onResume() {
        super.onResume()
        lifecycleSubject.onNext(FragmentEvent.RESUME)
    }

    override fun onPause() {
        lifecycleSubject.onNext(FragmentEvent.PAUSE)
        super.onPause()
    }

    override fun onStop() {
        lifecycleSubject.onNext(FragmentEvent.STOP)
        super.onStop()
    }

    override fun onDestroyView() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW)
        super.onDestroyView()
    }

    override fun onDestroy() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY)
        super.onDestroy()
    }

    override fun onDetach() {
        lifecycleSubject.onNext(FragmentEvent.DETACH)
        super.onDetach()
    }
}

abstract class OnboardingBaseFragment(
        val coordinator: OnboardingCoordinator
) :
        LifecycleFragment(), OnboardingTransitionAnimatable
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

        if (coordinator.canPopBackStack()) {
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
        coordinator.presentNextOnboardingFragment()
    }

    open fun presentNextOnboardingFragment() {
        coordinator.presentNextOnboardingFragment()
    }

    // This method exists to enable us to skip screens without saving anything
    open fun skipToNextOnboardingFragment() {
        coordinator.presentNextOnboardingFragment()
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