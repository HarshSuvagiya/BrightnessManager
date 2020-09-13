package com.scorpion.brightnessmanager.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.scorpion.brightnessmanager.R;
import com.scorpion.brightnessmanager.Utils;

import java.util.ArrayList;
import java.util.List;

public class ExitActivity extends AppCompatActivity implements Animation.AnimationListener {

    public UnifiedNativeAd nativeAdmob;
    private NativeAd nativeAd;
    FrameLayout frameLayout;
    LinearLayout itemvidedt,itemsplashwall,itemwifi;
    ImageView btnYes, btnNo,btnrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);

        itemvidedt=findViewById(R.id.itemvidedt);
        itemsplashwall=findViewById(R.id.itemsplashwall);
        itemwifi=findViewById(R.id.itemwifi);

        btnYes = findViewById(R.id.btnYes);
        btnNo = findViewById(R.id.btnNo);
        btnrate=findViewById(R.id.btnrate);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ExitActivity.this, BrightnessManagerActivity.class));
                finish();
            }
        });

        btnrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("market://details?id="
                        + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(goToMarket);
            }
        });

        frameLayout = (FrameLayout) findViewById(R.id.id_native_ad);

        if (Utils.idLoad) {
            if (Utils.AdmobFacebook == 1) {
                nat();
            } else {
                loadNativeAd();

            }
        }

        TextView txtappname=findViewById(R.id.txtappname);
        txtappname.setSelected(true);
        TextView txtappname2=findViewById(R.id.txtappname2);
        txtappname2.setSelected(true);
        TextView txtappname3=findViewById(R.id.txtappname3);
        txtappname3.setSelected(true);


        itemvidedt.setVisibility(LinearLayout.VISIBLE);
        Animation animation   =    AnimationUtils.loadAnimation(this, R.anim.bounce_animation);
        animation.setDuration(1500);
        itemvidedt.setAnimation(animation);
        itemvidedt.animate();
        itemsplashwall.setAnimation(animation);
        itemsplashwall.animate();
        itemwifi.setAnimation(animation);
        itemwifi.animate();
        animation.start();

        itemvidedt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.scorpion.allinoneeditor&hl=en_IN"));
                startActivity(browserIntent);
            }
        });
        itemsplashwall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.scorpion.splashwalls&hl=en_IN"));
                startActivity(browserIntent);
            }
        });
        itemwifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.scorpion.wificonnect&hl=en_IN"));
                startActivity(browserIntent);

            }
        });


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ExitActivity.this, BrightnessManagerActivity.class));
        finish();
    }

    public void nat() {
        AdLoader.Builder builder = new AdLoader.Builder((Context) this, getResources().getString(R.string.native_ad_unit_id));
        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                frameLayout.setVisibility(0);
                if (nativeAdmob != null) {
                    nativeAdmob.destroy();
                }
                nativeAdmob = unifiedNativeAd;
                UnifiedNativeAdView unifiedNativeAdView = (UnifiedNativeAdView) getLayoutInflater().inflate(R.layout.native_ad, null);
                populateUnifiedNativeAdView(unifiedNativeAd, unifiedNativeAdView);
                frameLayout.removeAllViews();
                frameLayout.addView(unifiedNativeAdView);
            }
        });
        builder.withAdListener(new AdListener() {
            public void onAdFailedToLoad(int i) {
            }
        }).build().loadAd(new AdRequest.Builder().build());
    }

    public void populateUnifiedNativeAdView(UnifiedNativeAd unifiedNativeAd, UnifiedNativeAdView unifiedNativeAdView) {
        unifiedNativeAdView.setMediaView((MediaView) unifiedNativeAdView.findViewById(R.id.ad_media));
        unifiedNativeAdView.setHeadlineView(unifiedNativeAdView.findViewById(R.id.ad_headline));
        unifiedNativeAdView.setBodyView(unifiedNativeAdView.findViewById(R.id.ad_body));
        unifiedNativeAdView.setCallToActionView(unifiedNativeAdView.findViewById(R.id.ad_call_to_action));
        unifiedNativeAdView.setIconView(unifiedNativeAdView.findViewById(R.id.ad_app_icon));
        unifiedNativeAdView.setStarRatingView(unifiedNativeAdView.findViewById(R.id.ad_stars));
        unifiedNativeAdView.setAdvertiserView(unifiedNativeAdView.findViewById(R.id.ad_advertiser));
        ((TextView) unifiedNativeAdView.getHeadlineView()).setText(unifiedNativeAd.getHeadline());
        unifiedNativeAdView.setNativeAd(unifiedNativeAd);
    }

    private void loadNativeAd() {

//        final CustomTextView customTextView = (CustomTextView) findViewById(R.id.txt_adload);
        this.nativeAd = new NativeAd(getApplicationContext(),getResources().getString(R.string.fb_native_id));
        this.nativeAd.setAdListener(new NativeAdListener() {
            public void onAdClicked(Ad ad) {
            }

            public void onError(Ad ad, AdError adError) {

                nat();
            }

            public void onLoggingImpression(Ad ad) {
            }

            public void onMediaDownloaded(Ad ad) {
            }

            public void onAdLoaded(Ad ad) {
                inflateAd(nativeAd);
//                customTextView.setVisibility(8);
            }
        });
        this.nativeAd.loadAd();
    }

    public void inflateAd(NativeAd nativeAd2) {
        nativeAd2.unregisterView();
        NativeAdLayout nativeAdLayout = (NativeAdLayout) findViewById(R.id.native_ad_container);
        int i = 0;
        LinearLayout adView = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.fbnative_ad, nativeAdLayout, false);
        nativeAdLayout.addView(adView);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(getApplicationContext(), nativeAd2, nativeAdLayout);
        linearLayout.removeAllViews();
        linearLayout.addView(adOptionsView, 0);
        AdIconView adIconView = (AdIconView) adView.findViewById(R.id.native_ad_icon);
        TextView textView = (TextView) adView.findViewById(R.id.native_ad_title);
        com.facebook.ads.MediaView mediaView = (com.facebook.ads.MediaView) adView.findViewById(R.id.native_ad_media);
        TextView textView2 = (TextView) adView.findViewById(R.id.native_ad_sponsored_label);
        Button button = (Button) adView.findViewById(R.id.native_ad_call_to_action);
        textView.setText(nativeAd2.getAdvertiserName());
        ((TextView) adView.findViewById(R.id.native_ad_body)).setText(nativeAd2.getAdBodyText());
        ((TextView) adView.findViewById(R.id.native_ad_social_context)).setText(nativeAd2.getAdSocialContext());
        if (!nativeAd2.hasCallToAction()) {
            i = 4;
        }
        button.setVisibility(i);
        button.setText(nativeAd2.getAdCallToAction());
        textView2.setText(nativeAd2.getSponsoredTranslation());
        ArrayList arrayList = new ArrayList();
        arrayList.add(textView);
        arrayList.add(button);
        nativeAd2.registerViewForInteraction((View) adView, mediaView, (com.facebook.ads.MediaView) adIconView, (List<View>) arrayList);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
