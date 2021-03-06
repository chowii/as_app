package au.com.ahbeard.sleepsense.fragments.settings;

import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import au.com.ahbeard.sleepsense.BuildConfig;
import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.coordinator.ProfileSetupCoordinator;
import au.com.ahbeard.sleepsense.coordinator.onboardingFlow.OnboardingFragmentType;
import au.com.ahbeard.sleepsense.services.AnalyticsService;

/**
 * Created by sabbib on 6/03/2017.
 */
class SettingsFragmentFactory {

    static Fragment createMyProfileFragment(final SettingsBaseFragment baseFragment){
        MyProfileSettingsFragment frag = new MyProfileSettingsFragment();

        frag.setOnItemClickListener(new MyProfileSettingsFragment.ItemClickListener() {
            @Override
            public void onItemClick(int titleRes) {
                OnboardingFragmentType type;
                switch (titleRes) {
                    case R.string.settings_profile_sleep_target:
                        type = OnboardingFragmentType.SLEEP_TARGET_SELECT;
                        break;
                    case R.string.settings_profile_weight:
                        type = OnboardingFragmentType.WEIGHT_SELECT;
                        break;
                    case R.string.settings_profile_height:
                        type = OnboardingFragmentType.HEIGHT_SELECT;
                        break;
                    case R.string.settings_profile_age:
                        type = OnboardingFragmentType.AGE_SELECT;
                        break;
                    case R.string.settings_profile_gender:
                        type = OnboardingFragmentType.GENDER_SELECT;
                        break;
                    default:
                        type = OnboardingFragmentType.AGE_SELECT;
                        break;
                }
                Fragment frag = ProfileSetupCoordinator.newInstance(baseFragment.getChildFragmentManager(), type);
                baseFragment.hideBottomBar();
                baseFragment.replaceFragment(frag);
            }
        });

        return frag;
    }

    static SettingsListFragment createSupportFragment(final SettingsBaseFragment baseFragment){

        SettingsListFragment listFragment = new SettingsListFragment();

        List<SettingsListItem> supportList = new ArrayList<>();

        supportList.add(new SettingsListItem("FAQs"));
        supportList.add(new SettingsListItem("Troubleshooting"));
        supportList.add(new SettingsListItem("Send Feedback"));
        supportList.add(new SettingsListItem("Contact Us"));
        supportList.add(new SettingsListItem("Update App"));

        listFragment.configure(baseFragment,
                R.string.settings_support_title,    /* Fragment Title  */
                R.layout.fragment_settings,         /* Fragment Layout */
                R.id.recyclerView,                  /* RecyclerView    */
                R.layout.item_settings,             /* Item TextView   */
                supportList,
                new SettingsListFragment.SettingsAdapterOnItemClickListener() {
                    @Override
                    public void onItemClick(String buttonTitle, int position) {
                        switch(buttonTitle) {
                            case "FAQs":
                                break;
                            case "Troubleshooting":
                                SettingsListFragment troubleshootingFragment = createTroubleshootingFragment(baseFragment);
                                baseFragment.replaceFragment(troubleshootingFragment);
                                break;
                            case "Send Feedback":
                                baseFragment.replaceFragment(SendFeedbackFragment.newInstance());
                                break;
                            case "Contact Us":
                                ContactUsFragment contactUsFragment = createContactUsFragment(baseFragment);

                                baseFragment.replaceFragment(contactUsFragment);
                                break;
                            case "Update App":
                                //TODO handle Update App
                                break;
                        }
                    }
                });

        return listFragment;
    }

    static ContactUsFragment createContactUsFragment(SettingsBaseFragment baseFragment) {
        return new ContactUsFragment(baseFragment);
    }

    static SettingsListFragment createTroubleshootingFragment(final SettingsBaseFragment baseFragment) {
        SettingsListFragment troubleshootingFragment = new SettingsListFragment();
        List<SettingsListItem> troubleshootingList = new ArrayList<>();

        troubleshootingList.add(new SettingsListItem("Can't connect with sleeping tracker"));
        troubleshootingList.add(new SettingsListItem("Mattress Pump issue"));
        troubleshootingList.add(new SettingsListItem("Base connection issue"));
        troubleshootingList.add(new SettingsListItem("Another issue"));
        troubleshootingList.add(new SettingsListItem("Another issue 2"));

        troubleshootingFragment.configure(
                baseFragment,
                R.string.settings_troubleshooting_title,
                R.layout.fragment_settings,
                R.id.recyclerView,
                R.layout.item_settings,
                troubleshootingList,
                new SettingsListFragment.SettingsAdapterOnItemClickListener(){

                    @Override
                    public void onItemClick(String buttonTitle, int position) {
                        //TODO handle troubleshooting item clicklistener
                    }
                }
        );
        return troubleshootingFragment;
    }

    static SettingsListFragment createSendFeedbackFragment(final SettingsBaseFragment baseFragment) {
        SettingsListFragment sendFeedbackFragment = new SettingsListFragment();
        List<SettingsListItem> sendFeedbackList = new ArrayList<>();

        sendFeedbackList.add(new SettingsListItem("Send Feedback"));
        sendFeedbackList.add(new SettingsListItem("Report Bug"));
        sendFeedbackList.add(new SettingsListItem("Request Help"));

        sendFeedbackFragment.configure(
                baseFragment,
                R.string.settings_send_feedback_title,                        /* Fragment Title   */
                R.layout.fragment_settings,             /* Fragment Layout  */
                R.id.recyclerView,                      /* RecyclerView     */
                R.layout.item_settings,                 /* Item TextView    */
                sendFeedbackList,
                new SettingsListFragment.SettingsAdapterOnItemClickListener(){

                    @Override
                    public void onItemClick(String buttonTitle, int position) {
                        //TODO handle sendFeedback item clicklistener
                    }
                }
        );
        return sendFeedbackFragment;
    }

    static DeviceListFragment createDeviceFragment(){
        final DeviceListFragment deviceFragment = new DeviceListFragment();

        deviceFragment.setDeviceOnClickListener(
                new DeviceListFragment.DeviceAdapterOnItemClickListener() {
                    @Override
                    public void onItemClick(View view) {
                        /**
                         * TODO: 9/03/2017
                         * add logic to set up device
                         */
                        if (view.getId() == R.id.device_reset_layout) {
                            AnalyticsService.instance().logPreferencesResetApp();
                            deviceFragment.resetDevices(view);
                        }else{
                            Button setUpButton = (Button) view.findViewById(R.id.set_up_device_button);
                            String setUpButtonText = setUpButton.getText().toString();
                            if(setUpButton != null && setUpButtonText.contains("base")) {
                                new AlertDialog.Builder(view.getContext()).setTitle("Devices").setMessage("Set up Adjustable Base clicked").show();
                            }else if(setUpButton != null && setUpButtonText.contains("tracker")){
                                new AlertDialog.Builder(view.getContext()).setTitle("Devices").setMessage("Set up Sleep Tracker clicked").show();
                            }
                        }
                    }
                }
        );
        return deviceFragment;
    }

    static SettingsListFragment createSettingsFragment(final SettingsBaseFragment baseFragment) {
        ArrayList<SettingsListItem> settingsList = new ArrayList<>();
        final SettingsListFragment frag = new SettingsListFragment();

        settingsList.add(new SettingsListItem("My Devices"));
        settingsList.add(new SettingsListItem("My Profile"));
        settingsList.add(new SettingsListItem("Support"));
        settingsList.add(new SettingsListItem("Six Week Sleep Challenge"));
        settingsList.add(new SettingsListItem("Privacy Policy"));
        settingsList.add(new SettingsListItem("Terms of Service"));
        settingsList.add(new SettingsListItem("SleepSense " + BuildConfig.VERSION_NAME, true));


        frag.configure(
                baseFragment,
                R.string.settings_more_title,                         /* Fragment Title   */
                R.layout.fragment_settings,     /* Fragment Layout  */
                R.id.recyclerView,              /* RecyclerView     */
                R.layout.item_settings,         /* Item TextView    */
                settingsList,
                new SettingsListFragment.SettingsAdapterOnItemClickListener() {
                    @Override
                    public void onItemClick(String s, int position) {
                        switch(s) {
                            case "My Devices":
                                DeviceListFragment deviceFragment = createDeviceFragment();
                                baseFragment.replaceFragment(deviceFragment);
                                break;
                            case "My Profile":
                                Fragment myProfileFrag = createMyProfileFragment(baseFragment);
                                baseFragment.replaceFragment(myProfileFrag);
                                break;
                            case "Support":
                                SettingsListFragment supportFragment = createSupportFragment(baseFragment);
                                baseFragment.replaceFragment(supportFragment);
                                break;
                            case "Six Week Sleep Challenge":
                            case "Privacy Policy":
                            case "Terms of Service":
                                baseFragment.replaceFragment(createWebViewFragment(s));
                                break;
                        }
                    }
                });

        return frag;
    }

    static CustomerInformationFragment createWebViewFragment(String buttonTitle) {
        CustomerInformationFragment customerInformationFragment = new CustomerInformationFragment();

        switch(buttonTitle) {
            case "Privacy Policy":
                customerInformationFragment.configure("http://www.ahbeard.com.au/privacypolicy");
                break;
            case "Terms of Service":
                customerInformationFragment.configure("https://sleepsense.com.au/terms-of-service");
                break;
            case "Six Week Sleep Challenge":
                customerInformationFragment.configure("http://www.ahbeard.com.au/sleepchallenge");
        }

        return customerInformationFragment;
    }
}
