package com.beddit.synchronization;

import java.nio.ByteBuffer;
import java.util.List;

public class Synchronizer {
    private final h a;
    private final b b;

    public Synchronizer(List trackDescriptors, float frameLength, String referenceTrackName, int bufferSizeInSamples) {
        this.a = new h(trackDescriptors, referenceTrackName, (double)frameLength);
        this.b = new b(frameLength, trackDescriptors);
    }

    public SampledFragment appendData(byte[] data, String trackName, double receivedAt) throws SynchronizationException {
        byte[] var4 = data;
        h data1 = this.a;
        g var5;
        int var6 = (var5 = (g)this.a.c.get(trackName)).a(var4);
        var5.d += var6;
        int var22 = var5.d;
        d var7 = var5.b;
        e var23;
        if(receivedAt < 10.0D) {
            var23 = var7.c;
            var7.c.a.clear();
        }

        var23 = var7.c;
        if(!var7.c.a.isEmpty()) {
            f var27 = (f)var23.a.get(var23.a.size() - 1);
            if(receivedAt <= var27.a) {
                throw new IllegalArgumentException("Timestamps must be strictly increasing");
            }

            if(var22 <= var27.b) {
                throw new IllegalArgumentException("Sample counts must be strictly increasing");
            }
        }

        var23.a.add(new f(var23, receivedAt, var22));
        double var26;
        if(receivedAt >= var7.b) {
            var7.b = receivedAt + 10.0D;
            e var41 = var7.c;
            int var24;
            if((var24 = var7.c.a.size() / 10) >= 10) {
                int var25 = Math.min(var24, 500);
                var26 = var7.c.a(var25);
                double var28 = var7.c.b(var25);
                double var30 = var7.c.c(var25);
                double var32 = var7.c.d(var25);
                var7.a = (var32 - var30) / (var28 - var26);
            }
        }

        byte[] var10000;
        if(trackName.equals(data1.a)) {
            data1.b = var5.a() / var5.c;
            var10000 = var4;
        } else {
            double var17 = data1.b;
            int var42 = var5.a(var4);
            double var20 = var5.c * var17;
            double var35 = var5.a();
            int var39 = (int)Math.round(var26 = (double)var42 * (var20 / var35) + var5.e - (double)var42);
            double var29 = var26 - (double)var39;
            var5.e = var29;
            if(var39 == 0) {
                var10000 = var4;
            } else {
                int var31 = var5.a.getBytesPerSample();
                var10000 = g.a(var4, var31, var39);
            }
        }

        data = var10000;

        try {
            var4 = data;
            b data2 = this.b;
            b var10001 = this.b;
            ByteBuffer var40;
            if((var40 = (ByteBuffer)this.b.a.get(trackName)) == null) {
                throw new IllegalArgumentException("Unknown track name given: " + trackName);
            } else if(var4.length > var40.remaining()) {
                throw new c(16384);
            } else {
                var40.put(var4);
                SampledFragment data3 = data2.a();
                return data3;
            }
        } catch (c var34) {
            throw new SynchronizationException(var34.getMessage());
        }
    }
}
