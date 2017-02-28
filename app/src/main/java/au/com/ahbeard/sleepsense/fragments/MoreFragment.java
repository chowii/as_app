package au.com.ahbeard.sleepsense.fragments;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.activities.ContactUsActivity;
import au.com.ahbeard.sleepsense.activities.DebugActivity;
import au.com.ahbeard.sleepsense.activities.HelpActivity;
import au.com.ahbeard.sleepsense.activities.PreferenceActivity;
import au.com.ahbeard.sleepsense.activities.ProfileActivity;
import au.com.ahbeard.sleepsense.services.AnalyticsService;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoreFragment extends Fragment {

    private static final String TAG = MoreFragment.class.getSimpleName();
    @Bind(R.id.more_layout_items)
    ViewGroup mItemsLayout;

    private int debugClickCounter = 0;

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

        add(inflater, mItemsLayout, "FAQs", null, new Runnable() {
            @Override
            public void run() {
                AnalyticsService.instance().logEventSettingsTouchFAQ();
                startActivity(HelpActivity.getIntent(getContext(),"FAQs","https://www.sleepsense.com.au/faq"));
            }
        });
        Log.v(TAG, "added faqs");
        add(inflater, mItemsLayout, "About A.H. Beard", null, new Runnable() {
            @Override
            public void run() {
                AnalyticsService.instance().logEventSettingsTouchAbout();
                startActivity(HelpActivity.getIntent(getContext(),"About AH Beard","http://www.ahbeard.com.au/ourcompany"));
            }
        });
        Log.v(TAG, "added Contact us");
        add(inflater, mItemsLayout, "Contact us", null, new Runnable() {
            @Override
            public void run() {
                AnalyticsService.instance().logEventSettingsTouchContactUs();
                startActivity(new Intent(getActivity(), ContactUsActivity.class));
            }
        });
        Log.v(TAG, "added Improve Your sleep");
        add(inflater, mItemsLayout, "Improve your sleep", null, new Runnable() {
            @Override
            public void run() {
                AnalyticsService.instance().logEventSettingsTouchImproveSleep();
                startActivity(HelpActivity.getIntent(getContext(),"Improve your sleep","http://www.ahbeard.com.au/sleepchallenge"));
            }
        });
        Log.v(TAG, "added Preferences");
        add(inflater, mItemsLayout, "Preferences", null, new Runnable() {
            @Override
            public void run() {
                AnalyticsService.instance().logEventSettingsTouchPrefs();
                startActivity(new Intent(getActivity(), PreferenceActivity.class));
            }
        });
        Log.v(TAG, "added My Profile");
        addSpacer(mItemsLayout);
        add(inflater, mItemsLayout, "My Profile", null, new Runnable() {
            @Override
            public void run() {
                AnalyticsService.instance().logEventSettingsTouchProfile();
                startActivity(new Intent(getActivity(), ProfileActivity.class));
            }
        });
        Log.v(TAG, "added ToS");
        addSpacer(mItemsLayout);
        add(inflater, mItemsLayout, "Terms of Service", null, new Runnable() {
            @Override
            public void run() {
                AnalyticsService.instance().logEventSettingsTouchTermsOfService();
                startActivity(HelpActivity.getIntent(getContext(),"Terms of Service","https://sleepsense.com.au/terms-of-service"));
            }
        });
        Log.v(TAG, "added Privacy Policy");
        add(inflater, mItemsLayout, "Privacy Policy", null, new Runnable() {
            @Override
            public void run() {
                AnalyticsService.instance().logEventSettingsTouchPrivacyPolicy();
                startActivity(HelpActivity.getIntent(getContext(),"Privacy Policy","http://www.ahbeard.com.au/privacypolicy"));
            }
        });
        addSpacer(mItemsLayout);

        try {
            PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            add(inflater, mItemsLayout, "App Version", String.format("%s (%s)", packageInfo.versionName, packageInfo.versionCode), true, new Runnable() {
                @Override
                public void run() {
                    if (debugClickCounter > 3) { //Needs five touches to trigger
                        debugClickCounter = 0;
                        AnalyticsService.instance().logEventSettingsTouchDebug();
                        startActivity(new Intent(getActivity(), DebugActivity.class));
                    } else {
                        debugClickCounter++;
                    }
                }
            });
            addSpacer(mItemsLayout);
        } catch (PackageManager.NameNotFoundException e) {

        }

        return view;
    }

    public void add(LayoutInflater inflater, ViewGroup container, String title, String detail, Runnable runnable) {
        add(inflater, container, title, detail, false, runnable);
    }

    public void add(LayoutInflater inflater, ViewGroup container, String title, String detail, boolean hideChevron, Runnable runnable) {
        View view = inflater.inflate(R.layout.item_more,container,false);
        new MoreItemViewHolder().bind(view).populate(title,detail,runnable, hideChevron);
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

        Runnable mRunnable;

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
            if ( mRunnable != null ) {
                mRunnable.run();
            }
        }

        public MoreItemViewHolder bind(View view) {
            ButterKnife.bind(this,view);
            return this;
        }

        public void populate(String title, String detail, Runnable runnable, boolean hideChevron) {
            mRunnable = runnable;
            mTitleTextView.setText(title);
            mDetailTextView.setText(detail);
            if (hideChevron) {
                mChevronImageView.setVisibility(View.GONE);
                mLayout.setClickable(true);
            } else if ( runnable == null ) {
                mChevronImageView.setVisibility(View.GONE);
                mLayout.setClickable(false);
            } else {
                mChevronImageView.setVisibility(View.VISIBLE);
                mLayout.setClickable(true);
            }
        }


    }

}
