package au.com.ahbeard.sleepsense.ui.onboarding.base

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.StringRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.coordinator.OnboardingCoordinator
import au.com.ahbeard.sleepsense.services.log.SSLog
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingQuestionsFragment.OnboardingQuestionsAdapter.OnItemClickListener
import au.com.ahbeard.sleepsense.ui.onboarding.fragments.*
import au.com.ahbeard.sleepsense.ui.onboarding.views.SSErrorHandlingOverlayView
import kotlinx.android.synthetic.main.fragment_onboarding_desc.view.*
import kotterknife.bindView

/**
 * Created by luisramos on 23/01/2017.
 */
abstract class OnboardingQuestionsFragment(coordinator: OnboardingCoordinator) : OnboardingBaseFragment(coordinator) {

    class QuestionViewModel(val title: String, val isTextButton: Boolean) {
        constructor(title: String) : this(title, false)
    }

    var data : List<QuestionViewModel> = arrayListOf()

    val containerView: RelativeLayout by bindView(R.id.containerView)
    val recyclerView: RecyclerView by bindView(R.id.recyclerView)
    val shadowView: ImageView by bindView(R.id.shadowImage)
    var layoutManager : LinearLayoutManager? = null
    var adapter : OnboardingQuestionsAdapter? = null

    val errorOverlayView : SSErrorHandlingOverlayView by lazy {
        createErrorView()
    }

    override fun viewsToAnimate(): List<View> {
        val firstChildPos = layoutManager?.findFirstVisibleItemPosition()
        val lastChildPos = layoutManager?.findLastVisibleItemPosition()
        if (firstChildPos != null && lastChildPos != null) {
            return (firstChildPos..lastChildPos).map { recyclerView.getChildAt(it) }
        }
        return arrayListOf()
    }

    override fun getViewLayoutId(): Int = R.layout.fragment_onboarding_questions

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager

        adapter = OnboardingQuestionsAdapter(data)
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(ShadowOverscrollOnScrollListener(shadowView))

        adapter?.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(pos: Int) {
                didSelectOption(pos)
            }
        }
    }

    open fun didSelectOption(index: Int) {

    }

    fun configureQuestions(@StringRes title: Int, vararg @StringRes strings: Int) {
        titleRes = title
        data = strings.map { QuestionViewModel(getString(it)) }
    }

    fun handleError(error: Throwable) {
        when (error) {
            is OnboardingErrorPumpNotFound -> showErrorOverlay(
                    R.string.onboarding_error_title_pump_not_found, R.string.onboarding_error_desc_pump_not_found)
            is OnboardingErrorPumpCantConnect -> showErrorOverlay(
                    R.string.onboarding_error_title_pump_cant_connect, R.string.onboarding_error_desc_pump_cant_connect)
            is OnboardingErrorTrackerNotFound -> showErrorOverlay(
                    R.string.onboarding_error_title_tracker_not_found, R.string.onboarding_error_desc_tracker_not_found)
            is OnboardingErrorTrackerOnlyFoundOne -> showErrorOverlay(
                    R.string.onboarding_error_title_tracker_not_found_two, R.string.onboarding_error_desc_tracker_not_found_two)
            is OnboardingErrorTrackerLostConnection -> showErrorOverlay(
                    R.string.onboarding_error_title_tracker_lost_connection, R.string.onboarding_error_desc_tracker_lost_connection)
            is OnboardingErrorBaseNotFound -> showErrorOverlay(
                    R.string.onboarding_error_title_base_not_found, R.string.onboarding_error_desc_base_not_found)
            is OnboardingErrorBaseOnlyFoundOne -> showErrorOverlay(
                    R.string.onboarding_error_title_base_not_found_two, R.string.onboarding_error_desc_base_not_found_two)
        }
    }

    fun showErrorOverlay(titleRes: Int, descRes: Int) {
        Handler(Looper.getMainLooper()).post {
            errorOverlayView.titleTextView.setText(titleRes)
            errorOverlayView.descTextView.setText(descRes)
            errorOverlayView.animateEntry(view!!)
        }
    }

    fun hideErrorOverlay() {
        Handler(Looper.getMainLooper()).post {
            errorOverlayView.animateExit()
        }
    }

    private fun createErrorView(): SSErrorHandlingOverlayView {
        val layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT)

        val errorView = SSErrorHandlingOverlayView(view!!.context)
        errorView.layoutParams = layoutParams
        errorView.visibility = View.INVISIBLE

        containerView.addView(errorView)

        return errorView
    }

    class OnboardingQuestionsAdapter(var data: List<QuestionViewModel>) : RecyclerView.Adapter<OnboardingQuestionsAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val button: Button by bindView(R.id.button)
        }

        interface OnItemClickListener {
            fun onItemClick(pos: Int)
        }

        var onItemClickListener: OnItemClickListener? = null

        override fun getItemCount(): Int {
            return data.size
        }

        override fun getItemViewType(position: Int): Int {
            return if (data[position].isTextButton) R.layout.item_onboarding_question_text else R.layout.item_onboarding_question
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent!!.context).inflate(viewType, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            holder?.button?.text = data[position].title
            holder?.button?.setOnClickListener { onItemClickListener?.onItemClick(position) }
        }
    }
}