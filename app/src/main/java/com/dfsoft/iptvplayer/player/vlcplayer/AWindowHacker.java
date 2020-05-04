package com.dfsoft.iptvplayer.player.vlcplayer;

import android.util.Log;
import android.view.Surface;

import org.videolan.libvlc.AWindow;
import org.videolan.libvlc.interfaces.IVLCVout;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class AWindowHacker {
    /**
     * Create an AWindow
     * <p>
     * You call this directly only if you use the libvlc_media_player native API (and not the Java
     * MediaPlayer class).
     *
     * @param surfaceCallback
     */
    private AWindow mWindow;

    private Class mHackerClass;


    public AWindowHacker(AWindow aWindow) {
        mWindow = aWindow;

        mHackerClass = aWindow.getClass();
    }

    public void attachSurfaceSlave(Surface videoSurface, Surface subtitlesSurface, IVLCVout.OnNewVideoLayoutListener mOnNewVideoLayoutListener) {
        setNativeSurface(0, videoSurface);
        setNativeSurface(1, subtitlesSurface);
        mWindow.attachViews(mOnNewVideoLayoutListener);
        surfaceReady();
    }

    private final static int SURFACE_STATE_INIT = 0;
    private final static int SURFACE_STATE_ATTACHED = 1;
    private final static int SURFACE_STATE_READY = 2;

    private void setPrivateFieldValue(String fieldName,int fieldValue) {
        try {
        Field myField = mHackerClass.getDeclaredField(fieldName);
            myField.setAccessible(true);
            myField.set(mWindow, fieldValue);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private void AtomicIntegerSet(String fieldName,int FieldValue) {
        try {
            Field field = mHackerClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            AtomicInteger value = new AtomicInteger();
            value.set(FieldValue);
            field.set(mWindow,value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void doOnSurfacesCreated() {
        try {
            Field field = mHackerClass.getDeclaredField("mIVLCVoutCallbacks");
            field.setAccessible(true);
            ArrayList<IVLCVout.Callback> values = new ArrayList<>();
            values = (ArrayList<IVLCVout.Callback>) field.get(values);
            for (IVLCVout.Callback cb : values) {
                cb.onSurfacesCreated(this.mWindow);
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private void surfaceReady() {
//        mSurfacesState.set(SURFACE_STATE_READY);
        AtomicIntegerSet("mSurfacesState",SURFACE_STATE_READY);
        doOnSurfacesCreated();
//        for (IVLCVout.Callback cb : mIVLCVoutCallbacks)
//            cb.onSurfacesCreated(this);
//        if (mSurfaceCallback != null)
//            mSurfaceCallback.onSurfacesCreated(this);
    }

    private void setNativeSurface(int id, Surface surface)  {
        try {
            Method method = mHackerClass.getDeclaredMethod("setNativeSurface", int.class, Surface.class);
            method.setAccessible(true);
            method.invoke(mWindow, id,surface);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void printprivatevalue() {
        Class t = mWindow.getClass();

        Field[] fields = t.getDeclaredFields();
        for(Field field : fields){
            System.out.println(field);
            Log.d("AWindowHacker", "printprivatevalue: "+field);
        }
    }
}
