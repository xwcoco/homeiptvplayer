package com.dfsoft.iptvplayer.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class IPTVEPG {
    public int code = 0;
    public String msg = "";
    public String name = "";
    public int tvid = 0;
    public String date = "";

    public List<IPTVEpgData> data = new ArrayList<>();

    public Boolean isEmpty() {
        return code != 200;
    }

    public int curTime = -1;

    public void getCurrentTimer() {
        this.curTime = -1;
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        for (int i = 0; i < this.data.size(); i++) {
            String starttime = data.get(i).starttime;
            int pos = starttime.indexOf(':');
            if (pos == -1)
                continue;
            int hour = Integer.parseInt(starttime.substring(0,pos));
            int minute = Integer.parseInt(starttime.substring(pos+1));

//            Log.d(TAG, "getCurrentTimer: "+hour + " m = "+minute);

            Calendar d = Calendar.getInstance();
            d.set(Calendar.HOUR_OF_DAY,hour);
            d.set(Calendar.MINUTE,minute);
            d.set(Calendar.SECOND,0);

            Date date1 = d.getTime();
            if (date1.after(date)) {
                break;
            }
            this.curTime = i;
        }
    }
}
