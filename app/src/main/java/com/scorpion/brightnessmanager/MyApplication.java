package com.scorpion.brightnessmanager;

import android.app.Application;

import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;

public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
       AudienceNetworkAds.initialize(this);
        AdSettings.addTestDevice("3b2c3eea-3e39-468d-8623-7f79df47e083");
        FBInterstitial.getInstance().loadFBInterstitial(this);

    }
}
