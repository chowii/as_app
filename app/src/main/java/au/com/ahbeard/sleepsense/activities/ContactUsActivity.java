package au.com.ahbeard.sleepsense.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import au.com.ahbeard.sleepsense.R;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by neal on 20/06/2016.
 */
public class ContactUsActivity extends BaseActivity {

    //
    // 1300 001 150
    //

    @OnClick(R.id.contact_us_button_email_us)
    void onClickEmailUs() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","mailto:sleepsense@ahbeard.com", null));
//        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
//        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }//

    @OnClick(R.id.contact_us_button_call_us)
    void onClickCallUs() {
        Uri numberUri = Uri.parse("tel:1300001150");
        Intent callIntent = new Intent(Intent.ACTION_DIAL, numberUri);
        startActivity(callIntent);
    }


    @OnClick(R.id.contact_us_image_view_facebook)
    void onClickFacebook() {
        Uri facebookUri = Uri.parse("https://www.facebook.com/ahbeardbeds/");
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW, facebookUri);
        startActivity(facebookIntent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contact_us);

        ButterKnife.bind(this);
    }
}
