package com.beddit.synchronization;

import com.beddit.synchronization.SampledTrackDescriptor;

public final class g {
    public final SampledTrackDescriptor a;
    public final d b;
    public final double c;
    public int d;
    public double e;

    public g(h var1, SampledTrackDescriptor var2, double var3) {
        this.c = (double)var2.getSamplesPerFrame() / var3;
        this.a = var2;
        double var10001 = this.c;
        this.a.getTrackName();
        double var5 = var10001;
        this.b = new d(var5);
        this.d = 0;
        this.e = 0.0D;
    }

    public final int a(byte[] var1) {
        Integer var2 = Integer.valueOf(this.a.getBytesPerSample());
        if(var1.length % var2.intValue() != 0) {
            throw new IllegalStateException("Track data does not divide evenly with bytes per sample");
        } else {
            return var1.length / var2.intValue();
        }
    }

    public double a() {
        d var1 = this.b;
        return this.b.a;
    }

    public static byte[] a(byte[] var0, int var1, int var2) {
        if(var2 == 0) {
            throw new IllegalArgumentException("Correction can not be zero");
        } else if(var0.length % var1 != 0) {
            throw new IllegalArgumentException("Data length must be multiple of sampleSize");
        } else if(Math.abs(var2) > var0.length / var1) {
            throw new IllegalArgumentException("Can not add or remove more than the original number of samples");
        } else {
            byte[] var3 = new byte[var0.length + var2 * var1];
            boolean var4 = var2 > 0;
            var2 = Math.abs(var2);
            int var5 = var0.length / var1 / var2;
            int var6;
            int var7;
            int var8;
            if(var4) {
                var8 = 0;
                var6 = 0;

                for(var7 = 0; var7 < var2; ++var7) {
                    System.arraycopy(var0, var8, var3, var6, var1 * var5);
                    var8 += var1 * var5;
                    var6 += var1 * (var5 + 1);
                    System.arraycopy(var0, var8 - var1, var3, var6 - var1, var1);
                }

                if(var8 < var0.length) {
                    System.arraycopy(var0, var8, var3, var6, var0.length - var8);
                }
            } else {
                var8 = 0;
                var6 = 0;

                for(var7 = 0; var7 < var2; ++var7) {
                    System.arraycopy(var0, var8, var3, var6, var1 * (var5 - 1));
                    var8 += var1 * var5;
                    var6 += var1 * (var5 - 1);
                }

                if(var8 < var0.length) {
                    System.arraycopy(var0, var8, var3, var6, var0.length - var8);
                }
            }

            return var3;
        }
    }
}
