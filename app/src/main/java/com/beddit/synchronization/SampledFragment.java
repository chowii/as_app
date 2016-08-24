package com.beddit.synchronization;

import com.beddit.synchronization.SampledTrackDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SampledFragment {
    private final int startFrameIndex;
    private final int frameCount;
    private final double frameLength;
    private final Map trackDatas;

    public SampledFragment(Map dataMap, int startFrameIndex, int frameCount, double frameLength) {
        this.startFrameIndex = startFrameIndex;
        this.frameCount = frameCount;
        this.frameLength = frameLength;
        this.trackDatas = new HashMap();
        Iterator startFrameIndex1 = dataMap.entrySet().iterator();

        while(startFrameIndex1.hasNext()) {
            Map.Entry dataMap1 = (Map.Entry)startFrameIndex1.next();
            a dataMap2 = new a(this, (SampledTrackDescriptor)dataMap1.getKey(), (byte[])dataMap1.getValue());
            this.trackDatas.put(dataMap2.a.getTrackName(), dataMap2);
            if(dataMap2.b.length != dataMap2.a.getBytesPerFrame() * frameCount) {
                throw new IllegalArgumentException("Data size does not match specification");
            }
        }

    }

    public int getFrameCount() {
        return this.frameCount;
    }

    public int getStartFrameIndex() {
        return this.startFrameIndex;
    }

    public double getFrameLength() {
        return this.frameLength;
    }

    public int getTrackCount() {
        return this.trackDatas.size();
    }

    public List getTrackNames() {
        return new ArrayList(this.trackDatas.keySet());
    }

    public List getTrackDescriptors() {
        ArrayList var1 = new ArrayList(this.trackDatas.size());
        Iterator var3 = this.trackDatas.values().iterator();

        while(var3.hasNext()) {
            a var2 = (a)var3.next();
            var1.add(var2.a);
        }

        return var1;
    }

    public byte[] getTrackData(String trackName) {
        a trackName1;
        return (trackName1 = (a)this.trackDatas.get(trackName)) != null?trackName1.b:null;
    }

    public SampledTrackDescriptor getTrackDescriptor(String trackName) {
        a trackName1;
        return (trackName1 = (a)this.trackDatas.get(trackName)) != null?trackName1.a:null;
    }
}

