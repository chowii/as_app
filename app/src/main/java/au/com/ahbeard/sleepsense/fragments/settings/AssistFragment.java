package au.com.ahbeard.sleepsense.fragments.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.fragments.BaseFragment;

/**
 * Created by sabbib on 1/03/2017.
 */

public class AssistFragment extends BaseFragment {

    protected WebView webView;

    protected String url;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_privacy_policy, container, false);

        webView = (WebView) view.findViewById(R.id.privacy_policy_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);

        return view;
    }

    public void configure(String url) {
        this.url = url;
    }



    public void onBackPressed(){
        if(webView.canGoBack()){
            webView.goBack();
            return;
        }
//        super.onBackPressed();
    }
}
