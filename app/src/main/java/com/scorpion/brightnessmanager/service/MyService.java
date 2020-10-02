package com.scorpion.brightnessmanager.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scorpion.brightnessmanager.BuildConfig;
import com.scorpion.brightnessmanager.R;
import com.scorpion.brightnessmanager.model.BrightnessModel;
import com.scorpion.brightnessmanager.model.RotationModel;
import com.scorpion.brightnessmanager.model.VolumeModel;
import com.scorpion.brightnessmanager.receivers.CallBroadCastReceiver;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

public class MyService extends Service {

    CountDownTimer brightnessCountDownTimer;
    CountDownTimer rotationCountDownTimer;
    CountDownTimer volumeCountDownTimer;
    public ArrayList<BrightnessModel> brightnessManagerList = new ArrayList<>();
    public ArrayList<RotationModel> rotationManagerList = new ArrayList<>();
    public ArrayList<VolumeModel> volumeManagerList = new ArrayList<>();
    String key = "brightnessManagerList";
    String key1 = "rotationManagerList";
    String key2 = "volumeManagerList";
    SharedPreferences shref;
    SharedPreferences.Editor editor;
    Gson gson = new Gson();
    String response;
    float curBrightnessValue = 50;
    boolean flag = true;
    boolean rotationFlag = true;
    boolean flag2 = true;
    boolean flag3 = true;
    boolean flag4 = true;
    int defaultMode;
    int ring;
    int media;
    int alarm;
    int voiceCall;
    int notification;
    AudioManager audioManager;
    boolean tmp = true;

    @Override
    public void onCreate() {
        super.onCreate();
        onDestroy();
        setForeground();

        try {

            shref = getSharedPreferences("MyPref", Context.MODE_PRIVATE);

//        if (shref.getInt("serviceONBrightness", 0) == 1)
            brightnessCountDownTimer = new CountDownTimer(10000, 2000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = am.getRunningAppProcesses();

                    response = shref.getString(key, "");
                    if (gson.fromJson(response, new TypeToken<List<BrightnessModel>>() {
                    }.getType()) != null)
                        brightnessManagerList = gson.fromJson(response, new TypeToken<List<BrightnessModel>>() {
                        }.getType());

                    if (shref.getInt("serviceONBrightness", 0) == 1) {
                        if (flag) {
                            try {
                                curBrightnessValue = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                            } catch (Settings.SettingNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                        for (int j = 0; j < brightnessManagerList.size(); j++) {
                            if (brightnessManagerList.get(j).getPkgName().equals(getCurrentAppName())) {
//                        if(flag){
//                            try {
//                                curBrightnessValue = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
//                            } catch (Settings.SettingNotFoundException e) {
//                                e.printStackTrace();
//                            }
//                            flag = false;
//                        }
                                flag = false;

                                if (brightnessManagerList.get(j).isAutoBrightness()) {
                                    Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);  //this will set the automatic mode on
                                } else {
                                    Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                                    Settings.System.putInt(getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightnessManagerList.get(j).getBrightnessValue());
                                }
                                break;
                            } else flag = true;
                        }
                        if (flag) {
                            flag = true;
                            Settings.System.putInt(getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, (int) curBrightnessValue);
                        }
                    }
                }

                @Override
                public void onFinish() {
                    brightnessCountDownTimer.start();
                }
            }.start();

//        if (shref.getInt("serviceONRotation", 0) == 1)
            rotationCountDownTimer = new CountDownTimer(10000, 2000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = am.getRunningAppProcesses();

                    response = shref.getString(key1, "");
                    if (gson.fromJson(response, new TypeToken<List<RotationModel>>() {
                    }.getType()) != null)
                        rotationManagerList = gson.fromJson(response, new TypeToken<List<RotationModel>>() {
                        }.getType());

                    if (shref.getInt("serviceONRotation", 0) == 1) {
                        for (int j = 0; j < rotationManagerList.size(); j++) {
                            if (rotationManagerList.get(j).getPkgName().equals(getCurrentAppName())) {
                                rotationFlag = false;
                                flag2 = true;

                                if (rotationManagerList.get(j).isAutoRotation()) {
                                    setAutoOrientationEnabled(getApplicationContext(), true);
                                } else {
                                    if (rotationManagerList.get(j).getOrientationMode() == 1) {
                                        Settings.System.putInt(getApplicationContext().getContentResolver(), "accelerometer_rotation", 0);
                                        Settings.System.putInt(MyService.this.getContentResolver(), "user_rotation", 0);
                                    } else {
                                        Settings.System.putInt(getApplicationContext().getContentResolver(), "accelerometer_rotation", 0);
                                        Settings.System.putInt(MyService.this.getContentResolver(), "user_rotation", 1);
                                    }
                                }
                                break;
                            } else rotationFlag = true;
                        }
                        if (rotationFlag) {
//                    rotationFlag = true;
                            if (flag2) {
                                Settings.System.putInt(getApplicationContext().getContentResolver(), "accelerometer_rotation", 0);
                                Settings.System.putInt(MyService.this.getContentResolver(), "user_rotation", 0);
                                setAutoOrientationEnabled(getApplicationContext(), false);
                                rotationFlag = false;
                                flag2 = false;
                            }
                        }
                    }
                }

                @Override
                public void onFinish() {
                    rotationCountDownTimer.start();
                }
            }.start();

//        if (shref.getInt("serviceONVolume", 0) == 1)
            volumeCountDownTimer = new CountDownTimer(10000, 2000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = am.getRunningAppProcesses();
                    audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                    ring = shref.getInt("ringProgress", audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
                    media = shref.getInt("mediaProgress", audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) - 1);
                    alarm = shref.getInt("alarmProgress", audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM) - 1);
                    voiceCall = shref.getInt("voiceProgress", audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL) - 1);
                    notification = shref.getInt("notificationProgress", audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION));
                    response = shref.getString(key2, "");
                    defaultMode = shref.getInt("defaultMode", 0);
                    if (gson.fromJson(response, new TypeToken<List<VolumeModel>>() {
                    }.getType()) != null)
                        volumeManagerList = gson.fromJson(response, new TypeToken<List<VolumeModel>>() {
                        }.getType());

                    Log.e("SIZE", String.valueOf(volumeManagerList.size()));

                    if (shref.getInt("serviceONVolume", 0) == 1) {
                        for (int j = 0; j < volumeManagerList.size(); j++) {
                            Log.e("APP1", volumeManagerList.get(j).getPkgName());
                            Log.e("APP1", String.valueOf(volumeManagerList.get(j).getMode()));
                            Log.e("APP2", getCurrentAppName());

                            if (volumeManagerList.get(j).getPkgName().equals(getCurrentAppName())) {
                                flag3 = false;
                                flag4 = true;

                                if (tmp) {
                                    if (volumeManagerList.get(j).getMode() == 0)
                                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                    else if (volumeManagerList.get(j).getMode() == 1)
                                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                    else if (volumeManagerList.get(j).getMode() == 2)
                                        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

                                    audioManager.setStreamVolume(AudioManager.STREAM_RING, volumeManagerList.get(j).getRing(), 0);
                                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeManagerList.get(j).getMedia(), 0);
                                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volumeManagerList.get(j).getAlarm(), 0);
                                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, volumeManagerList.get(j).getVoiceCall(), 0);
                                    audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, volumeManagerList.get(j).getNotification(), 0);
                                    tmp = false;
                                }
                                break;
                            } else flag3 = true;
                        }
                        if (flag3) {
                            if (flag4) {
                                tmp = true;

                                if (defaultMode == 0) {
                                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                    Log.e("MODE", "NORMAL1");
                                } else if (defaultMode == 1)
                                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                else if (defaultMode == 2)
                                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

                                audioManager.setStreamVolume(AudioManager.STREAM_RING, ring, 0);
                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, media, 0);
                                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, alarm, 0);
                                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, voiceCall, 0);
                                audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, notification, 0);
                                Log.e("MODE", "NORMAL2");
                                flag3 = false;
                                flag4 = false;
                            }
                        }
                    }
                }

                @Override
                public void onFinish() {
                    volumeCountDownTimer.start();
                }
            }.start();
        }
        catch (Exception e){}
    }

    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null) {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isForeground(Context ctx, String myPackage) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);

        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        if (componentInfo.getPackageName().equals(myPackage)) {
            return true;
        }
        return false;
    }

    public String getCurrentAppName() {

        long currentTimeMillis = System.currentTimeMillis();
        List<UsageStats> queryUsageStats = ((UsageStatsManager) getApplicationContext().getSystemService(USAGE_STATS_SERVICE)).queryUsageStats(0, currentTimeMillis - 3600000, currentTimeMillis);
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        String str = activityManager.getRunningAppProcesses().get(0).processName;
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(5);

        if (Build.VERSION.SDK_INT > 20) {
            if (queryUsageStats != null) {
                TreeMap treeMap = new TreeMap();
                for (UsageStats next : queryUsageStats) {
                    treeMap.put(Long.valueOf(next.getLastTimeUsed()), next);
                }
                if (treeMap.isEmpty()) {
                    str = BuildConfig.FLAVOR;
                } else {
                    UsageStats usageStats = (UsageStats) treeMap.get(treeMap.lastKey());
                    if (usageStats.getTotalTimeInForeground() > 0) {
                        str = usageStats.getPackageName();
                    }
                }
            }
        } else if (runningTasks.size() > 0) {
            str = runningTasks.get(0).topActivity.getPackageName();
        }
        Log.e("PROCESSMANAGER123", str);
        return str;
    }

    public static String getTopAppName(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String strName = "";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                strName = getLollipopFGAppPackageName(context);
            } else {
                strName = mActivityManager.getRunningTasks(1).get(0).topActivity.getClassName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strName;
    }

    private static String getLollipopFGAppPackageName(Context ctx) {

        try {
            UsageStatsManager usageStatsManager = (UsageStatsManager) ctx.getSystemService(USAGE_STATS_SERVICE);
            long milliSecs = 60 * 1000;
            Date date = new Date();
            List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, date.getTime() - milliSecs, date.getTime());
            if (queryUsageStats.size() > 0) {
            }
            long recentTime = 0;
            String recentPkg = "";
            for (int i = 0; i < queryUsageStats.size(); i++) {
                UsageStats stats = queryUsageStats.get(i);
                if (stats.getLastTimeStamp() > recentTime) {
                    recentTime = stats.getLastTimeStamp();
                    recentPkg = stats.getPackageName();
                }
            }
            return recentPkg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void setForeground() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel("noni", "system", 4);
            notificationManager.createNotificationChannel(notificationChannel);
            startForeground(11, new Notification.Builder(this, notificationChannel.getId()).setContentTitle(getResources().getString(R.string.app_name)).setContentText("Long press to hide this notification").setSmallIcon(R.drawable.ic_notifications_black_24dp).build());
            return;
        }
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setContentTitle(getResources().getString(R.string.app_name)).setSmallIcon(R.drawable.ic_notifications_black_24dp).setContentText("Long press to hide this notification");
        startForeground(11, builder.build());
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(NotificationCompat.CATEGORY_ALARM);
        long elapsedRealtime = SystemClock.elapsedRealtime() + 10000;
        Intent intent2 = new Intent(this, CallBroadCastReceiver.class);
        intent2.setAction("com.wond.call.coming");
        alarmManager.set(2, elapsedRealtime, PendingIntent.getBroadcast(this, 0, intent2, 0));
        return super.onStartCommand(intent, i, i2);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (brightnessCountDownTimer != null) brightnessCountDownTimer.cancel();
        if (rotationCountDownTimer != null) rotationCountDownTimer.cancel();

        shref = getSharedPreferences("MyPref", Context.MODE_PRIVATE);

//        if (shref.getInt("serviceONBrightness", 0) == 1 || shref.getInt("serviceONRotation", 0) == 1) {
//            if (shref.getInt("serviceONBrightness", 0) != 1)
//                if (brightnessCountDownTimer != null)
//                    brightnessCountDownTimer.cancel();
//
//            if (shref.getInt("serviceONRotation", 0) != 1)
//                if (rotationCountDownTimer != null)
//                    rotationCountDownTimer.cancel();
//
//        } else {
//            super.onDestroy();
//            if (brightnessCountDownTimer != null) brightnessCountDownTimer.cancel();
//            if (rotationCountDownTimer != null) rotationCountDownTimer.cancel();
//        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class LocalBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    public static void setAutoOrientationEnabled(Context context, boolean enabled) {
        Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
    }

}
