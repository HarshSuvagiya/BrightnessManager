package com.scorpion.brightnessmanager.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.scorpion.brightnessmanager.FBInterstitial;
import com.scorpion.brightnessmanager.R;
import com.scorpion.brightnessmanager.Utils;

public class IntermediateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intermediate);

        findViewById(R.id.brightnessManager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BrightnessManagerActivity.class));
            }
        });
        findViewById(R.id.rotationManager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RotationManagerActivity.class));
            }
        });
        findViewById(R.id.volumeManager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), VolumeManagerActivity.class));
            }
        });
    }


    @Override
    public void onBackPressed() {
        showAdmobInter();
        finish();
    }

    private InterstitialAd mInterstitialAd;

    public void showAdmobInter() {

        if(Utils.AdmobFacebook == 2)
        {
            FBInterstitial.getInstance().displayFBInterstitial(IntermediateActivity.this, new FBInterstitial.FbCallback() {
                public void callbackCall() {
                    startActivity(new Intent(IntermediateActivity.this, ExitActivity.class));
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
                        finish();
                    }
                });

            } else {
                startActivity(new Intent(IntermediateActivity.this, ExitActivity.class));
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

    @Override
    protected void onResume() {
        super.onResume();
        if(Utils.idLoad)
            initAdmobInter();
    }
}
