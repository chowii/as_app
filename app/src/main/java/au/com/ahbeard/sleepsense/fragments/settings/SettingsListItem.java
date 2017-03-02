package au.com.ahbeard.sleepsense.fragments.settings;

/**
 * Created by Sabbib on 27/02/2017.
 */
public class SettingsListItem {

    private String head;
    private String desc;


    public SettingsListItem(String head, String desc) {
        this.head = head;
        this.desc = desc;
    }

    public SettingsListItem() {}

    public String getHead() {
        return head;
    }

    public String getDesc() {
        return desc;
    }




}