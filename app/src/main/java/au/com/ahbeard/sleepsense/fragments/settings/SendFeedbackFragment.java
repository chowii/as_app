package au.com.ahbeard.sleepsense.fragments.settings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.zip.ZipEntry;

import au.com.ahbeard.sleepsense.BuildConfig;
import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.services.DebugEmailService;
import au.com.ahbeard.sleepsense.services.log.SSLog;

/**
 * Created by sabbib on 14/03/2017.
 */

public class SendFeedbackFragment extends Fragment implements View.OnClickListener {

	EditText body;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_send_feedback, container, false);
		body = (EditText) v.findViewById(R.id.email_body);

		TextView sendFeedbackButton = (TextView) v.findViewById(R.id.send_feedback_button);
		TextView cancelFeedbackButton = (TextView) v.findViewById(R.id.cancel_sending_feedback_action);
		sendFeedbackButton.setOnClickListener(this);
		cancelFeedbackButton.setOnClickListener(this);
		return v;
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

	private boolean sendFeedback(){
		Intent mailIntent = DebugEmailService.getDebugEmailIntent(getContext());
		if(body.getText() != null) mailIntent.putExtra(Intent.EXTRA_TEXT, body.getText());
		try{ startActivity(Intent.createChooser(mailIntent, "Sending email...")); }
		catch (Exception e){ e.printStackTrace(); return false;}
		return true;
	}
}
