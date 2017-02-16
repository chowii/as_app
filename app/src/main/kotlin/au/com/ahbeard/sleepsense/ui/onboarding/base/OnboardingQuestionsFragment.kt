package au.com.ahbeard.sleepsense.ui.onboarding.base

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.services.log.SSLog
import au.com.ahbeard.sleepsense.ui.onboarding.base.OnboardingQuestionsFragment.OnboardingQuestionsAdapter.OnItemClickListener
import kotterknife.bindView

/**
 * Created by luisramos on 23/01/2017.
 */
abstract class OnboardingQuestionsFragment : OnboardingBaseFragment() {

    class QuestionViewModel(val title: String, val isTextButton: Boolean) {
        constructor(title: String) : this(title, false)
    }

    var data : List<QuestionViewModel> = arrayListOf()

    val recyclerView: RecyclerView by bindView(R.id.recyclerView)
    val shadowView: ImageView by bindView(R.id.shadowImage)
    var layoutManager : LinearLayoutManager? = null
    var adapter : OnboardingQuestionsAdapter? = null

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