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
import kotterknife.bindOptionalView
import kotterknife.bindView

/**
 * Created by luisramos on 30/01/2017.
 *
 * This fragment creates a ruler to insert Height or Weight.
 * It can be configured using [configureRuler].
 *
 * Usage in this project is done by subclassing and overriding [onCreate],
 * calling [configureRuler] with the specified fields.
 */
open class OnboardingRulerFragment : OnboardingBaseFragment() {

    var orientation: Orientation = Orientation.VERTICAL

    var layoutManager: LinearLayoutManager? = null
    var adapter: RulerAdapter? = null
    var scrollListener: RulerScrollingListener? = null
    val recyclerView: RecyclerView by bindView(R.id.recyclerView)
    val valueTextView: TextView by bindView(R.id.valueTextView)
    val smallTextView: TextView by bindView(R.id.smallValueTextView)

    var data : List<Int> = arrayListOf()

    var min = 80
    var max = 400

    val currentValue : Int
        get() = scrollListener?.currentValue ?: 0

    override fun viewsToAnimate(): List<View> {
        return arrayListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backgroundGradient = BackgroundGradient.TRACKER
    }

    override fun getViewLayoutId(): Int = if (orientation.isVertical())
            R.layout.fragment_onboarding_ruler_vertical
        else
            R.layout.fragment_onboarding_ruler_horizontal

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutOrientation = if (orientation.isVertical()) LinearLayoutManager.VERTICAL else LinearLayoutManager.HORIZONTAL
        layoutManager = LinearLayoutManager(activity, layoutOrientation, false)
        recyclerView.layoutManager = layoutManager

        if (orientation.isVertical()) {
            data = (min/10..max/10).toList()
        } else {
            data = (min/5..max/5).toList()
        }

        adapter = RulerAdapter(data, orientation, min)
        recyclerView.adapter = adapter

        recyclerView.viewTreeObserver.addOnGlobalLayoutListener {
            adapter?.recyclerViewSize = if (orientation.isVertical()) recyclerView.measuredHeight else recyclerView.measuredWidth
        }

        val lineHeight = context.resources.getDimension(R.dimen.rule_line_size)
        scrollListener = RulerScrollingListener(valueTextView, smallTextView, min, lineHeight, orientation)
        recyclerView.addOnScrollListener(scrollListener)
    }

    fun configureRuler(@StringRes title: Int, min: Int, max:Int, orientation: Orientation) {
        titleRes = title
        this.min = min
        this.max = max
        this.orientation = orientation
    }

    class RulerAdapter(var data: List<Int>, val orientation: Orientation, val min: Int) : RecyclerView.Adapter<RulerAdapter.ViewHolder>() {

        var recyclerViewSize = 0
            set(value) {
                val prev = field
                field = value
                if (prev != field) {
                    notifyDataSetChanged()
                }
            }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView: TextView? by bindOptionalView(R.id.textView)
//            val lineView: View = view.findViewById(R.id.lineView)

            fun setMargins(orientation: Orientation, margin1: Int, margin2: Int) {
                if (orientation.isVertical()) {
                    setTopBottomMargins(margin1, margin2)
                } else {
                    setLeftRightMargins(margin1, margin2)
                }
            }

            fun setTopBottomMargins(topMargin: Int, bottomMargin: Int) {
                val layoutParams = itemView.layoutParams as RecyclerView.LayoutParams
                layoutParams.topMargin = topMargin
                layoutParams.bottomMargin = bottomMargin
                itemView.layoutParams = layoutParams
            }

            fun setLeftRightMargins(leftMargin: Int, rightMargin: Int) {
                val layoutParams = itemView.layoutParams as RecyclerView.LayoutParams
                layoutParams.leftMargin= leftMargin
                layoutParams.rightMargin= rightMargin
                itemView.layoutParams = layoutParams
            }
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun getItemViewType(position: Int): Int {
            return if (orientation.isVertical()) R.layout.ruler_group_lines else R.layout.ruler_vertical_line
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent!!.context).inflate(viewType, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val margin1 = if (position == 0) recyclerViewSize / 2 else 0
            val margin2 = if (position == data.size - 1) recyclerViewSize / 2 else 0

            holder?.setMargins(orientation, margin1, margin2)

            if (orientation.isVertical()) {
                holder?.textView?.text = "${min + position * 10}cm"
            }
        }

    }

    class RulerScrollingListener(
            val valueTextView: TextView,
            val smallTextView: TextView,
            val minValue: Int,
            val ruleLineHeight: Float,
            val orientation: Orientation
    ) : RecyclerView.OnScrollListener() {

        private var overallScroll = 0
        var currentValue = 0

        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            overallScroll += if (orientation.isVertical()) dy else dx

            currentValue = minValue + (overallScroll / ruleLineHeight).toInt()
            valueTextView.text = currentValue.toString()
            smallTextView.text = if (orientation.isVertical()) convertCmToFeet(currentValue) else convertKgToLbs(currentValue)
        }

        fun convertKgToLbs(kgs: Int) : String {
            val lbs = (kgs.toDouble() / 0.453592).toInt()
            return "$lbs lbs"
        }

        fun convertCmToFeet(cm: Int) : String {
            val feet = cm.toDouble() / 30.48
            val intFeet = feet.toInt()
            val inches = ((feet - intFeet.toDouble()) * 12.0).toInt()
            return "$intFeet' $inches''"
        }
    }

    enum class Orientation {
        VERTICAL,
        HORIZONTAL;

        fun isVertical(): Boolean = this == Orientation.VERTICAL
    }
}