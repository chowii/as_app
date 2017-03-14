package au.com.ahbeard.sleepsense.fragments.settings;

/**
 * Created by Sabbib on 27/02/2017.
 */
public class SettingsListItem {

    private String title;
    private String head;
    private String subHead1;

    private boolean isTextRow;

    public SettingsListItem(String head) {
        this.head = head;
        this.isTextRow = false;
    }

    public SettingsListItem(String head, boolean isTextRow) {
        this.head = head;
        this.isTextRow = isTextRow;
    }

    public String getHead() { return head; }
    public String getSubHead() { return subHead1; }

    public String getTitle() { return title; }

    public boolean isTextRow() { return isTextRow; }
}