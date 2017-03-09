package au.com.ahbeard.sleepsense.fragments.settings;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.fragments.BaseFragment;

/**
 * Created by sabbib on 7/03/2017.
 */

public class ContactUsFragment extends BaseFragment implements View.OnClickListener{

    public ContactUsFragment(){

    }

    SettingsBaseFragment frag;

    public ContactUsFragment(SettingsBaseFragment baseFragment){
        frag = baseFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);

        Button callUsButton = (Button) view.findViewById(R.id.contact_us_button_call_us);
        Button visitWebButton = (Button) view.findViewById(R.id.contact_us_button_visit_web);
        callUsButton.setOnClickListener(this);
        visitWebButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if ((requestCode == 123)
                && (grantResults.length > 0)
                && (grantResults[0] == PackageManager.PERMISSION_GRANTED))

            call();
    }

    private void call(){
        Uri numberParsed = parseNumber(getResources().getString(R.string.contact_us_number));
        Intent callIntent = new Intent();
        callIntent.setData(numberParsed);

        int permissionCheck =
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CALL_PHONE);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) startActivity(callIntent);
        else ActivityCompat.requestPermissions(getActivity(),
                new String[]{android.Manifest.permission.CALL_PHONE}, 123);
    }

    private void openWebView(){
        CustomerInformationFragment aF = new CustomerInformationFragment();
        aF.configure(getResources().getString(R.string.contact_us_web_url));
        frag.replaceFragment(aF);
    }

    private Uri parseNumber(String numberResource){ return Uri.parse("tel:" + numberResource); }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch(viewId){
            case R.id.contact_us_button_call_us:
                call();
                break;
            case R.id.contact_us_button_visit_web:
                openWebView();
        }
    }
}
