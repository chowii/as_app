package au.com.ahbeard.sleepsense.ui.onboarding.base

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import au.com.ahbeard.sleepsense.R
import kotterknife.bindView

/**
 * Created by luisramos on 16/02/2017.
 */
open class OnboardingPickerBaseFragment : OnboardingBaseFragment() {

    class RowViewModel(val value: Int, val text: String)

    var data : List<RowViewModel> = arrayListOf()

    val recyclerView: RecyclerView by bindView(R.id.recyclerView)
    var layoutManager : LinearLayoutManager? = null
    var adapter : PickerRowAdapter? = null

    override fun viewsToAnimate(): List<View> {
        return recyclerView.getVisibleViews()
    }

    override fun getViewLayoutId(): Int = R.layout.fragment_onboarding_age

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager

        adapter = PickerRowAdapter(data)
        recyclerView.adapter = adapter

        recyclerView.viewTreeObserver.addOnGlobalLayoutListener {
            adapter?.recyclerViewSize = recyclerView.measuredHeight
        }
    }

    fun configurePicker(@StringRes title: Int, format: String, values: List<Int>) {
        titleRes = title
        data = values.map { RowViewModel(it, java.lang.String.format(format, it)) }
    }

    class PickerRowAdapter(var data: List<RowViewModel>) : RecyclerWithMarginsAdapter() {

        class ViewHolder(view: View) : RecyclerWithMarginsAdapter.MarginViewHolder(view) {
            val textView: TextView by bindView(R.id.textView)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_onboarding_picker_row, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerWithMarginsAdapter.MarginViewHolder?, position: Int) {
            super.onBindViewHolder(holder, position)

            val viewHolder = holder as? ViewHolder
            viewHolder?.textView?.text = data[position].text
        }
    }

    class PickerScrollListener() : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            
        }
    }
}

fun RecyclerView.getVisibleViews() : List<View> {
    val layoutManager = this.layoutManager as? LinearLayoutManager
    layoutManager?.let {
        val firstChildPos = layoutManager.findFirstVisibleItemPosition()
        val lastChildPos = layoutManager.findLastVisibleItemPosition()
        return (firstChildPos..lastChildPos).map { getChildAt(it) }
    }
    return arrayListOf()
}