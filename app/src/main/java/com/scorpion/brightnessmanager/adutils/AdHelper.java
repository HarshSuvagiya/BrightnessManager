package com.scorpion.brightnessmanager.adutils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.scorpion.brightnessmanager.R;
import com.scorpion.brightnessmanager.Utils;

public class AdHelper {

    private static AdView adView2;

    public static void AdLoadHelper(final Context context, AdView adView, LinearLayout layBanner) {

        adView2 = adView;
        if (Utils.idLoad) {
            if (Utils.AdmobFacebook == 2) {

//                AdSettings.addTestDevice("\"1b2ee112-d0b0-4949-a1ed-44a30179e646\"");
                com.facebook.ads.AdView adView1 = new com.facebook.ads.AdView(context, context.getResources().getString(R.string.fb_banner_id), AdSize.BANNER_HEIGHT_50);
                // Add the ad view to your activity layout
                if(layBanner!=null) {
                    layBanner.removeAllViews();
                }
                layBanner.addView(adView1);
                // Request an ad
                adView1.loadAd();
                adView2.setVisibility(View.GONE);
                adView1.setAdListener(new com.facebook.ads.AdListener() {
                    @Override
                    public void onError(Ad ad, AdError adError) {
//                        Toast.makeText(context, "Error: " + adError.getErrorMessage(),
//                                Toast.LENGTH_LONG).show();
                        adView2.setVisibility(View.VISIBLE);
                        MobileAds.initialize(context, new OnInitializationCompleteListener() {
                            @Override
                            public void onInitializationComplete(InitializationStatus initializationStatus) {
                            }
                        });
                        AdRequest adRequest = new AdRequest.Builder().build();
                        adView2.loadAd(adRequest);
                        adView2.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                super.onAdClosed();
                            }

                            @Override
                            public void onAdFailedToLoad(int i) {
                                super.onAdFailedToLoad(i);
                            }

                            @Override
                            public void onAdLeftApplication() {
                                super.onAdLeftApplication();
                            }

                            @Override
                            public void onAdOpened() {
                                super.onAdOpened();
                            }

                            @Override
                            public void onAdLoaded() {
                                super.onAdLoaded();
                                adView2.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAdClicked() {
                                super.onAdClicked();
                            }

                            @Override
                            public void onAdImpression() {
                                super.onAdImpression();
                            }
                        });

                    }

                    @Override
                    public void onAdLoaded(Ad ad) {

                    }

                    @Override
                    public void onAdClicked(Ad ad) {

                    }

                    @Override
                    public void onLoggingImpression(Ad ad) {

                    }
                });

            } else {
                adView2.setVisibility(View.VISIBLE);
                layBanner.setVisibility(View.GONE);
                MobileAds.initialize(context, new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {
                    }
                });
                AdRequest adRequest = new AdRequest.Builder().build();
                adView2.loadAd(adRequest);
                adView2.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                    }

                    @Override
                    public void onAdFailedToLoad(int i) {
                        super.onAdFailedToLoad(i);
                    }

                    @Override
                    public void onAdLeftApplication() {
                        super.onAdLeftApplication();
                    }

                    @Override
                    public void onAdOpened() {
                        super.onAdOpened();
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        adView2.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                    }
                });
            }

        }
    }

    public static void AdLoadHelperAdmob(final Context context, AdView adView) {

        adView2 = adView;
        if (Utils.idLoad) {
            MobileAds.initialize(context, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
            AdRequest adRequest = new AdRequest.Builder().build();
            adView2.loadAd(adRequest);
            adView2.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    adView2.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }
            });
        }
    }

}
