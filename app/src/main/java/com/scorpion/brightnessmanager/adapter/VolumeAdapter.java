package com.scorpion.brightnessmanager.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scorpion.brightnessmanager.R;
import com.scorpion.brightnessmanager.Utils;
import com.scorpion.brightnessmanager.adutils.AdHelper;
import com.scorpion.brightnessmanager.model.VolumeModel;
import com.squareup.picasso.Picasso;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import static com.scorpion.brightnessmanager.activity.VolumeManagerActivity.volumeManagerList;

public class VolumeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public ArrayList<VolumeModel> appList;
    private Context context;
    SharedPreferences shref;
    SharedPreferences.Editor editor;
    String key = "volumeManagerList";
    int k = 0;
    int getOrientationMode = 0;
    boolean isAutoRotationChecked = false;
    float MaxBrightness;
    int additionalContent = 0;
    AudioManager audioManager;
    LinearLayout modeNornalll, modeVibratell, modeSilentll;
    RadioButton rdModeNormal, rdModeVibrate, rdModeSilent;
    SeekBar seekbarRing, seekbarMedia, seekbarAlarm, seekbarVoiceCall, seekbarNotification;
    Dialog dialog;
    LinearLayout layBanner;
    class ADHolder extends RecyclerView.ViewHolder {
        AdView adView;

        public ADHolder(@NonNull View itemView) {
            super(itemView);
            this.setIsRecyclable(false);
            adView = itemView.findViewById(R.id.adView);
            layBanner = itemView.findViewById(R.id.banner_container);
        }
    }

    class ViewHolder1 extends RecyclerView.ViewHolder {
        public ImageView im_icon_app;
        public TextView tv_app_name, tv_app_package;
        SwitchButton toggle;


        ViewHolder1(View view) {
            super(view);
            this.setIsRecyclable(false);
            this.im_icon_app = (ImageView) view.findViewById(R.id.im_icon_app);
            this.tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);
            this.tv_app_package = (TextView) view.findViewById(R.id.tv_app_package);
            this.toggle = view.findViewById(R.id.toggle);

        }
    }

    public VolumeAdapter(Context context2, ArrayList<VolumeModel> arrayList) {
        this.context = context2;
        this.appList = arrayList;
        initAdmobInter();
        shref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String response = shref.getString(key, "");

        if (gson.fromJson(response, new TypeToken<List<VolumeModel>>() {
        }.getType()) != null)
            volumeManagerList = gson.fromJson(response, new TypeToken<List<VolumeModel>>() {
            }.getType());


    }

    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == CONTENT_TYPE) {
            return new ViewHolder1(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_app_volume, viewGroup, false));
        } else {
            return new ADHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_ad, viewGroup, false));
        }
    }

    private int getRealPosition(int position) {
        if (Utils.AdPos == 0) {
            return position;
        } else {
            return position - position / Utils.AdPos;
        }
    }

    private AdView mAdView1;
    LinearLayout viewSeekbarContainer;

    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {

        if (getItemViewType(i) == AD_TYPE) {
            AdHelper.AdLoadHelper(context, ((ADHolder) viewHolder).adView,layBanner);
        } else {

            final int i1 = getRealPosition(i);
            final ViewHolder1 viewHolderNew = (ViewHolder1) viewHolder;
            Glide.with(context).asBitmap().load(Uri.parse(((VolumeModel) appList.get(i1)).getIcon())).thumbnail(0.5f).into(((ViewHolder1) viewHolder).im_icon_app);
            ((ViewHolder1) viewHolder).tv_app_name.setText(appList.get(i1).getAppLabel());
            ((ViewHolder1) viewHolder).tv_app_package.setText(appList.get(i1).getPkgName());

            ((ViewHolder1) viewHolder).toggle.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                    if (Utils.isServiceOnVolume == 1) {
                        if (viewHolderNew.toggle.isChecked()) {
                            viewHolderNew.toggle.setChecked(true);
                            dialog = new Dialog(context);
                            dialog.setContentView(R.layout.configure_volume_popup);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.setCancelable(false);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                            if (Utils.idLoad) {

                                mAdView1 = dialog.findViewById(R.id.adView);
                                AdRequest adRequest = new AdRequest.Builder().build();
                                mAdView1.loadAd(adRequest);

                                mAdView1.setAdListener(new AdListener() {
                                    @Override
                                    public void onAdLoaded() {
                                    }

                                    @Override
                                    public void onAdFailedToLoad(int errorCode) {
                                        // Code to be executed when an ad request fails.
                                    }

                                    @Override
                                    public void onAdOpened() {
                                        // Code to be executed when an ad opens an overlay that
                                        // covers the screen.
                                    }

                                    @Override
                                    public void onAdClicked() {
                                        // Code to be executed when the user clicks on an ad.
                                    }

                                    @Override
                                    public void onAdLeftApplication() {
                                        // Code to be executed when the user has left the app.
                                    }

                                    @Override
                                    public void onAdClosed() {
                                        // Code to be executed when the user is about to return
                                        // to the app after tapping on an ad.
                                    }
                                });
                            }

                            TextView txtAppName;
                            final TextView txtBrightnessLevelPreview;
                            ImageView imgAppIcon;
                            CardView viewSave;

                            viewSeekbarContainer = dialog.findViewById(R.id.viewSeekbarContainer);

                            txtAppName = dialog.findViewById(R.id.txtAppName);
                            imgAppIcon = dialog.findViewById(R.id.imgAppIcon);
                            viewSave = dialog.findViewById(R.id.viewSave);
                            modeNornalll = dialog.findViewById(R.id.modeNornalll);
                            modeVibratell = dialog.findViewById(R.id.modeVibratell);
                            modeSilentll = dialog.findViewById(R.id.modeSilentll);
                            rdModeNormal = dialog.findViewById(R.id.rdModeNormal);
                            rdModeVibrate = dialog.findViewById(R.id.rdModeVibrate);
                            rdModeSilent = dialog.findViewById(R.id.rdModeSilent);
                            seekbarRing = dialog.findViewById(R.id.seekbarRing);
                            seekbarMedia = dialog.findViewById(R.id.seekbarMedia);
                            seekbarAlarm = dialog.findViewById(R.id.seekbarAlarm);
                            seekbarVoiceCall = dialog.findViewById(R.id.seekbarVoiceCall);
                            seekbarNotification = dialog.findViewById(R.id.seekbarNotification);
                            txtAppName.setText(appList.get(i1).getAppLabel());
                            Picasso.get().load(appList.get(i1).getIcon()).placeholder(R.mipmap.ic_launcher).into(imgAppIcon);
                            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

                            seekbarRing.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
                            seekbarMedia.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                            seekbarAlarm.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM));
                            seekbarVoiceCall.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL));
                            seekbarNotification.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION));

                            rdModeNormal.setChecked(true);
                            seekbarRing.setProgress(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
                            seekbarMedia.setProgress(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) - 1);
                            seekbarAlarm.setProgress(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) - 1);
                            seekbarVoiceCall.setProgress(audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL) - 1);
                            seekbarNotification.setProgress(audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION));

                            modeNornalll.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    rdModeNormal.setChecked(true);
                                    rdModeVibrate.setChecked(false);
                                    rdModeSilent.setChecked(false);
                                    getOrientationMode = 0;
                                }
                            });

                            modeSilentll.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    rdModeNormal.setChecked(false);
                                    rdModeVibrate.setChecked(false);
                                    rdModeSilent.setChecked(true);
                                    getOrientationMode = 1;
                                }
                            });

                            modeVibratell.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    rdModeNormal.setChecked(false);
                                    rdModeVibrate.setChecked(true);
                                    rdModeSilent.setChecked(false);
                                    getOrientationMode = 2;
                                }
                            });

                            rdModeNormal.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    rdModeNormal.setChecked(true);
                                    rdModeVibrate.setChecked(false);
                                    rdModeSilent.setChecked(false);
                                    getOrientationMode = 0;
                                }
                            });

                            rdModeSilent.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    rdModeNormal.setChecked(false);
                                    rdModeVibrate.setChecked(false);
                                    rdModeSilent.setChecked(true);
                                    getOrientationMode = 1;
                                }
                            });

                            rdModeVibrate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    rdModeNormal.setChecked(false);
                                    rdModeVibrate.setChecked(true);
                                    rdModeSilent.setChecked(false);
                                    getOrientationMode = 2;
                                }
                            });

                            viewSave.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Utils.totalInterCount++;
                                    appList.get(i1).setMode(getOrientationMode);
                                    appList.get(i1).setRing(seekbarRing.getProgress());
                                    appList.get(i1).setMedia(seekbarMedia.getProgress());
                                    appList.get(i1).setAlarm(seekbarAlarm.getProgress());
                                    appList.get(i1).setVoiceCall(seekbarVoiceCall.getProgress());
                                    appList.get(i1).setNotification(seekbarNotification.getProgress());
                                    volumeManagerList.add(appList.get(i1));
                                    addToList();
                                    if(Utils.totalInterCount % Utils.timesInterAd == 0)
                                        showAdmobInter();
                                    else
                                        dialog.dismiss();
                                }
                            });

                            dialog.show();
                        } else {
                            for (int j = 0; j < volumeManagerList.size(); j++) {
                                if (volumeManagerList.get(j).getPkgName().equals(appList.get(i1).getPkgName()))
                                    volumeManagerList.remove(j);
                            }
                            addToList();
                        }
                    } else {
                        switchToggle(viewHolderNew);
//                        Toast.makeText(context, "Please turn on service", Toast.LENGTH_SHORT).show();
//                        viewHolderNew.toggle.setChecked(false);
                    }
                }
            });
//            ((ViewHolder1) viewHolder).toggle.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (Utils.isServiceOnVolume == 1) {
//                        if (viewHolderNew.toggle.isChecked()) {
//                            viewHolderNew.toggle.setChecked(true);
//                            dialog = new Dialog(context);
//                            dialog.setContentView(R.layout.configure_brightness_popup);
//                            dialog.setCanceledOnTouchOutside(false);
//                            dialog.setCancelable(false);
//                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//
//                            if (Utils.idLoad) {
//
//                                mAdView1 = dialog.findViewById(R.id.adView);
//                                AdRequest adRequest = new AdRequest.Builder().build();
//                                mAdView1.loadAd(adRequest);
//
//                                mAdView1.setAdListener(new AdListener() {
//                                    @Override
//                                    public void onAdLoaded() {
//                                    }
//
//                                    @Override
//                                    public void onAdFailedToLoad(int errorCode) {
//                                        // Code to be executed when an ad request fails.
//                                    }
//
//                                    @Override
//                                    public void onAdOpened() {
//                                        // Code to be executed when an ad opens an overlay that
//                                        // covers the screen.
//                                    }
//
//                                    @Override
//                                    public void onAdClicked() {
//                                        // Code to be executed when the user clicks on an ad.
//                                    }
//
//                                    @Override
//                                    public void onAdLeftApplication() {
//                                        // Code to be executed when the user has left the app.
//                                    }
//
//                                    @Override
//                                    public void onAdClosed() {
//                                        // Code to be executed when the user is about to return
//                                        // to the app after tapping on an ad.
//                                    }
//                                });
//                            }
//
//                            TextView txtAppName;
//                            final TextView txtBrightnessLevelPreview;
//                            ImageView imgAppIcon;
//                            CardView viewSave;
//
//
//                            viewSeekbarContainer = dialog.findViewById(R.id.viewSeekbarContainer);
//
//                            txtAppName = dialog.findViewById(R.id.txtAppName);
//                            imgAppIcon = dialog.findViewById(R.id.imgAppIcon);
//                            viewSave = dialog.findViewById(R.id.viewSave);
//                            modeNornalll = dialog.findViewById(R.id.modeNornalll);
//                            modeVibratell = dialog.findViewById(R.id.modeVibratell);
//                            modeSilentll = dialog.findViewById(R.id.modeSilentll);
//                            rdModeNormal = dialog.findViewById(R.id.rdModeNormal);
//                            rdModeVibrate = dialog.findViewById(R.id.rdModeVibrate);
//                            rdModeSilent = dialog.findViewById(R.id.rdModeSilent);
//                            seekbarRing = dialog.findViewById(R.id.seekbarRing);
//                            seekbarMedia = dialog.findViewById(R.id.seekbarMedia);
//                            seekbarAlarm = dialog.findViewById(R.id.seekbarAlarm);
//                            seekbarVoiceCall = dialog.findViewById(R.id.seekbarVoiceCall);
//                            seekbarNotification = dialog.findViewById(R.id.seekbarNotification);
//                            txtAppName.setText(appList.get(i1).getAppLabel());
//                            Picasso.get().load(appList.get(i1).getIcon()).placeholder(R.mipmap.ic_launcher).into(imgAppIcon);
//                            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//
//                            seekbarRing.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
//                            seekbarMedia.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
//                            seekbarAlarm.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM));
//                            seekbarVoiceCall.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL));
//                            seekbarNotification.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION));
//
//                            rdModeNormal.setChecked(true);
//                            seekbarRing.setProgress(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
//                            seekbarMedia.setProgress(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) - 1);
//                            seekbarAlarm.setProgress(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) - 1);
//                            seekbarVoiceCall.setProgress(audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL) - 1);
//                            seekbarNotification.setProgress(audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION));
//
//                            modeNornalll.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    rdModeNormal.setChecked(true);
//                                    rdModeVibrate.setChecked(false);
//                                    rdModeSilent.setChecked(false);
//                                    getOrientationMode = 0;
//                                }
//                            });
//
//                            modeSilentll.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    rdModeNormal.setChecked(false);
//                                    rdModeVibrate.setChecked(false);
//                                    rdModeSilent.setChecked(true);
//                                    getOrientationMode = 1;
//                                }
//                            });
//
//                            modeVibratell.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    rdModeNormal.setChecked(false);
//                                    rdModeVibrate.setChecked(true);
//                                    rdModeSilent.setChecked(false);
//                                    getOrientationMode = 2;
//                                }
//                            });
//
//                            rdModeNormal.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    rdModeNormal.setChecked(true);
//                                    rdModeVibrate.setChecked(false);
//                                    rdModeSilent.setChecked(false);
//                                    getOrientationMode = 0;
//                                }
//                            });
//
//                            rdModeSilent.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    rdModeNormal.setChecked(false);
//                                    rdModeVibrate.setChecked(false);
//                                    rdModeSilent.setChecked(true);
//                                    getOrientationMode = 1;
//                                }
//                            });
//
//                            rdModeVibrate.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    rdModeNormal.setChecked(false);
//                                    rdModeVibrate.setChecked(true);
//                                    rdModeSilent.setChecked(false);
//                                    getOrientationMode = 2;
//                                }
//                            });
//
//                            viewSave.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Utils.totalInterCount++;
//                                    appList.get(i1).setMode(getOrientationMode);
//                                    appList.get(i1).setRing(seekbarRing.getProgress());
//                                    appList.get(i1).setMedia(seekbarMedia.getProgress());
//                                    appList.get(i1).setAlarm(seekbarAlarm.getProgress());
//                                    appList.get(i1).setVoiceCall(seekbarVoiceCall.getProgress());
//                                    appList.get(i1).setNotification(seekbarNotification.getProgress());
//                                    volumeManagerList.add(appList.get(i1));
//                                    addToList();
//                                    if(Utils.totalInterCount % Utils.timesInterAd == 0)
//                                        showAdmobInter();
//                                    else
//                                        dialog.dismiss();
//                                }
//                            });
//
//                            dialog.show();
//                        } else {
//                            for (int j = 0; j < volumeManagerList.size(); j++) {
//                                if (volumeManagerList.get(j).getPkgName().equals(appList.get(i1).getPkgName()))
//                                    volumeManagerList.remove(j);
//                            }
//                            addToList();
//                        }
//                    } else {
//                        Toast.makeText(context, "Please turn on service", Toast.LENGTH_SHORT).show();
//                        viewHolderNew.toggle.setChecked(false);
//                    }
//                }
//            });

            for (int j = 0; j < volumeManagerList.size(); j++) {
//                Log.e("BRIGHT", volumeManagerList.get(j).getPkgName() + " " + j);
//                Log.e("BRIGHT1", appList.get(i).getPkgName() + " " + i);
                if (appList.get(i1).getPkgName().equals(volumeManagerList.get(j).getPkgName()))
                    ((ViewHolder1) viewHolder).toggle.setChecked(true);
//                else if(!volumeManagerList.contains(appList))
//                    ((ViewHolder1) viewHolder).toggle.setChecked(false);
            }
        }
    }

    public void switchToggle(final ViewHolder1 viewHolderNew){
        new CountDownTimer(1000,1000){

            @Override
            public void onTick(long l) {
                Toast.makeText(context, "Please turn on service", Toast.LENGTH_SHORT).show();
                viewHolderNew.toggle.setChecked(false);

            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    public void addToList() {
        Gson gson = new Gson();
        String json = gson.toJson(volumeManagerList);
        editor = shref.edit();
        editor.putString(key, json);
        editor.apply();
    }

    int AD_TYPE = 1, CONTENT_TYPE = 0;

    @Override
    public int getItemViewType(int position) {
        if (position > 0 && position % Utils.AdPos == 0 && Utils.idLoad) {
            return AD_TYPE;
        }
        return CONTENT_TYPE;
    }

    public int getItemCount() {
        int adPos = Utils.AdPos - 1;
        if (appList.size() > 0 && Utils.AdPos > 0 && appList.size() > Utils.AdPos) {
            additionalContent = appList.size() / adPos;
        }
        if (additionalContent != 0)
            if (appList.size() % additionalContent == 0)
                additionalContent--;
        return appList.size() + additionalContent;
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
                    dialog.dismiss();
                    initAdmobInter();
                }
            });

        } else {
            dialog.dismiss();
        }
    }

    public void initAdmobInter() {
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(context.getString(R.string.inter_id));
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
