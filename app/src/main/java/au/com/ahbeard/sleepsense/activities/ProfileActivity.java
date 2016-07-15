package au.com.ahbeard.sleepsense.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.services.PreferenceService;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity {

    private String mSex;

    @OnClick(R.id.profile_button_male)
    void onClickMale() {
        mSex = "Male";
        mMaleButton.setSelected(true);
        mFemaleButton.setSelected(false);
        mUnspecifiedButton.setSelected(false);
    }

    @Bind(R.id.profile_button_male)
    Button mMaleButton;

    @OnClick(R.id.profile_button_female)
    void onClickFemale() {
        mSex = "Female";
        mMaleButton.setSelected(false);
        mFemaleButton.setSelected(true);
        mUnspecifiedButton.setSelected(false);
    }

    @Bind(R.id.profile_button_female)
    Button mFemaleButton;

    @OnClick(R.id.profile_button_unspecified)
    void onClickUnspecified() {
        mSex = "Unspecified";
        mMaleButton.setSelected(false);
        mFemaleButton.setSelected(false);
        mUnspecifiedButton.setSelected(true);
    }

    @Bind(R.id.profile_button_unspecified)
    Button mUnspecifiedButton;

    @OnClick(R.id.profile_button_save_profile)
    void onClickSaveProfile() {
        Integer age = Integer.parseInt(mAgeEditText.getText().toString());
        String emailAddress = mEmailAddressEditText.getText().toString();
        PreferenceService.instance().setProfile(mSex, age, emailAddress);
        finish();
    }

    @Bind(R.id.profile_edit_text_age)
    EditText mAgeEditText;

    @Bind(R.id.profile_edit_text_email_address)
    EditText mEmailAddressEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Stop keyboard from focusing on EditText
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        mSex = PreferenceService.instance().getProfileSex();

        if ("male".equalsIgnoreCase(mSex)) {
            mMaleButton.setSelected(true);
        } else if ("female".equalsIgnoreCase(mSex)) {
            mFemaleButton.setSelected(true);
        } else if ("unspecified".equalsIgnoreCase(mSex)) {
            mUnspecifiedButton.setSelected(true);
        }

        mAgeEditText.setText(PreferenceService.instance().getProfileAge());
        mEmailAddressEditText.setText(PreferenceService.instance().getProfileEmailAddress());

    }


}
