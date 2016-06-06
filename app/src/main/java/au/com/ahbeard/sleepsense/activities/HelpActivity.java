package au.com.ahbeard.sleepsense.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import au.com.ahbeard.sleepsense.R;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by neal on 6/06/2016.
 */
public class HelpActivity extends BaseActivity {

    public static final String EXTRA_HEADING = "heading";
    public static final String EXTRA_URL = "url";

    @Bind(R.id.help_web_view)
    WebView mWebView;

    @Bind(R.id.help_text_view_heading)
    TextView mHeadingTextView;

    private String mHeading;
    private String mURL;

    public static Intent getIntent(Context context, String heading, String url) {
        Intent intent = new Intent(context,HelpActivity.class);
        intent.putExtra(EXTRA_HEADING,heading);
        intent.putExtra(EXTRA_URL,url);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_help);

        ButterKnife.bind(this);

        mHeading = getIntent().getStringExtra(EXTRA_HEADING);
        mURL = getIntent().getStringExtra(EXTRA_URL);

        mHeadingTextView.setText(mHeading);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient(){

        });
        mWebView.setWebViewClient(new WebViewClient(){

        });

        mWebView.loadUrl(mURL);

    }
}
