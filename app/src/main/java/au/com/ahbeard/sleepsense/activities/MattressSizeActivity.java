package au.com.ahbeard.sleepsense.activities;


import android.content.Intent;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.fragments.onboarding.MattressSizeNotSureDialogFragment;
import au.com.ahbeard.sleepsense.services.SharedPreferencesStore;
import au.com.ahbeard.sleepsense.utils.GlobalVars;
import rx.subscriptions.CompositeSubscription;

public class MattressSizeActivity extends BaseActivity {

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mattress_size);
    }

    @Override
    protected void onDestroy() {
        mCompositeSubscription.clear();
        super.onDestroy();
    }

    public void buttonSingleClick(View arg) {
        connectToMattressPump(GlobalVars.MattressType.SINGLE);
    }

    public void buttonKingSingleClick(View arg) {
        connectToMattressPump(GlobalVars.MattressType.QUEEENKING);
    }

    public void buttonDoubleClick(View arg) {
        connectToMattressPump(GlobalVars.MattressType.QUEEENKING);
    }

    public void buttonQueenClick(View arg) {
        connectToMattressPump(GlobalVars.MattressType.QUEEENKING);
    }

    public void buttonKingClick(View arg) {
        connectToMattressPump(GlobalVars.MattressType.QUEEENKING);
    }

    public void buttonSplitKingClick(View arg) {
        connectToMattressPump(GlobalVars.MattressType.SPLITKING);
    }

    //display pop-up fragment
    public void buttonNotSureClick(View arg) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        BottomSheetDialogFragment newFragment = MattressSizeNotSureDialogFragment.newInstance();
        newFragment.show(ft,"dialog");
    }

    private void connectToMattressPump(GlobalVars.MattressType mattressType) {
        if (isBluetoothEnabled()) {
            SharedPreferencesStore.PutItem(GlobalVars.SHARED_PREFERENCE_MATTRESS_TYPE,
                    mattressType.name(), getApplicationContext());

//            Take user to Pump device and pass selected mattress type as param
            Intent intent = ConnectingPumpActivity.getConnectingPumpActivity(this);
            intent.putExtra(GlobalVars.SELECTED_MATTRESS_TYPE, mattressType);
            startActivity(intent);
            finish();
        }
        else {
            showBluetoothOffAlertView();
        }
    }
}