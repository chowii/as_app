package au.com.ahbeard.sleepsense.fragments.settings;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import au.com.ahbeard.sleepsense.R;
import au.com.ahbeard.sleepsense.services.log.SSLog;

/**
 * Created by sabbib on 6/03/2017.
 */
class SettingsFragmentFactory {

    static SettingsListFragment createMyProfileFragment(SettingsBaseFragment baseFragment){
        List<SettingsListItem> profileList = new ArrayList<>();

        profileList.add(new SettingsListItem("Sleep Target"));
        profileList.add(new SettingsListItem("Weight"));
        profileList.add(new SettingsListItem("Height"));
        profileList.add(new SettingsListItem("Age"));
        profileList.add(new SettingsListItem("Gender"));

        SettingsListFragment frag = new SettingsListFragment();


        frag.configure(baseFragment,
                R.string.settings_profile_title, /* Fragment Title  */
                R.layout.fragment_my_profile,    /* Fragment Layout */
                R.id.my_profile_txt,             /* RecyclerView    */
                R.layout.item_my_profile,        /* Item TextView   */
                profileList,
                new SettingsListFragment.SettingsAdapterOnItemClickListener() {
                @Override
                public void onItemClick(String buttonTitle, int position) {
                    //TODO complete profile item clicklistener
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
                R.id.settings_txt,                  /* RecyclerView    */
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
                                SettingsListFragment sendFeedbackFragment = createSendFeedbackFragment(baseFragment);
                                baseFragment.replaceFragment(sendFeedbackFragment);
                                break;
                            case "Contact Us":
//                                SettingsListFragment contactUsFragment = createContactUsFragment(baseFragment);
//                                baseFragment.replaceFragment(contactUsFragment);
                                break;
                            case "Update App":
                                //TODO handle Update App
                                break;
                        }
                    }
                });

        return listFragment;
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
                R.id.settings_txt,
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
                R.string.settings_send_feedback_title,  /* Fragment Title   */
                R.layout.fragment_settings,             /* Fragment Layout  */
                R.id.settings_txt,                      /* RecyclerView     */
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

    static SettingsListFragment createContactUsFragment(final SettingsBaseFragment baseFragment) {
        SettingsListFragment contactUsFragment = new SettingsListFragment();
        //TODO handle Contact Us
        return contactUsFragment;
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

        frag.configure(
                baseFragment,
                R.string.settings_more_title,   /* Fragment Title   */
                R.layout.fragment_settings,     /* Fragment Layout  */
                R.id.settings_txt,              /* RecyclerView     */
                R.layout.item_settings,         /* Item TextView    */
                settingsList,
                new SettingsListFragment.SettingsAdapterOnItemClickListener() {
            @Override
            public void onItemClick(String s, int position) {
                switch(s) {
                    case "My Devices":
//                        act.getSupportFragmentManager().beginTransaction().replace(R.id.container, new DeviceFragments()).commit();
                        break;
                    case "My Profile":
                        SettingsListFragment myProfileFrag = createMyProfileFragment(baseFragment);
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

    public static AssistFragment createWebViewFragment(String buttonTitle) {
        AssistFragment frag = new AssistFragment();

        switch(buttonTitle) {
            case "Privacy Policy":
                frag.configure("http://www.ahbeard.com.au/privacypolicy");
                break;
            case "Terms of Service":
                frag.configure("https://sleepsense.com.au/terms-of-service");
                break;
            case "Six Week Sleep Challenge":
                frag.configure("http://www.ahbeard.com.au/sleepchallenge");
        }

        return frag;
    }



}
