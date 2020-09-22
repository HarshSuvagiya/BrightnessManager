package com.scorpion.brightnessmanager.activity;

import androidx.annotation.NonNull;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scorpion.brightnessmanager.FBInterstitial;
import com.scorpion.brightnessmanager.R;
import com.scorpion.brightnessmanager.Utils;
import com.scorpion.brightnessmanager.adutils.AdHelper;

public class IntermediateActivity extends AppCompatActivity {

    DatabaseReference rootRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intermediate);
        FirebaseApp.initializeApp(IntermediateActivity.this);
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("BrightnessManager").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean value = Boolean.parseBoolean(dataSnapshot.child("LoadAd").getValue().toString());
                Utils.AdPos = Integer.parseInt(dataSnapshot.child("AdPerItem").getValue().toString());
                Utils.AdmobFacebook = Integer.parseInt(dataSnapshot.child("AdmobFacebook").getValue().toString());
//                Utils.AdmobFacebook = 1;
                Utils.isProLive = Boolean.parseBoolean(dataSnapshot.child("IsProLive").getValue().toString());
                Utils.timesInterAd = Integer.parseInt(dataSnapshot.child("TimesInterAd").getValue().toString());
//                Utils.idLoad = false;
                Utils.idLoad = value;
                if(Utils.idLoad)
                    initAdmobInter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        findViewById(R.id.brightnessManager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BrightnessManagerActivity.class));
                showAdmobInter1();
            }
        });
        findViewById(R.id.rotationManager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RotationManagerActivity.class));
                showAdmobInter1();
            }
        });
        findViewById(R.id.volumeManager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), VolumeManagerActivity.class));
                showAdmobInter1();
            }
        });
    }


    @Override
    public void onBackPressed() {
        showAdmobInter();
//        finish();
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
                        startActivity(new Intent(IntermediateActivity.this, ExitActivity.class));
                    }
                });

            } else {
                startActivity(new Intent(IntermediateActivity.this, ExitActivity.class));
            }
        }
    }

    public void showAdmobInter1() {

        if(Utils.AdmobFacebook == 2)
        {
            FBInterstitial.getInstance().displayFBInterstitial(IntermediateActivity.this, new FBInterstitial.FbCallback() {
                public void callbackCall() {

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

                    }
                });

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
