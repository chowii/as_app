package au.com.ahbeard.sleepsense.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.ahbeard.sleepsense.R;

/**
 * Created by neal on 10/03/2016.
 */
public class SimpleTabStrip extends LinearLayout {

    private ViewPager mViewPager;

    private List<TabHolder> mTabs = new ArrayList<>();

    private int mTabLayoutId;
    private int mTabColor;
    private int mSelectedTabColor;

    public SimpleTabStrip(Context context) {
        super(context);
        init(context, null, 0);
    }

    public SimpleTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public SimpleTabStrip(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.SimpleTabStrip, defStyleAttr, 0);

        mTabLayoutId = a.getResourceId(R.styleable.SimpleTabStrip_tabLayout, 0);
        mTabColor = a.getColor(R.styleable.SimpleTabStrip_tabColor, Color.GRAY);
        mSelectedTabColor = a.getColor(R.styleable.SimpleTabStrip_selectedTabColor, Color.BLUE);

    }

    public void setViewPager(ViewPager viewPager) {

        mViewPager = viewPager;

        mTabs.clear();

        this.removeAllViews();

        if (viewPager.getAdapter() instanceof TabProvider) {

            for (int i = 0; i < mViewPager.getAdapter().getCount(); i++) {
                createTab(i);
            }
        }

        updateTabs(viewPager.getCurrentItem());

    }

    private void createTab(final int position) {

        if (mTabLayoutId > 0) {

            View view = LayoutInflater.from(getContext()).inflate(mTabLayoutId, this, false);

            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            view.setLayoutParams(layoutParams);

            TabHolder tabHolder = new TabHolder();

            tabHolder.tabImageView = (ImageView) view.findViewById(R.id.tab_image_view);
            tabHolder.tabNameTextView = (TextView) view.findViewById(R.id.tab_text_view_name);

            mTabs.add(tabHolder);

            view.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(position,false);updateTabs(position);
                }
            });

            addView(view);

        }
    }

    private void updateTabs(int selectedPosition) {

        TabProvider tabProvider = (TabProvider) mViewPager.getAdapter();

        for ( int position=0; position< mTabs.size(); position++ ) {

            mTabs.get(position).tabNameTextView.setText(tabProvider.getName(position));

            if (position==selectedPosition) {
                mTabs.get(position).tabImageView.setImageResource(tabProvider.getSelectedIconResourceId(position));
                mTabs.get(position).tabNameTextView.setTextColor(mSelectedTabColor);
            } else {
                mTabs.get(position).tabImageView.setImageResource(tabProvider.getIconResourceId(position));
                mTabs.get(position).tabNameTextView.setTextColor(mTabColor);
            }

        }

        invalidate();

    }



    public interface TabProvider {

        String getName(int position);
        int getIconResourceId(int position);
        int getSelectedIconResourceId(int position);

    }

    public class TabHolder {

        ImageView tabImageView;
        TextView tabNameTextView;

    }

}
