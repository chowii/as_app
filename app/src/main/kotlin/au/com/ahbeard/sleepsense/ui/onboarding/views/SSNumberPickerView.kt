package au.com.ahbeard.sleepsense.ui.onboarding.views

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SnapHelper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import au.com.ahbeard.sleepsense.R
import au.com.ahbeard.sleepsense.ui.extensions.getVisibleViews
import au.com.ahbeard.sleepsense.ui.onboarding.base.RecyclerWithMarginsAdapter
import kotterknife.bindView

/**
 * Created by luisramos on 14/03/2017.
 */
class SSNumberPickerView : RecyclerView {

    private var minValue = 3
    private var maxValue = 120
    private var defaultValue = 36
    var data : List<RowViewModel> = arrayListOf()

    var linearLayoutManager : LinearLayoutManager = LinearLayoutManager(context)
    var pickerRowAdapter : PickerRowAdapter? = null
    var snapHelper: SnapHelper = LinearSnapHelper()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    init {
        layoutManager = linearLayoutManager

        addOnScrollListener(PickerScrollListener())

        snapHelper.attachToRecyclerView(this)

        viewTreeObserver.addOnGlobalLayoutListener {
            pickerRowAdapter?.recyclerViewSize = measuredHeight
        }
    }

    var shouldCenterOnDefault = true
    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)

        val spec = MeasureSpec.getMode(heightSpec)

        if (shouldCenterOnDefault && height != 0) {
            shouldCenterOnDefault = false
            val rowHeight = resources.getDimension(R.dimen.onboarding_picker_line_height)
            val offset = (height / 2) - rowHeight
            linearLayoutManager.scrollToPositionWithOffset(defaultValue - minValue, offset.toInt())
        }
    }

    fun configure(min: Int, max: Int, format: String, defaultValue: Int) {
        minValue = min
        maxValue = max
        this.defaultValue = defaultValue
        data = (min..max).toList().map { RowViewModel(it, java.lang.String.format(format, it)) }

        if (adapter == null) {
            val pickerLineHeight = resources.getDimension(R.dimen.onboarding_picker_line_height).toInt()
            pickerRowAdapter = PickerRowAdapter(data, pickerLineHeight)
            adapter = pickerRowAdapter
        } else {
            pickerRowAdapter?.data = data
            pickerRowAdapter?.notifyDataSetChanged()
        }
    }

    fun getSelectedValue() : Int {
        val firstChildPos = linearLayoutManager.findFirstVisibleItemPosition()
        val lastChildPos = linearLayoutManager.findLastVisibleItemPosition()
//        if (firstChildPos != null && lastChildPos != null) {
        val pos = (lastChildPos - firstChildPos) / 2 + firstChildPos
        if (pos >= 0 && pos < data.size) {
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

        var currentOffset: Int = 0

        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            currentOffset += dy

            val halfHeight = (recyclerView?.height ?: 0) / 2

            recyclerView?.getVisibleViews()?.forEach {
                val centerTreshold = it.top + it.height / 2
                val space = if (centerTreshold <= halfHeight) centerTreshold else halfHeight - (centerTreshold - halfHeight)
                val scalePercentage = (Math.max(space, 20) / halfHeight)

                val minScale = 0.6
                val scale = (minScale + scalePercentage * (1.0 - minScale)).toFloat()

                val viewHolder = recyclerView.getChildViewHolder(it) as? PickerRowAdapter.ViewHolder
                viewHolder?.let {
                    viewHolder.textView.scaleX = scale
                    viewHolder.textView.scaleY = scale
//                    viewHolder.textView.alpha = percentage.toFloat()
                }
            }

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

    class RowViewModel(val value: Int, val text: String)
}