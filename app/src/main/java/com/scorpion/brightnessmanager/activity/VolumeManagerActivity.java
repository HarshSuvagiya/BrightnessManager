package com.scorpion.brightnessmanager.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scorpion.brightnessmanager.PickAppListener;
import com.scorpion.brightnessmanager.R;
import com.scorpion.brightnessmanager.SimpleDividerItemDecoration;
import com.scorpion.brightnessmanager.Utils;
import com.scorpion.brightnessmanager.adapter.VolumeAdapter;
import com.scorpion.brightnessmanager.adutils.AdHelper;
import com.scorpion.brightnessmanager.model.VolumeModel;
import com.scorpion.brightnessmanager.service.MyService;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class VolumeManagerActivity extends AppCompatActivity {

    public VolumeAdapter appAdapter;
    public ArrayList<VolumeModel> appList = new ArrayList<>();
    public Context context;
    private EditText edtSearch;
    public PickAppListener listener;
    String key = "volumeManagerList";
    SharedPreferences shref;
    SharedPreferences.Editor editor;
    private RecyclerView recycler;
    public static ArrayList<VolumeModel> volumeManagerList = new ArrayList<>();
    SwitchButton usagePermissionToggle, modifyToggle;
    Button closeDialog;
    CardView viewSave;
    Dialog dialog;
    boolean usageFlag = false, modifyFlag = false;
    EditText editSearch;
    SwitchButton service;
    MyService myService;
    Dialog defaultDialog;
    boolean isCalibrated = false;
    float curBrightnessValue1 = 150;
    ProgressDialog progressDialog;
    RemoteMessage remoteMessage;
    int defaultMode;
    DatabaseReference rootRef;
    private AdView mAdView;
    AudioManager audioManager;
    ImageView settings;
    LinearLayout layBanner2, layBanner1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volume_manager);

        FirebaseApp.initializeApp(VolumeManagerActivity.this);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.volume_manager));
        try {
            if (getIntent().getExtras() != null) {
                Object value = getIntent().getExtras().get("link");
                Log.e("MainActivity: ", String.valueOf(value));
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse((String) value));
                startActivity(browserIntent);
            }
        } catch (Exception e) {
        }

        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("VolumeManager").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean value = Boolean.parseBoolean(dataSnapshot.child("LoadAd").getValue().toString());

                Utils.AdPos = Integer.parseInt(dataSnapshot.child("AdPerItem").getValue().toString());
                Utils.timesInterAd = Integer.parseInt(dataSnapshot.child("TimesInterAd").getValue().toString());
                Utils.idLoad = value;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//            }
//        },2000);

        shref = getSharedPreferences("MyPref", 0);
        editor = shref.edit();
        context = this;
        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        editSearch = findViewById(R.id.editSearch);
        service = findViewById(R.id.serviceToggle);
        settings = findViewById(R.id.settings);

//        service.setChecked(true);
//        startService(new Intent(getApplicationContext(), MyService.class));
//        editor.putInt("serviceONVolume", 1);
//        editor.commit();

        if (shref.getInt("serviceONVolume", 0) == 1) {
            service.setChecked(true);
            Utils.isServiceOnVolume = 1;
        } else {
            service.setChecked(false);
            Utils.isServiceOnVolume = 0;
        }

        service.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    if (shref.getInt("serviceONRotation", 0) != 1 || shref.getInt("serviceONBrightness", 0) != 1) {
                        startService(new Intent(getApplicationContext(), MyService.class));
                    }

                    editor.putInt("serviceONVolume", 1);
                    Utils.isServiceOnVolume = 1;
                    editor.apply();
                } else {
                    if (shref.getInt("serviceONRotation", -1) == 0 && shref.getInt("serviceONBrightness", -1) == 0) {
                        stopService(new Intent(getApplicationContext(), MyService.class));
                    }

                    editor.putInt("serviceONVolume", 0);
                    Utils.isServiceOnVolume = 0;
                    editor.apply();
                }
            }
        });

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchApp(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.permission_popup__volume);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        usagePermissionToggle = dialog.findViewById(R.id.chkUsagePermission);
        modifyToggle = dialog.findViewById(R.id.chkModifySettingPermission);
        viewSave = dialog.findViewById(R.id.viewSave);
        layBanner1 = dialog.findViewById(R.id.banner_container1);
        try {
            AdHelper.AdLoadHelper(context, (AdView) dialog.findViewById(R.id.adView), layBanner1);
        }
        catch (Exception e){}

        usagePermissionToggle.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (!checkUsageAccess()) {
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    startActivity(intent);
//                    usagePermissionToggle.setChecked(true);
                }
            }
        });

        modifyToggle.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    startActivity(intent);
                }
//                else
//                    modifyToggle.setChecked(true);
            }
        });

//        usagePermissionToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (!checkUsageAccess()) {
//                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
//                    startActivity(intent);
////                    usagePermissionToggle.setChecked(true);
//                }
////                else
////                    usagePermissionToggle.setChecked(false);
//            }
//        });

//        modifyToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//                if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
//                    Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
//                    startActivity(intent);
//                } else
//                    modifyToggle.setChecked(true);
////                checkSystemWritePermission();
////                if(checkSystemWritePermission())
////                    modifyToggle.setChecked(true);
////                else
////                    modifyToggle.setChecked(false);
//            }
//        });

        viewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usagePermissionToggle.isChecked() && modifyToggle.isChecked()){
                    dialog.dismiss();
                    editor.putBoolean("VolumeDefaultSet", true);
                }
                else
                    Toast.makeText(context, "Please allow all permission!!!", Toast.LENGTH_SHORT).show();
            }
        });

//        if (!isUsageEnabled(getApplicationContext()) && !isModificationEnabled(getApplicationContext())) {
        if(!shref.getBoolean("VolumeDefaultSet",false)){
            dialog.show();
        }

        recycler = findViewById(R.id.recycler);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(),
//                DividerItemDecoration.VERTICAL);
//        recycler.addItemDecoration(new SimpleDividerItemDecoration(this));
//        shref = getSharedPreferences("MyFavImages", Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String response = shref.getString(key, "");

        if (gson.fromJson(response, new TypeToken<List<VolumeModel>>() {
        }.getType()) != null)
            volumeManagerList = gson.fromJson(response, new TypeToken<List<VolumeModel>>() {
            }.getType());
        context = this;

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                showDefaultDialog();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDefaultDialog();
            }
        });

        for (int i = 0; i < volumeManagerList.size(); i++)
            Log.e("PACKAGE000", volumeManagerList.get(i).getPkgName());
        updateRecyclerView("");
    }

    public void showDefaultDialog() {
        defaultDialog = new Dialog(context);
        defaultDialog.setContentView(R.layout.configure_volume_popup);
        defaultDialog.setCanceledOnTouchOutside(false);
        defaultDialog.setCancelable(false);
        defaultDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        defaultDialog.show();

        LinearLayout modeNornalll, modeVibratell, modeSilentll;
        final RadioButton rdModeNormal, rdModeVibrate, rdModeSilent;
        CardView viewSave;
        TextView txtAppName;
        final SeekBar seekbarRing, seekbarMedia, seekbarAlarm, seekbarVoiceCall, seekbarNotification;
        int getMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        modeNornalll = defaultDialog.findViewById(R.id.modeNornalll);
        modeVibratell = defaultDialog.findViewById(R.id.modeVibratell);
        modeSilentll = defaultDialog.findViewById(R.id.modeSilentll);
        rdModeNormal = defaultDialog.findViewById(R.id.rdModeNormal);
        rdModeVibrate = defaultDialog.findViewById(R.id.rdModeVibrate);
        rdModeSilent = defaultDialog.findViewById(R.id.rdModeSilent);
        seekbarRing = defaultDialog.findViewById(R.id.seekbarRing);
        seekbarMedia = defaultDialog.findViewById(R.id.seekbarMedia);
        seekbarAlarm = defaultDialog.findViewById(R.id.seekbarAlarm);
        seekbarVoiceCall = defaultDialog.findViewById(R.id.seekbarVoiceCall);
        seekbarNotification = defaultDialog.findViewById(R.id.seekbarNotification);
        viewSave = defaultDialog.findViewById(R.id.viewSave);
        txtAppName = defaultDialog.findViewById(R.id.txtAppName);

        seekbarRing.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
        seekbarMedia.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekbarAlarm.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM));
        seekbarVoiceCall.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL));
        seekbarNotification.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION));

        seekbarRing.setProgress(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
        seekbarMedia.setProgress(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) - 1);
        seekbarAlarm.setProgress(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) - 1);
        seekbarVoiceCall.setProgress(audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL) - 1);
        seekbarNotification.setProgress(audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION));

        layBanner2 = defaultDialog.findViewById(R.id.banner_container2);

        AdHelper.AdLoadHelper(context, (AdView) defaultDialog.findViewById(R.id.adView),layBanner2);
        int mode = shref.getInt("defaultMode", 0);

        if (mode == 2) {
            rdModeNormal.setChecked(false);
            rdModeVibrate.setChecked(true);
            rdModeSilent.setChecked(false);
        } else if (mode == 0) {
            rdModeNormal.setChecked(true);
            rdModeVibrate.setChecked(false);
            rdModeSilent.setChecked(false);
        } else {
            rdModeNormal.setChecked(false);
            rdModeVibrate.setChecked(false);
            rdModeSilent.setChecked(true);
        }

        txtAppName.setText("Default settings");
        Log.e("PROGRESS", String.valueOf(shref.getInt("ringProgress", 0)));

        seekbarRing.setProgress(shref.getInt("ringProgress", audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)));
        seekbarMedia.setProgress(shref.getInt("mediaProgress", audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) - 1));
        seekbarAlarm.setProgress(shref.getInt("alarmProgress", audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM) - 1));
        seekbarVoiceCall.setProgress(shref.getInt("voiceProgress", audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL) - 1));
        seekbarNotification.setProgress(shref.getInt("notificationProgress", audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION)));

        modeNornalll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rdModeNormal.setChecked(true);
                rdModeVibrate.setChecked(false);
                rdModeSilent.setChecked(false);
                defaultMode = 1;
                editor.putInt("defaultMode", 0);
                editor.commit();
            }
        });

        modeVibratell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rdModeNormal.setChecked(false);
                rdModeVibrate.setChecked(true);
                rdModeSilent.setChecked(false);
                defaultMode = 2;
                editor.putInt("defaultMode", 2);
                editor.commit();
            }
        });

        modeSilentll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rdModeNormal.setChecked(false);
                rdModeVibrate.setChecked(false);
                rdModeSilent.setChecked(true);
                defaultMode = 3;
                editor.putInt("defaultMode", 1);
                editor.commit();
            }
        });

        rdModeNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rdModeNormal.setChecked(true);
                rdModeVibrate.setChecked(false);
                rdModeSilent.setChecked(false);
                defaultMode = 1;
                editor.putInt("defaultMode", 0);
                editor.commit();
            }
        });

        rdModeVibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rdModeNormal.setChecked(false);
                rdModeVibrate.setChecked(true);
                rdModeSilent.setChecked(false);
                defaultMode = 2;
                editor.putInt("defaultMode", 2);
                editor.commit();
            }
        });

        rdModeSilent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rdModeNormal.setChecked(false);
                rdModeVibrate.setChecked(false);
                rdModeSilent.setChecked(true);
                defaultMode = 3;
                editor.putInt("defaultMode", 1);
                editor.commit();
            }
        });


        viewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("ringProgress", seekbarRing.getProgress());
                editor.putInt("mediaProgress", seekbarMedia.getProgress());
                editor.putInt("alarmProgress", seekbarAlarm.getProgress());
                editor.putInt("voiceProgress", seekbarVoiceCall.getProgress());
                editor.putInt("notificationProgress", seekbarNotification.getProgress());
                editor.commit();
                Log.e("PROGRESS123", String.valueOf(seekbarRing.getProgress()));
                defaultDialog.dismiss();
            }
        });
    }

    public void addToList() {
        Gson gson = new Gson();
        String json = gson.toJson(volumeManagerList);
        editor = shref.edit();
        editor.putString(key, json);
        editor.commit();
    }

    public boolean checkUsageAccess() {
        try {
            usageFlag = true;
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
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

    public void searchApp(String appName){
        ArrayList<VolumeModel> filterList = new ArrayList<>();

        for (VolumeModel model : appList){
            if (model.getAppLabel().toLowerCase().contains(appName.toLowerCase())){
                filterList.add(model);
            }
        }
        appAdapter = new VolumeAdapter(VolumeManagerActivity.this, filterList);
        recycler.setAdapter(appAdapter);
    }

    @SuppressLint({"StaticFieldLeak"})
    public void updateRecyclerView(final String str1) {
        new AsyncTask<Void, Void, ArrayList<VolumeModel>>() {
            public void onPreExecute() {
                super.onPreExecute();
                appList.clear();
            }

            public ArrayList<VolumeModel> doInBackground(Void... voidArr) {
                PackageManager packageManager = context.getPackageManager();
                ArrayList<VolumeModel> arrayList = new ArrayList<>();
                Intent intent = new Intent("android.intent.action.MAIN", null);
                intent.addCategory("android.intent.category.LAUNCHER");
                List queryIntentActivities = context.getPackageManager().queryIntentActivities(intent, 0);
                for (int i = 0; i < queryIntentActivities.size(); i++) {
                    String str = ((ResolveInfo) queryIntentActivities.get(i)).activityInfo.applicationInfo.packageName;
                    String charSequence = ((ResolveInfo) queryIntentActivities.get(i)).activityInfo.applicationInfo.loadLabel(packageManager).toString();
                    StringBuilder sb = new StringBuilder();
                    sb.append("android.resource://");
                    sb.append(str);
                    sb.append("/");
                    sb.append(((ResolveInfo) queryIntentActivities.get(i)).activityInfo.applicationInfo.icon);
                    String sb2 = sb.toString();

                    if (str1.equals("")) {
                        VolumeModel appObject = new VolumeModel(charSequence, sb2, str, 0, 0, 0, 0, 0, 0);
                        if (!checkAppExists(str, arrayList)) {
                            arrayList.add(appObject);
                        }
                    } else if (charSequence.toLowerCase().contains(str1.toLowerCase())) {
                        VolumeModel appObject = new VolumeModel(charSequence, sb2, str, 0, 0, 0, 0, 0, 0);
                        if (!checkAppExists(str, arrayList)) {
                            arrayList.add(appObject);
                        }
                    }
                }

                Collections.sort(arrayList, new Comparator<VolumeModel>() {
                    public int compare(VolumeModel appObject, VolumeModel appObject2) {
                        return appObject.getAppLabel().compareTo(appObject2.getAppLabel());
                    }
                });
                return arrayList;
            }

            public void onPostExecute(ArrayList<VolumeModel> arrayList) {
                super.onPostExecute(arrayList);
                appList.addAll(arrayList);
//                appAdapter.notifyDataSetChanged();
//                pbLoading.setVisibility(8);
                progressDialog.dismiss();
//                ArrayList<BrightnessModel> alist = new ArrayList<>();
//                for (int i = 0; i < appList.size(); i++) {
//                    if (i % Utils.AdPos == 0) {
//                        alist.add(appList.get(i));
//                    }
//                    alist.add(appList.get(i));
//                }
                VolumeAdapter appAdapter = new VolumeAdapter(VolumeManagerActivity.this, appList);
                recycler.setAdapter(appAdapter);

            }
        }.execute(new Void[0]);
    }

    public boolean checkAppExists(String str, ArrayList<VolumeModel> arrayList) {
        if (arrayList.size() == 0) {
            return false;
        }
        for (int i = 0; i < arrayList.size(); i++) {
            if (((VolumeModel) arrayList.get(i)).getPkgName().equals(str)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkSystemWritePermission() {
        boolean retVal = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            retVal = Settings.System.canWrite(this);
            if (retVal) {
//                modifyFlag = true;
//                modifyToggle.setChecked(true);

                //Toast.makeText(this, "Write allowed :-) ", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(this, "Write not allowed :-( ", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }
        return retVal;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.idLoad)
            initAdmobInter();
        if (!isUsageEnabled(getApplicationContext()))
            usagePermissionToggle.setChecked(false);
        else
            usagePermissionToggle.setChecked(true);

        if (!isModificationEnabled(getApplicationContext()))
            modifyToggle.setChecked(false);
        else
            modifyToggle.setChecked(true);

    }

    public static boolean isUsageEnabled(Context context) {
        boolean z = true;
        if (Build.VERSION.SDK_INT < 21) {
            return true;
        }
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(APP_OPS_SERVICE);
            String str = "android:get_usage_stats";
            if ((Build.VERSION.SDK_INT >= 29 ? appOpsManager.unsafeCheckOpNoThrow(str, applicationInfo.uid, applicationInfo.packageName) : appOpsManager.checkOpNoThrow(str, applicationInfo.uid, applicationInfo.packageName)) != 0) {
                z = false;
            }
            return z;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public static boolean isModificationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            return Settings.System.canWrite(context);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        showAdmobInter();
    }

    private InterstitialAd mInterstitialAd;
    public void showAdmobInter() {
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    Log.e("TAG Admob", "The interstitial loaded.");
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                    Log.e("TAG Admob", "The interstitial wasn't loaded yet :" + errorCode);
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when the ad is displayed.
                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                }

                @Override
                public void onAdClosed() {
                    finish();
                }
            });

        } else {
            finish();
        }
    }

    public void initAdmobInter() {
        mInterstitialAd = new com.google.android.gms.ads.InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.inter_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.e("TAG Admob", "The interstitial loaded.");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.e("TAG Admob", "The interstitial wasn't loaded yet :" + errorCode);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
            }
        });
    }
}
