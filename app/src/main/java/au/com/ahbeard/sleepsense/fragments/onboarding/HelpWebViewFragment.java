package au.com.ahbeard.sleepsense.fragments.onboarding;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import au.com.ahbeard.sleepsense.R;

public class HelpWebViewFragment extends Fragment {


    public HelpWebViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help_web_view, container, false);
        WebView myWebView = (WebView) view.findViewById(R.id.webview);
        myWebView.loadUrl("http://www.example.com");
        return view;
    }

    public static  HelpWebViewFragment newInstance() {
        HelpWebViewFragment frag = new HelpWebViewFragment();
        return frag;
    }

}
