package com.beddit.synchronization;

import java.util.TreeMap;

public abstract class i {
    private static TreeMap b;
    public final int a;

    static {
        (b = new TreeMap()).put("int8", new n());
        b.put("uint8", new q());
        b.put("int16", new l());
        b.put("uint16", new o());
        b.put("int32", new m());
        b.put("uint32", new p());
        b.put("float32", new j());
        b.put("float64", new k());
    }

    public static i a(String var0) {
        i var1;
        if((var1 = (i)b.get(var0)) == null) {
            throw new IllegalArgumentException("Unsupported data type: " + var0);
        } else {
            return var1;
        }
    }

    public i(String var1, int var2) {
        this.a = var2;
    }
    public i(String var1, int var2, byte var3) { this.a = var2; }
}

