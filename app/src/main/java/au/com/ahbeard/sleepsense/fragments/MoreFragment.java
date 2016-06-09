package au.com.ahbeard.sleepsense.fragments;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.activities.DebugActivity;
import au.com.ahbeard.sleepsense.activities.HelpActivity;
import au.com.ahbeard.sleepsense.activities.HomeActivity;
import au.com.ahbeard.sleepsense.activities.PreferenceActivity;
import au.com.ahbeard.sleepsense.activities.ProfileActivity;
import au.com.ahbeard.sleepsense.services.SleepService;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoreFragment extends Fragment {

    @Bind(R.id.more_layout_items)
    ViewGroup mItemsLayout;

    public MoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MoreFragment newInstance() {
        MoreFragment fragment = new MoreFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_more, container, false);

        ButterKnife.bind(this, view);

        add(inflater,mItemsLayout,"FAQs",null,HelpActivity.getIntent(getContext(),"FAQs","http://share.mentallyfriendly.com/sleepsense/#!/faq"));
        add(inflater,mItemsLayout,"About A.H. Beard",null,HelpActivity.getIntent(getContext(),"About AH Beard","http://share.mentallyfriendly.com/sleepsense/#!/faq"));
        add(inflater,mItemsLayout,"Contact us",null,HelpActivity.getIntent(getContext(),"Contact us","http://share.mentallyfriendly.com/sleepsense/#!/faq"));
        add(inflater,mItemsLayout,"Improve your sleep",null,HelpActivity.getIntent(getContext(),"Improve your sleep","http://share.mentallyfriendly.com/sleepsense/#!/faq"));
        add(inflater,mItemsLayout,"Preferences",null,new Intent(getActivity(), PreferenceActivity.class));
        addSpacer(mItemsLayout);
        add(inflater,mItemsLayout,"My Profile",null,new Intent(getActivity(), ProfileActivity.class));
        addSpacer(mItemsLayout);
        add(inflater,mItemsLayout,"Terms of Service",null,HelpActivity.getIntent(getContext(),"Terms of Service","http://share.mentallyfriendly.com/sleepsense/#!/faq"));
        add(inflater,mItemsLayout,"Privacy Policy",null,HelpActivity.getIntent(getContext(),"Privacy Policy","http://share.mentallyfriendly.com/sleepsense/#!/faq"));
        addSpacer(mItemsLayout);

        try {
            PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            add(inflater,mItemsLayout,"App Version", packageInfo.versionName,null);
            addSpacer(mItemsLayout);
        } catch (PackageManager.NameNotFoundException e) {

        }

        add(inflater,mItemsLayout,"Debug",null,new Intent(getActivity(), DebugActivity.class));
        addSpacer(mItemsLayout);

        return view;
    }

    public void add(LayoutInflater inflater, ViewGroup container, String title, String detail, Intent intent) {
        View view = inflater.inflate(R.layout.item_more,container,false);
        new MoreItemViewHolder().bind(view).populate(title,detail,intent);
        container.addView(view);
    }

    public void addSpacer(ViewGroup container) {
        View view = new View(getContext());
        view.setLayoutParams(new LinearLayout.LayoutParams(0,(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,20,getResources().getDisplayMetrics())));
        container.addView(view);
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    class MoreItemViewHolder {

        Intent mIntent;

        @Bind(R.id.more_text_view_title)
        TextView mTitleTextView;
        @Bind(R.id.more_text_view_detail)
        TextView mDetailTextView;
        @Bind(R.id.more_image_view_chevron)
        ImageView mChevronImageView;
        @Bind(R.id.more_layout)
        ViewGroup mLayout;

        @OnClick(R.id.more_layout)
        void itemClicked() {
            if ( mIntent != null ) {
                startActivity(mIntent);
            }
        }

        public MoreItemViewHolder bind(View view) {
            ButterKnife.bind(this,view);
            return this;
        }

        public void populate(String title, String detail, Intent intent ) {
            mIntent = intent;
            mTitleTextView.setText(title);
            mDetailTextView.setText(detail);
            if ( mIntent == null ) {
                mChevronImageView.setVisibility(View.GONE);
                mLayout.setClickable(false);
            } else {
                mChevronImageView.setVisibility(View.VISIBLE);
                mLayout.setClickable(true);
            }
        }


    }

}
