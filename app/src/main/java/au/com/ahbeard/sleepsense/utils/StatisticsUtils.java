package au.com.ahbeard.sleepsense.utils;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import au.com.ahbeard.sleepsense.R;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by neal on 17/05/2016.
 */
public class StatisticsUtils {

    private ViewGroup mStatisticsLayout;

    public StatisticsUtils(ViewGroup statisticsLayout) {
        mStatisticsLayout = statisticsLayout;
    }

    /**
     * @param color
     * @param name
     * @param value
     *
     * @return
     */
    public StatisticViewHolder addStatistic(int color, CharSequence name, CharSequence value) {
        return addStatistic(color, name, value, null);
    }

    /**
     * @param color
     * @param name
     * @param value
     * @param expandedText
     *
     * @return
     */
    public StatisticViewHolder addStatistic(int color, CharSequence name, CharSequence value, CharSequence expandedText) {

        View view = LayoutInflater.from(mStatisticsLayout.getContext()).inflate(R.layout.item_statistic, mStatisticsLayout, false);

        final StatisticViewHolder viewHolder = new StatisticViewHolder();

        ButterKnife.bind(viewHolder, view);

        viewHolder.nameTextView.setText(name);
        viewHolder.valueTextView.setText(value);

        if (expandedText != null) {
            viewHolder.expandCollapseImageView.setVisibility(View.VISIBLE);
            viewHolder.expandedTextView.setText(expandedText);
            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewHolder.expandedLayout.getVisibility() == View.VISIBLE) {
                        viewHolder.expandedLayout.setVisibility(View.GONE);
                        viewHolder.expandCollapseImageView.setImageResource(R.drawable.statistic_expand_normal);
                    } else {
                        viewHolder.expandedLayout.setVisibility(View.VISIBLE);
                        viewHolder.expandCollapseImageView.setImageResource(R.drawable.statistic_contract_normal);
                    }
                }
            });

        }


        mStatisticsLayout.addView(view);

        return viewHolder;
    }

    public static CharSequence valueSuffix(int mainColor, int alternateColor, String value, String suffix) {

        SpannableStringBuilder builder = new SpannableStringBuilder();

        appendToSpannableStringBuilder(builder,value,new ForegroundColorSpan(mainColor));
        appendToSpannableStringBuilder(builder,suffix,new ForegroundColorSpan(alternateColor));

        return builder;
    }

    public static CharSequence timeInSecondsSinceEpochToString(int mainColor, int alternateColor, Float timeInSecondsSinceEpoch) {

        if (timeInSecondsSinceEpoch != null) {

            int sleepHours = (int) (timeInSecondsSinceEpoch / (60 * 60));
            int sleepMinutes = (int) (timeInSecondsSinceEpoch / (60)) % 60;

            SpannableStringBuilder builder = new SpannableStringBuilder();

            if ( sleepHours > 0 ) {
                appendToSpannableStringBuilder(builder,Integer.toString(sleepHours),new ForegroundColorSpan(mainColor));
                appendToSpannableStringBuilder(builder," h ",new ForegroundColorSpan(alternateColor));
            }

            appendToSpannableStringBuilder(builder,Integer.toString(sleepMinutes),new ForegroundColorSpan(mainColor));
            appendToSpannableStringBuilder(builder," min",new ForegroundColorSpan(alternateColor));

            return builder;
        } else {
            return "";
        }
    }

    public static void appendToSpannableStringBuilder(SpannableStringBuilder builder, CharSequence charSequence, Object object) {

        SpannableString spannableString = new SpannableString(charSequence);

        spannableString.setSpan(object,0,charSequence.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.append(spannableString);


    }


    /**
     *
     */
    public class StatisticViewHolder {

        @Bind(R.id.statistic_layout)
        public View layout;
        @Bind(R.id.statistic_image_view)
        public ImageView imageView;
        @Bind(R.id.statistic_text_view_name)
        public TextView nameTextView;
        @Bind(R.id.statistic_text_view_value)
        public TextView valueTextView;
        @Bind(R.id.statistic_image_view_expand_collapse)
        public ImageView expandCollapseImageView;
        @Bind(R.id.statistic_layout_expanded)
        public View expandedLayout;
        @Bind(R.id.statistic_text_view_expanded_text)
        public TextView expandedTextView;

    }

}
