package com.beddit.synchronization;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class h {
    public String a;
    public double b;
    public Map c = new HashMap();

    public h(List var1, String var2, double var3) {
        Iterator var5 = var1.iterator();

        while(var5.hasNext()) {
            SampledTrackDescriptor var7;
            String var6 = (var7 = (SampledTrackDescriptor)var5.next()).getTrackName();
            g var8 = new g(this, var7, var3);
            this.c.put(var6, var8);
        }

        if(var2 == null) {
            throw new IllegalArgumentException("Reference track name is null");
        } else if(this.c.get(var2) == null) {
            throw new IllegalArgumentException("Reference track is not included in given track descriptors");
        } else {
            this.a = var2;
            this.b = 1.0D;
        }
    }
}
