package au.com.ahbeard.sleepsense.model.beddit;

/**
 * Created by neal on 9/05/2016.
 */
public class TrackData {

    private String mName;
    private String mType;
    private byte[] mData;

    public TrackData(String name, String type, byte[] data) {
        mName = name;
        mType = type;
        mData = data;
    }

    public String getName() {
        return mName;
    }

    public String getType() {
        return mType;
    }

    public byte[] getData() {
        return mData;
    }
}
