package au.com.ahbeard.sleepsense.activities;

import android.content.Context;
import android.content.Intent;
import android.net.MailTo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.fragments.InStoreHelpFragment;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by neal on 6/06/2016.
 */
public class InStoreHelpActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_instore_help);

        ButterKnife.bind(this);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.in_store_help_container, InStoreHelpFragment.newInstance()).commit();


    }


}
