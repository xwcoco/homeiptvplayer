package com.forcetech.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.dfsoft.iptvplayer.utils.LogUtils;
import com.forcetech.android.ForceTV;

public class P8PService extends Service {
    private ForceTV forceTV;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        this.forceTV = new ForceTV();
        this.forceTV.start("forcetv", 9002);
        LogUtils.i("P2PManager","bind....");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (forceTV != null)
            this.forceTV.stop();
        return super.onUnbind(intent);
    }
}
