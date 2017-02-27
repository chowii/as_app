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
import au.com.ahbeard.sleepsense.services.log.SSLog
import kotterknife.bindView

/**
 * Created by luisramos on 16/02/2017.
 */
open class OnboardingPickerBaseFragment : OnboardingBaseFragment() {

    private var minValue = 3
    private var maxValue = 120

    class RowViewModel(val value: Int, val text: String)

    var data : List<RowViewModel> = arrayListOf()

    val recyclerView: RecyclerView by bindView(R.id.recyclerView)
    var layoutManager : LinearLayoutManager? = null
    var adapter : PickerRowAdapter? = null
    var snapHelper: SnapHelper = LinearSnapHelper()

    override fun viewsToAnimate(): List<View> {
        return recyclerView.getVisibleViews()
    }

    override fun getViewLayoutId(): Int = R.layout.fragment_onboarding_base_picker

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        recyclerView.setBackgroundColor(R.color.debug1)

        layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager

        val pickerLineHeight = resources.getDimension(R.dimen.onboarding_picker_line_height).toInt()
        adapter = PickerRowAdapter(data, pickerLineHeight)
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(PickerScrollListener())
        snapHelper.attachToRecyclerView(recyclerView)

        recyclerView.viewTreeObserver.addOnGlobalLayoutListener {
            adapter?.recyclerViewSize = recyclerView.measuredHeight
        }
    }

    fun configurePicker(@StringRes title: Int, format: String, min: Int, max: Int) {
        minValue = min
        maxValue = max
        titleRes = title
        data = (minValue..maxValue).toList().map { RowViewModel(it, java.lang.String.format(format, it)) }
    }

    fun getSelectedValue() : Int {
        val firstChildPos = layoutManager?.findFirstVisibleItemPosition()
        val lastChildPos = layoutManager?.findLastVisibleItemPosition()
        if (firstChildPos != null && lastChildPos != null) {
            val pos = (lastChildPos - firstChildPos) / 2 + firstChildPos
            return data[pos].value
        }
        return minValue
    }

    class PickerRowAdapter(var data: List<RowViewModel>, itemViewSize: Int) : RecyclerWithMarginsAdapter(itemViewSize) {

        class ViewHolder(view: View) : RecyclerWithMarginsAdapter.MarginViewHolder(view) {
            val textView: TextView by bindView(R.id.textView)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_onboarding_picker_row, parent, false)
//            view.setBackgroundColor(R.color.debug2)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerWithMarginsAdapter.MarginViewHolder?, position: Int) {
            super.onBindViewHolder(holder, position)

            val viewHolder = holder as? ViewHolder
            viewHolder?.textView?.text = data[position].text
        }
    }

    class PickerScrollListener : RecyclerView.OnScrollListener() {

        var currentOffset = 0

        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            currentOffset += dy

//            recyclerView?.getVisibleViews()?.forEach {
//
//                val viewHeight = recyclerView.height / 2
//                val correctedTop = if (it.top <= viewHeight) it.top else (recyclerView.height - it.top)
//                val offset = correctedTop + it.height / 2
//
//                val percentage = offset.toFloat() / viewHeight
//                it.alpha = Math.max(0f, Math.min(percentage, 1f))
//
//                val textView = it.findViewById(R.id.textView) as? TextView
//                if (textView?.text == "3") {
//                    SSLog.d("${textView?.text ?: "0"} per: $percentage viewHeight: $viewHeight correctedTop: $correctedTop offset: $offset")
//                }
//            }
        }
    }
}

fun RecyclerView.getVisibleViews() : List<View> {
    val layoutManager = this.layoutManager as? LinearLayoutManager
    layoutManager?.let {
        val firstChildPos = layoutManager. findFirstVisibleItemPosition()
        val lastChildPos = layoutManager.findLastVisibleItemPosition()
        return (firstChildPos..lastChildPos).map { layoutManager.findViewByPosition(it) } .filter { it != null }
    }
    return arrayListOf()
}