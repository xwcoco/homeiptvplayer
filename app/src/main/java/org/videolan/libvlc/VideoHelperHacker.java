package org.videolan.libvlc;

import com.dfsoft.iptvplayer.utils.LogUtils;

import org.videolan.libvlc.VideoHelper;
import org.videolan.libvlc.interfaces.IVLCVout;
import org.videolan.libvlc.util.DisplayManager;
import org.videolan.libvlc.util.VLCVideoLayout;

public class VideoHelperHacker extends VideoHelper {
    public VideoHelperHacker(MediaPlayer player, VLCVideoLayout surfaceFrame, DisplayManager dm, boolean subtitles, boolean textureView) {
        super(player, surfaceFrame, dm, subtitles, textureView);
    }

    @Override
    public void onNewVideoLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
        super.onNewVideoLayout(vlcVout, width, height, visibleWidth, visibleHeight, sarNum, sarDen);
        LogUtils.i("VideoHelperHacker","width = "+ width);
        LogUtils.i("VideoHelperHacker","height = "+ height);
    }

    @Override
    public void attachViews() {
        super.attachViews();
    }
}
