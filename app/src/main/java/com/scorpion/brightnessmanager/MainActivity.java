package com.scorpion.brightnessmanager;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.AudienceNetworkAds;
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
import com.scorpion.brightnessmanager.adutils.AdHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public AppAdapter appAdapter;
    public ArrayList<BrightnessModel> appList = new ArrayList<>();
    public Context context;
    private EditText edtSearch;
    public PickAppListener listener;
    String key = "brightnessManagerList";
    SharedPreferences shref;
    SharedPreferences.Editor editor;
    private RecyclerView recycler;
    public static ArrayList<BrightnessModel> brightnessManagerList = new ArrayList<>();
    ToggleButton usagePermissionToggle, modifyToggle;
    Button closeDialog;
    CardView viewSave;
    Dialog dialog;
    boolean usageFlag = false, modifyFlag = false;
    EditText editSearch;
    ToggleButton service;
    MyService myService;
    Dialog calibrateDialog;
    boolean isCalibrated = false;
    float curBrightnessValue1 = 150;
    ProgressDialog progressDialog;
    RemoteMessage remoteMessage;

    DatabaseReference rootRef;
    private AdView mAdView;
    LinearLayout premium;
    ImageView btnPrivacyPolicy;
    private AdView mAdView2, mAdView1;
    LinearLayout layBanner2, layBanner1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);

        FirebaseApp.initializeApp(MainActivity.this);


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
        rootRef.child("BrightnessManager").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean value = Boolean.parseBoolean(dataSnapshot.child("LoadAd").getValue().toString());
                Utils.AdPos = Integer.parseInt(dataSnapshot.child("AdPerItem").getValue().toString());
                Utils.AdmobFacebook = Integer.parseInt(dataSnapshot.child("AdmobFacebook").getValue().toString());
                Utils.isProLive = Boolean.parseBoolean(dataSnapshot.child("IsProLive").getValue().toString());
                Utils.timesInterAd = Integer.parseInt(dataSnapshot.child("TimesInterAd").getValue().toString());
                Utils.idLoad = value;
                if (Utils.idLoad) {
                    mAdView1 = dialog.findViewById(R.id.adView1);
                    layBanner1 = dialog.findViewById(R.id.banner_container1);
                    AdHelper.AdLoadHelper(context, mAdView1, layBanner1);
                }

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

        shref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = shref.edit();
        context = this;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        editSearch = findViewById(R.id.editSearch);
        service = findViewById(R.id.serviceToggle);
        premium = findViewById(R.id.premium);

        premium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isProLive){
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.scorpion.brightnessmanagerpro"); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(context, "Pro version coming soon!!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

//        service.setChecked(true);
//        startService(new Intent(getApplicationContext(), MyService.class));
//        editor.putInt("serviceON", 1);
//        editor.commit();

        if (shref.getInt("serviceON", 0) == 1){
            service.setChecked(true);
            Utils.isServiceOn = 1;
        }
        else{
            service.setChecked(false);
            Utils.isServiceOn = 0;
        }


        service.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(new Intent(getApplicationContext(), MyService.class));
                    editor.putInt("serviceON", 1);
                    Utils.isServiceOn = 1;
                    editor.commit();
                } else {
                    stopService(new Intent(getApplicationContext(), MyService.class));
                    editor.putInt("serviceON", 0);
                    Utils.isServiceOn = 0;
                    editor.commit();
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
        dialog.setContentView(R.layout.permission_popup);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        usagePermissionToggle = dialog.findViewById(R.id.chkUsagePermission);
        modifyToggle = dialog.findViewById(R.id.chkModifySettingPermission);
        viewSave = dialog.findViewById(R.id.viewSave);

        usagePermissionToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!checkUsageAccess()) {
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    startActivity(intent);
//                    usagePermissionToggle.setChecked(true);
                }
//                else
//                    usagePermissionToggle.setChecked(false);
            }
        });

        modifyToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkSystemWritePermission();
//                if(checkSystemWritePermission())
//                    modifyToggle.setChecked(true);
//                else
//                    modifyToggle.setChecked(false);
            }
        });

        viewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usagePermissionToggle.isChecked() && modifyToggle.isChecked())
                    dialog.dismiss();
                else
                    Toast.makeText(context, "Please allow all permission!!!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                calibrateDialog = new Dialog(context);
                calibrateDialog.setContentView(R.layout.find_max_brightness_popup);
                calibrateDialog.setCanceledOnTouchOutside(false);
                calibrateDialog.setCancelable(false);
                calibrateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                calibrateDialog.show();
                ImageView txtGet;
                CardView viewSave;
                txtGet = calibrateDialog.findViewById(R.id.txtGet);
                viewSave = calibrateDialog.findViewById(R.id.viewSave);

                if (Utils.idLoad) {
                    mAdView2 = calibrateDialog.findViewById(R.id.adView2);
                    layBanner2 = calibrateDialog.findViewById(R.id.banner_container2);
                    AdHelper.AdLoadHelper(context, mAdView2, layBanner2);
                }

                txtGet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            float curBrightnessValue = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
                            editor.putFloat("MaxBrightness", curBrightnessValue);
                            editor.commit();
                        } catch (Settings.SettingNotFoundException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(context, "Make sure your device is on full brightness, if yes hit save.", Toast.LENGTH_SHORT).show();
                        isCalibrated = true;
                    }
                });

                viewSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isCalibrated) {
                            try {
                                float curBrightnessValue = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
                                if (curBrightnessValue < 255)
                                    curBrightnessValue = 255;
                                editor.putFloat("MaxBrightness", curBrightnessValue);
                                editor.commit();
                            } catch (Settings.SettingNotFoundException e) {
                                e.printStackTrace();
                            }
                            Settings.System.putInt(getApplicationContext().getContentResolver(),
                                    Settings.System.SCREEN_BRIGHTNESS, (int) curBrightnessValue1);
                            calibrateDialog.dismiss();
                        } else
                            Toast.makeText(context, "Please hit GET button in order to calibrate.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        if (!isUsageEnabled(getApplicationContext()) || !isModificationEnabled(getApplicationContext())) {
            dialog.show();
        }

        recycler = findViewById(R.id.recycler);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL);
        recycler.addItemDecoration(new SimpleDividerItemDecoration(this));
        shref = getSharedPreferences("MyFavImages", Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String response = shref.getString(key, "");

        if (gson.fromJson(response, new TypeToken<List<BrightnessModel>>() {
        }.getType()) != null)
            brightnessManagerList = gson.fromJson(response, new TypeToken<List<BrightnessModel>>() {
            }.getType());
        context = this;
        updateRecyclerView("");


        btnPrivacyPolicy = findViewById(R.id.btnPrivacyPolicy);
        btnPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/brightnessmanager/home"));
                startActivity(browserIntent);
            }
        });
    }

    public void addToList() {
        Gson gson = new Gson();
        String json = gson.toJson(brightnessManagerList);
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
            UsageStatsManager usageStatsManager = (UsageStatsManager) ctx.getSystemService("usagestats");
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
        ArrayList<BrightnessModel> filterList = new ArrayList<>();

        for (BrightnessModel model : appList){
            if (model.getAppLabel().toLowerCase().contains(appName.toLowerCase())){
                filterList.add(model);
            }
        }
        appAdapter = new AppAdapter(MainActivity.this, filterList);
        recycler.setAdapter(appAdapter);
    }

    @SuppressLint({"StaticFieldLeak"})
    public void updateRecyclerView(final String str1) {
        new AsyncTask<Void, Void, ArrayList<BrightnessModel>>() {
            public void onPreExecute() {
                super.onPreExecute();
                appList.clear();
            }

            public ArrayList<BrightnessModel> doInBackground(Void... voidArr) {
                PackageManager packageManager = context.getPackageManager();
                ArrayList<BrightnessModel> arrayList = new ArrayList<>();
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
                        BrightnessModel appObject = new BrightnessModel(charSequence, str, sb2, false, 200);
                        if (!checkAppExists(str, arrayList)) {
                            arrayList.add(appObject);
                        }
                    } else if (charSequence.toLowerCase().contains(str1.toLowerCase())) {
                        BrightnessModel appObject = new BrightnessModel(charSequence, str, sb2, false, 200);
                        if (!checkAppExists(str, arrayList)) {
                            arrayList.add(appObject);
                        }
                    }
                }

                Collections.sort(arrayList, new Comparator<BrightnessModel>() {
                    public int compare(BrightnessModel appObject, BrightnessModel appObject2) {
                        return appObject.getAppLabel().compareTo(appObject2.getAppLabel());
                    }
                });
                return arrayList;
            }

            public void onPostExecute(ArrayList<BrightnessModel> arrayList) {
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
                AppAdapter appAdapter = new AppAdapter(MainActivity.this, appList);
                recycler.setAdapter(appAdapter);

            }
        }.execute(new Void[0]);
    }

    public boolean checkAppExists(String str, ArrayList<BrightnessModel> arrayList) {
        if (arrayList.size() == 0) {
            return false;
        }
        for (int i = 0; i < arrayList.size(); i++) {
            if (((BrightnessModel) arrayList.get(i)).getPkgName().equals(str)) {
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
        if(Utils.idLoad)
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
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService("appops");
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
            return android.provider.Settings.System.canWrite(context);
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

        if(Utils.AdmobFacebook == 2)
        {
            FBInterstitial.getInstance().displayFBInterstitial(MainActivity.this, new FBInterstitial.FbCallback() {
                public void callbackCall() {
                    startActivity(new Intent(MainActivity.this, ExitActivity.class));
                }
            });
        }
        else {
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
                        startActivity(new Intent(MainActivity.this, ExitActivity.class));
                    }
                });

            } else {
                startActivity(new Intent(MainActivity.this, ExitActivity.class));
            }
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
