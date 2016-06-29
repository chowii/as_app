package au.com.ahbeard.sleepsense.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.Settings;
import au.com.ahbeard.sleepsense.activities.InStoreHelpActivity;
import au.com.ahbeard.sleepsense.activities.InStoreNewOnBoardActivity;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InStoreMoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InStoreMoreFragment extends Fragment {

    @Bind(R.id.more_layout_items)
    ViewGroup mItemsLayout;

    @OnClick(R.id.in_store_more_image_view_close)
    void onClickClose() {
        getActivity().onBackPressed();
    }

    public InStoreMoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InStoreMoreFragment newInstance() {
        InStoreMoreFragment fragment = new InStoreMoreFragment();
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
        View view = inflater.inflate(R.layout.fragment_in_store_more, container, false);

        ButterKnife.bind(this, view);

        final Intent launchIntent = getContext().getPackageManager().getLaunchIntentForPackage(Settings.INTERACTIVE_EXPERIENCE_APP_ID);

        if ( launchIntent != null ) {
            add(inflater, mItemsLayout, "Back to Interactive Experience", null, new Runnable() {
                @Override
                public void run() {
                    launchIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(launchIntent);
                }
            });

            addSpacer(mItemsLayout);
        }

        add(inflater, mItemsLayout, "FAQs", null, new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getActivity(),InStoreHelpActivity.class));
            }
        });
        addSpacer(mItemsLayout);

        add(inflater, mItemsLayout, "Reset Sleepsense", null, new Runnable() {
            @Override
            public void run() {

                new AlertDialog.Builder(getActivity()).setPositiveButton(getString(R.string.preference_dialog_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(InStoreNewOnBoardActivity.getOnBoardActivity(getActivity()));
                        getActivity().finish();
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).setMessage("Are you sure you want to reset Sleepsense and rediscover devices?").create().show();

            }
        });
        addSpacer(mItemsLayout);

        try {
            PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            add(inflater,mItemsLayout,"App Version", packageInfo.versionName,null);
            addSpacer(mItemsLayout);
        } catch (PackageManager.NameNotFoundException e) {

        }

//        if (BuildConfig.DEBUG ) {
//            add(inflater,mItemsLayout,"Debug",null,new Intent(getActivity(), DebugActivity.class));
//            addSpacer(mItemsLayout);
//            addSpacer(mItemsLayout);
//        }
//
        return view;
    }

    public void add(LayoutInflater inflater, ViewGroup container, String title, String detail, Runnable runnable) {
        View view = inflater.inflate(R.layout.item_more,container,false);
        new MoreItemViewHolder().bind(view).populate(title,detail,runnable);
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

        public void populate(String title, String detail, Runnable runnable ) {
            mRunnable = runnable;
            mTitleTextView.setText(title);
            mDetailTextView.setText(detail);
            if ( mRunnable == null ) {
                mChevronImageView.setVisibility(View.GONE);
                mLayout.setClickable(false);
            } else {
                mChevronImageView.setVisibility(View.VISIBLE);
                mLayout.setClickable(true);
            }
        }


    }

}
