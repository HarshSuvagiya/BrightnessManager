package com.scorpion.brightnessmanager;

import android.app.Application;

import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;

public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
       AudienceNetworkAds.initialize(this);
        AdSettings.addTestDevice("ceda4006-1573-433a-8605-eb7f9abc2a84");
        FBInterstitial.getInstance().loadFBInterstitial(this);

    }
}
