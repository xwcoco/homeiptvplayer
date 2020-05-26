package com.forcetech.android;

public class ForceTV {
    public native int start(int port, int mem);

    public void start(String libName, int port)
    {
        System.loadLibrary(libName);
        try
        {
            start(port, 20971520);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public native int stop();
}
