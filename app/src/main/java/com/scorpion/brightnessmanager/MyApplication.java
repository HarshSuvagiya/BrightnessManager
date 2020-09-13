package com.scorpion.brightnessmanager;

import android.app.Application;

import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;

public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
       AudienceNetworkAds.initialize(this);
        AdSettings.addTestDevice("340e2933-4197-42a7-becd-fe54e5e42200");
        FBInterstitial.getInstance().loadFBInterstitial(this);

    }
}
