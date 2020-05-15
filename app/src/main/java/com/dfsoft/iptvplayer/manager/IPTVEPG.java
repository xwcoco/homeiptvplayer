package com.dfsoft.iptvplayer.manager;

import android.nfc.Tag;
import android.util.Log;

import com.dfsoft.iptvplayer.utils.LogUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.security.auth.callback.Callback;

public class IPTVEPG {
    public int code = 0;
    public String msg = "";
    public String name = "";
    public int tvid = 0;
    public String date = "";

    public ArrayList<IPTVEpgData> data = new ArrayList<>();

    public Boolean isEmpty() {
        return code != 200;
    }

    public int curTime = -1;

    public int getEPGTimeIndex(ArrayList<IPTVEpgData> list) {
        int index = -1;
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        for (int i = 0; i < list.size(); i++) {
            String starttime = list.get(i).starttime;
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
            index = i;
        }
        return index;
    }

    public void getCurrentTimer() {
        this.curTime = this.getEPGTimeIndex(this.data);
    }

    public int getCurrenPercent() {
        if (this.curTime == -1 || this.curTime + 1 == data.size()) return 50;
        String starttime = data.get(this.curTime).starttime;
        String nextTime = data.get(this.curTime+1).starttime;

        int pos = starttime.indexOf(':');
        if (pos == -1)
            return 0;
        int hour = Integer.parseInt(starttime.substring(0,pos));
        int minute = Integer.parseInt(starttime.substring(pos+1));

        pos = nextTime.indexOf(':');
        if (pos == -1) return 0;

        Calendar d = Calendar.getInstance();
        d.set(Calendar.HOUR_OF_DAY,hour);
        d.set(Calendar.MINUTE,minute);
        d.set(Calendar.SECOND,0);

        hour = Integer.parseInt(nextTime.substring(0,pos));
        minute = Integer.parseInt(nextTime.substring(pos+1));

        Calendar e = Calendar.getInstance();
        e.set(Calendar.HOUR_OF_DAY,hour);
        e.set(Calendar.MINUTE,minute);
        e.set(Calendar.SECOND,0);

        Calendar n = Calendar.getInstance();

        long ld = d.getTimeInMillis();
        long le = e.getTimeInMillis();
        long ln = n.getTimeInMillis();

//        LogUtils.i("IPTVEPG"," start = "+
//                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS", Locale.CHINA).format(ld));
//        LogUtils.i("IPTVEPG"," end = "+
//                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS", Locale.CHINA).format(le));
//
//        LogUtils.i("IPTVEPG"," now = "+
//                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS", Locale.CHINA).format(ln));
        if (le - ld == 0) return 0;
        double ee =(double)(ln - ld) / (double)(le-ld);
        return (int) Math.round(ee * 100);

    }

    public double getProgramMinutes(int index) {
        String starttime = starttime = data.get(index).starttime;;
        String nextTime = "";
        if (index != data.size() - 1) {
            nextTime = data.get(index+1).starttime;
        }
        int pos = starttime.indexOf(':');
        if (pos == -1)
            return 0;
        int hour = Integer.parseInt(starttime.substring(0,pos));
        int minute = Integer.parseInt(starttime.substring(pos+1));

        Calendar d = Calendar.getInstance();
        d.set(Calendar.HOUR_OF_DAY,hour);
        d.set(Calendar.MINUTE,minute);
        d.set(Calendar.SECOND,0);

        Calendar e = Calendar.getInstance();

        if (nextTime.isEmpty()) {
//            e.set(Calendar.DAY_OF_MONTH,e.get(Calendar.DAY_OF_MONTH)+1);
            e.add(Calendar.DATE,1);
            e.set(Calendar.HOUR_OF_DAY,0);
            e.set(Calendar.MINUTE,0);
            e.set(Calendar.SECOND,0);

        } else {
            pos = nextTime.indexOf(':');
            if (pos == -1) return 0;

            hour = Integer.parseInt(nextTime.substring(0,pos));
            minute = Integer.parseInt(nextTime.substring(pos+1));

            e.set(Calendar.HOUR_OF_DAY,hour);
            e.set(Calendar.MINUTE,minute);
            e.set(Calendar.SECOND,0);
        }

        long ld = d.getTimeInMillis();
        long le = e.getTimeInMillis();

        return (double)(le - ld) / 1000 / 60;
    }

    public ArrayList<IPTVEpgData> getDataInHours(int begin,int end) {
        ArrayList<IPTVEpgData> tmpDatas = new ArrayList<>();

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(Calendar.HOUR_OF_DAY,begin);
        beginTime.set(Calendar.MINUTE,0);
        beginTime.set(Calendar.SECOND,0);

        Calendar endTime = Calendar.getInstance();
        endTime.set(Calendar.HOUR_OF_DAY,end);
        endTime.set(Calendar.MINUTE,0);
        endTime.set(Calendar.SECOND,0);

        for (int i = 0; i < this.data.size(); i++) {
            IPTVEpgData epg = this.data.get(i);

            String starttime = epg.starttime;

            int pos = starttime.indexOf(':');
            if (pos == -1)
                continue;

            int hour = Integer.parseInt(starttime.substring(0,pos));
            int minute = Integer.parseInt(starttime.substring(pos+1));

            Calendar d = Calendar.getInstance();
            d.set(Calendar.HOUR_OF_DAY,hour);
            d.set(Calendar.MINUTE,minute);
            d.set(Calendar.SECOND,0);

            String nextTime = "";
            if (i != data.size() - 1) {
                nextTime = data.get(i+1).starttime;
            }

            Calendar e = Calendar.getInstance();
            if (nextTime.isEmpty()) {
//            e.set(Calendar.DAY_OF_MONTH,e.get(Calendar.DAY_OF_MONTH)+1);
                e.add(Calendar.DATE,1);
                e.set(Calendar.HOUR_OF_DAY,0);
                e.set(Calendar.MINUTE,0);
                e.set(Calendar.SECOND,0);

            } else {
                pos = nextTime.indexOf(':');
                if (pos == -1) continue;

                hour = Integer.parseInt(nextTime.substring(0,pos));
                minute = Integer.parseInt(nextTime.substring(pos+1));

                e.set(Calendar.HOUR_OF_DAY,hour);
                e.set(Calendar.MINUTE,minute);
                e.set(Calendar.SECOND,0);
            }

//            Calendar now = Calendar.getInstance();

            if (d.compareTo(endTime) < 0) {
                if (d.compareTo(beginTime) >= 0 || e.compareTo(beginTime) >= 0) {
                    if (d.compareTo(beginTime) < 0) {
                        epg.minutes = (int) ((e.getTimeInMillis() - beginTime.getTimeInMillis()) / 1000 / 60);
                    } else
                        epg.minutes = (int) ((e.getTimeInMillis() - d.getTimeInMillis()) / 1000 / 60);

                    tmpDatas.add(epg);
                }
            }

//            if ((d.compareTo(beginTime) >= 0 || d.compareTo(endTime) <= 0) {
//
//            }

        }

        return  tmpDatas;
    }


}
