package au.com.ahbeard.sleepsense.fragments.settings;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.services.DebugEmailService;

/**
 * Created by sabbib on 14/03/2017.
 */

public class SendFeedbackFragment extends Fragment implements View.OnClickListener {

    EditText body;

	boolean shouldDismiss = false;

	@Override
	public void onResume() {
		super.onResume();

		if (shouldDismiss) {
			removeFragment();
		}
	}

	@Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_send_feedback, container, false);
        body = (EditText) v.findViewById(R.id.email_body);

        int permissionCheck = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) sendFeedback();

        TextView sendFeedbackButton = (TextView) v.findViewById(R.id.send_feedback_button);
        TextView cancelFeedbackButton = (TextView) v.findViewById(R.id.cancel_sending_feedback_action);
        sendFeedbackButton.setOnClickListener(this);
        cancelFeedbackButton.setOnClickListener(this);
        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult){
        if((requestCode == 1234)
                &&
           (grantResult.length > 0)
                &&
		   (grantResult[0] == PackageManager.PERMISSION_GRANTED)){
            sendFeedback();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        titleTextView.setText("Send Feedback");
        super.onViewCreated(view, savedInstanceState);
    }

	    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.send_feedback_button: sendFeedback(); break;
            case R.id.cancel_sending_feedback_action: getActivity().onBackPressed(); break;
        }
    }

    private void sendFeedback(){
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if( permissionCheck == PackageManager.PERMISSION_GRANTED) {

            DebugEmailService.getDebugEmailIntent(getContext(), new DebugEmailService.OnIntentCreatedCallback() {
                @Override
                public void onIntentCreated(Intent intentMail) {
                    if(body.getText() != null) intentMail.putExtra(Intent.EXTRA_TEXT, body.getText());
                    try{ startActivity(Intent.createChooser(intentMail, "Sending email...")); }
                    catch (Exception e){ e.printStackTrace();}
	                shouldDismiss = true;
                }
            });

        }
        else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1234);
        }
    }


    public void removeFragment() {
	    //TODO complete going back to prev fragment
	    getActivity().onBackPressed();
    }

    public static Fragment newInstance() {
        SendFeedbackFragment fragment = new SendFeedbackFragment();
        Bundle agrs = new Bundle();
        fragment.setArguments(agrs);
        return fragment;
    }


}
