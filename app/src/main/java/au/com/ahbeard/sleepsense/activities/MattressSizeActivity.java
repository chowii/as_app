package au.com.ahbeard.sleepsense.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import au.com.ahbeard.sleepsense.R;
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

    public void buttonNotSureClick(View arg) {
        //TODO: display not sure content in pop-up
    }

    private void connectToMattressPump(GlobalVars.MattressType mattressType) {
        if (isBluetoothEnabled()) {
            SharedPreferencesStore.PutItem(GlobalVars.SHARED_PREFERENCE_MATTRESS_TYPE,
                    mattressType.name(), getApplicationContext());
            //TODO: test code below, to be deleted
//            Intent intent = AgeActivity.getAgeActivity(this);
//            startActivity(intent);
//            finish();

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
