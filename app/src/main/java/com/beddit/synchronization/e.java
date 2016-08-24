package com.beddit.synchronization;

import java.util.ArrayList;
import java.util.List;

public final class e {
    public List a = new ArrayList();

    e() {
    }

    public final double a(int var1) {
        if(this.a.size() < var1) {
            throw new IllegalStateException("There are too few datapoints");
        } else {
            double[] var2 = new double[var1];

            for(int var3 = 0; var3 < var1; ++var3) {
                var2[var3] = ((f)this.a.get(var3)).a;
            }

            return a(var2);
        }
    }

    public final double b(int var1) {
        if(this.a.size() < var1) {
            throw new IllegalStateException("There are too few datapoints");
        } else {
            double[] var2 = new double[var1];
            int var3 = this.a.size() - var1;

            for(int var4 = 0; var4 < var1; ++var4) {
                var2[var4] = ((f)this.a.get(var3 + var4)).a;
            }

            return a(var2);
        }
    }

    public final double c(int var1) {
        if(this.a.size() < var1) {
            throw new IllegalStateException("There are too few datapoints");
        } else {
            double[] var2 = new double[var1];

            for(int var3 = 0; var3 < var1; ++var3) {
                var2[var3] = (double)((f)this.a.get(var3)).b;
            }

            return a(var2);
        }
    }

    public final double d(int var1) {
        if(this.a.size() < var1) {
            throw new IllegalStateException("There are too few datapoints");
        } else {
            double[] var2 = new double[var1];
            int var3 = this.a.size() - var1;

            for(int var4 = 0; var4 < var1; ++var4) {
                var2[var4] = (double)((f)this.a.get(var3 + var4)).b;
            }

            return a(var2);
        }
    }

    static double a(double[] var0) {
        double var1 = 0.0D;

        for(int var3 = 0; var3 < var0.length; ++var3) {
            var1 += var0[var3];
        }

        return var1 / (double)var0.length;
    }
}