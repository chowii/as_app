package au.com.ahbeard.sleepsense.fragments.settings;

/**
 * Created by Sabbib on 27/02/2017.
 */
public class SettingsList {

    private String head;
    private String desc;

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public SettingsList(String head, String desc) {
        this.head = head;
        this.desc = desc;
    }

    public SettingsList() {}

    public String getHead() {
        return head;
    }

    public String getDesc() {
        return desc;
    }




}