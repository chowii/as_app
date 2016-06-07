package au.com.ahbeard.sleepsense.fragments.onboarding;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.MailTo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import au.com.ahbeard.sleepsense.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 */
public class OnBoardingHelpFragment extends OnBoardingFragment {

    private OnActionListener mOnActionListener;

    @Bind(R.id.on_board_button_try_again)
    Button mContinueButton;

    @OnClick(R.id.on_board_button_try_again)
    public void onContinueClicked() {
        if (mOnActionListener != null) {
            mOnActionListener.onRetryButtonClicked();
        }
    }

    @Bind(R.id.on_boarding_help_web_view)
    WebView mWebView;

    public interface OnActionListener {
        void onRetryButtonClicked();

        void onCallButtonClicked();
    }

    public OnBoardingHelpFragment() {
        // Required empty public constructor
    }

    public static OnBoardingHelpFragment newInstance() {
        OnBoardingHelpFragment fragment = new OnBoardingHelpFragment();
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
        View view = inflater.inflate(R.layout.fragment_on_boarding_help, container, false);

        ButterKnife.bind(this, view);

        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.setWebChromeClient(new WebChromeClient() {


        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (MailTo.isMailTo(url)) {
                    MailTo mailTo = MailTo.parse(url);
                    Intent mailIntent = createEmailIntent(getContext(), mailTo.getTo(), mailTo.getSubject(), mailTo.getBody(), mailTo.getCc());
                    PackageManager packageManager = getActivity().getPackageManager();
                    if (mailIntent.resolveActivity(packageManager) != null) {
                        startActivity(mailIntent);
                    } else {
                        Log.d("Help", "No facility available to handle mailto: url");
                    }
                    return false;
                } else if (url.startsWith("tel:")) {
                    Intent telephoneIntent = new Intent(url);
                    PackageManager packageManager = getActivity().getPackageManager();
                    if (telephoneIntent.resolveActivity(packageManager) != null) {
                        startActivity(telephoneIntent);
                    } else {
                        Log.d("Help", "No facility available to handle tel: url");
                    }
                    return true;
                } else {
                    view.loadUrl(url);
                }
                return true;
            }

            private Intent createEmailIntent(Context context, String address, String subject, String body, String cc) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
                intent.putExtra(Intent.EXTRA_TEXT, body);
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_CC, cc);
                intent.setType("message/rfc822");
                return intent;
            }
        });

        mWebView.loadUrl("http://share.mentallyfriendly.com/sleepsense/#!/faq");

        return view;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnActionListener) {
            mOnActionListener = (OnActionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnActionListener = null;
    }

    @Override
    public boolean onBackPressed() {

        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        } else {
            return super.onBackPressed();
        }

    }
}
