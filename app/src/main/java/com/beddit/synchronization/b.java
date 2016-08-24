package com.beddit.synchronization;

import com.beddit.synchronization.SampledFragment;
import com.beddit.synchronization.SampledTrackDescriptor;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class b {
    private final float b;
    private final List c;
    public final Map a;
    private int d;

    public b(float var1, List var2) {
        this.b = var1;
        this.c = var2;
        this.d = 0;
        this.a = new HashMap();
        Iterator var5 = this.c.iterator();

        while(var5.hasNext()) {
            SampledTrackDescriptor var4;
            if((var4 = (SampledTrackDescriptor)var5.next()).getBytesPerFrame() == 0) {
                throw new IllegalArgumentException("Track Descriptor has zero length frame: " + var4.getTrackName());
            }

            ByteBuffer var3;
            (var3 = ByteBuffer.allocate(16384)).order(ByteOrder.nativeOrder());
            if((ByteBuffer)this.a.put(var4.getTrackName(), var3) != null) {
                throw new IllegalArgumentException("Tracks with duplicate names: " + var4.getTrackName());
            }
        }

    }

    public SampledFragment a() {
        b var1 = this;
        int var2 = 2147483647;
        Iterator var4 = this.c.iterator();

        SampledTrackDescriptor var3;
        int var10000;
        while(true) {
            if(!var4.hasNext()) {
                var10000 = var2;
                break;
            }

            var3 = (SampledTrackDescriptor)var4.next();
            int var12;
            if((var12 = ((ByteBuffer)var1.a.get(var3.getTrackName())).position() / var3.getBytesPerFrame()) == 0) {
                var10000 = 0;
                break;
            }

            var2 = Math.min(var12, var2);
        }

        int var10 = var10000;
        if(var10000 == 0) {
            return null;
        } else {
            HashMap var11 = new HashMap(this.c.size());
            var4 = this.c.iterator();

            while(var4.hasNext()) {
                String var5 = (var3 = (SampledTrackDescriptor)var4.next()).getTrackName();
                ByteBuffer var6 = (ByteBuffer)this.a.get(var5);
                int var7 = var10 * var3.getBytesPerFrame();
                byte[] var8 = Arrays.copyOfRange(var6.array(), 0, var7);
                byte[] var14 = Arrays.copyOfRange(var6.array(), var7, var6.position());
                ByteBuffer var9;
                (var9 = ByteBuffer.allocate(var6.capacity())).order(var6.order());
                var9.put(var14);
                this.a.put(var5, var9);
                var11.put(var3, var8);
            }

            SampledFragment var13 = new SampledFragment(var11, this.d, var10, (double)this.b);
            this.d += var10;
            return var13;
        }
    }
}
