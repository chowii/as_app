package com.beddit.synchronization;

public class SampledTrackDescriptor {
    private final String name;
    private final String deviceVersion;
    private final String sampleType;
    private final int samplesPerFrame;

    public SampledTrackDescriptor(String name, String deviceVersion, String sampleType, int samplesPerFrame) {
        this.name = name;
        this.deviceVersion = deviceVersion;
        this.sampleType = sampleType;
        this.samplesPerFrame = samplesPerFrame;
    }

    public String getTrackName() {
        return this.name;
    }

    public String getDeviceVersion() {
        return this.deviceVersion;
    }

    public String getSampleType() {
        return this.sampleType;
    }

    public int getSamplesPerFrame() {
        return this.samplesPerFrame;
    }

    public int getBytesPerFrame() {
        return this.getBytesPerSample() * this.samplesPerFrame;
    }

    public int getBytesPerSample() {
        return i.a(this.sampleType).a;
    }
}
