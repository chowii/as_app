package au.com.ahbeard.sleepsense.fragments.settings;

/**
 * Created by sabbib on 9/03/2017.
 */

class DeviceListItem {

    private boolean isConnected;
    private String title;
    private String head;
    private String subHead;
    private boolean isButton;


    public DeviceListItem(String title, String head, String subHead, boolean isConnected, boolean isButton) {
        this.title = title;
        this.head = head;
        this.subHead = subHead;
        this.isConnected = isConnected;
        this.isButton = isButton;
    }

    public boolean isButton() {return isButton; }

    public String getTitle() { return title; }

    public String getHead() { return head; }

    public String getSubHead() { return subHead; }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean isConnected) { this.isConnected = isConnected; }

    public interface DeviceAdapterOnItemOnClickListener {
    }
}
