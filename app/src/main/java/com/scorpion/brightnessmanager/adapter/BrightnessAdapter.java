package com.scorpion.brightnessmanager.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jem.rubberpicker.RubberSeekBar;
import com.scorpion.brightnessmanager.activity.BrightnessManagerActivity;
import com.scorpion.brightnessmanager.model.BrightnessModel;
import com.scorpion.brightnessmanager.FBInterstitial;
import com.scorpion.brightnessmanager.R;
import com.scorpion.brightnessmanager.Utils;
import com.scorpion.brightnessmanager.adutils.AdHelper;
import com.squareup.picasso.Picasso;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import static com.scorpion.brightnessmanager.activity.BrightnessManagerActivity.brightnessManagerList;

public class BrightnessAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public ArrayList<BrightnessModel> appList;
    private Activity context;
    SharedPreferences shref;
    SharedPreferences.Editor editor;
    String key = "brightnessManagerList";
    int k = 0;
    int getCurrentProgress;
    boolean isAutoBrightnessChecked = false;
    float MaxBrightness;
    int additionalContent = 0;
    int flag = 0;
    Dialog dialog;
    AdView adView;

    class ADHolder extends RecyclerView.ViewHolder {


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

    public BrightnessAdapter(Activity context2, ArrayList<BrightnessModel> arrayList) {
        this.context = context2;
        this.appList = arrayList;

        shref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String response = shref.getString(key, "");

        if (gson.fromJson(response, new TypeToken<List<BrightnessModel>>() {
        }.getType()) != null)
            brightnessManagerList = gson.fromJson(response, new TypeToken<List<BrightnessModel>>() {
            }.getType());

        MaxBrightness = shref.getFloat("MaxBrightness", 255);
        if (Utils.idLoad)
            initAdmobInter();
    }

    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == CONTENT_TYPE) {
            return new ViewHolder1(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_app, viewGroup, false));
        } else {
            return new ADHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_ad, viewGroup, false));
        }
    }

    private int getRealPosition(int position) {
        if (Utils.AdPos == 0) {
            return position;
        } else {
            return (position - position / Utils.AdPos);
        }
    }

    LinearLayout viewSeekbarContainer;

    LinearLayout layBanner;

    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {

        if (getItemViewType(i) == AD_TYPE) {
            if (Utils.idLoad) {

//                AdHelper.AdLoadHelperAdmob(context, ((ADHolder) viewHolder).adView);
                AdHelper.AdLoadHelper(context, adView, layBanner);
            }
        } else {

            final int i1 = getRealPosition(i);
            final ViewHolder1 viewHolderNew = (ViewHolder1) viewHolder;
            Glide.with(context).asBitmap().load(Uri.parse(((BrightnessModel) appList.get(i1)).getIcon())).thumbnail(0.5f).into(((ViewHolder1) viewHolder).im_icon_app);
            ((ViewHolder1) viewHolder).tv_app_name.setText(appList.get(i1).getAppLabel());
            ((ViewHolder1) viewHolder).tv_app_package.setText(appList.get(i1).getPkgName());

//            ((ViewHolder1) viewHolder).toggle.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                }
//            });

            ((ViewHolder1) viewHolder).toggle.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                    MaxBrightness = shref.getFloat("MaxBrightness", 200);
                    if (Utils.isServiceOnBrightness == 1) {
                        if (viewHolderNew.toggle.isChecked()) {
                            viewHolderNew.toggle.setChecked(true);
                            dialog = new Dialog(context);
                            dialog.setContentView(R.layout.configure_brightness_popup);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.setCancelable(false);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                            if (Utils.idLoad) {
                                adView = dialog.findViewById(R.id.adView);
                                layBanner = dialog.findViewById(R.id.banner_container);
                                AdHelper.AdLoadHelper(context, adView, layBanner);
                            }

                            TextView txtAppName;
                            final TextView txtBrightnessLevelPreview;
                            ImageView imgAppIcon;
                            CardView viewSave;
                            SwitchButton chkAutoBrightness;

                            RubberSeekBar seekbarBrightnessLevel;

                            viewSeekbarContainer = dialog.findViewById(R.id.viewSeekbarContainer);

                            txtAppName = dialog.findViewById(R.id.txtAppName);
                            txtBrightnessLevelPreview = dialog.findViewById(R.id.txtBrightnessLevelPreview);
                            imgAppIcon = dialog.findViewById(R.id.imgAppIcon);
                            viewSave = dialog.findViewById(R.id.viewSave);
                            chkAutoBrightness = dialog.findViewById(R.id.chkAutoBrightness);
                            seekbarBrightnessLevel = dialog.findViewById(R.id.seekbarBrightnessLevel);
                            seekbarBrightnessLevel.setMax((int) Math.round(MaxBrightness));
                            seekbarBrightnessLevel.setCurrentValue((int) (MaxBrightness / 2));
                            txtAppName.setText(appList.get(i1).getAppLabel());
                            Picasso.get().load(appList.get(i1).getIcon()).placeholder(R.mipmap.ic_launcher).into(imgAppIcon);
                            viewSave.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Utils.totalInterCount++;
                                    brightnessManagerList.add(appList.get(i1));
                                    addToList();
                                    appList.get(i1).setBrightnessValue(getCurrentProgress);
                                    appList.get(i1).setAutoBrightness(isAutoBrightnessChecked);
                                    dialog.dismiss();
                                    addToList();
                                    Log.e("TOTAL123", String.valueOf(Utils.totalInterCount));
                                    Log.e("TOTAL123456", String.valueOf(Utils.timesInterAd));
                                    if (Utils.totalInterCount % Utils.timesInterAd == 0)
                                        showAdmobInter();
                                    else
                                        dialog.dismiss();
                                }
                            });

                            chkAutoBrightness.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                                    if (isChecked) {
                                        isAutoBrightnessChecked = true;
                                        viewSeekbarContainer.setVisibility(View.GONE);
                                    } else {
                                        isAutoBrightnessChecked = false;
                                        viewSeekbarContainer.setVisibility(View.VISIBLE);
                                    }
                                }
                            });

                            seekbarBrightnessLevel.setOnRubberSeekBarChangeListener(new RubberSeekBar.OnRubberSeekBarChangeListener() {
                                @Override
                                public void onProgressChanged(RubberSeekBar rubberSeekBar, int i, boolean b) {
                                    txtBrightnessLevelPreview.setText(String.valueOf(i));
                                    getCurrentProgress = i;
                                }

                                @Override
                                public void onStartTrackingTouch(RubberSeekBar rubberSeekBar) {

                                }

                                @Override
                                public void onStopTrackingTouch(RubberSeekBar rubberSeekBar) {

                                }
                            });

                            dialog.show();
                        } else {
                            for (int j = 0; j < brightnessManagerList.size(); j++) {
                                if (brightnessManagerList.get(j).getPkgName().equals(appList.get(i1).getPkgName()))
                                    brightnessManagerList.remove(j);
                            }
                            addToList();
                        }
                    } else {


                        switchToggle(viewHolderNew);

//                        if (viewHolderNew.toggle.isChecked())
//                            viewHolderNew.toggle.setChecked(!viewHolderNew.toggle.isChecked());
                    }
                }
            });

//            ((ViewHolder1) viewHolder).toggle.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    MaxBrightness = shref.getFloat("MaxBrightness", 200);
//                    if (Utils.isServiceOn == 1) {
//                        if (viewHolderNew.toggle.isChecked()) {
//                            viewHolderNew.toggle.setChecked(true);
//                            dialog = new Dialog(context);
//                            dialog.setContentView(R.layout.configure_brightness_popup);
//                            dialog.setCanceledOnTouchOutside(false);
//                            dialog.setCancelable(false);
//                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//
//                            if (Utils.idLoad) {
//                                adView = dialog.findViewById(R.id.adView);
//                                layBanner = dialog.findViewById(R.id.banner_container);
//                                AdHelper.AdLoadHelper(context, adView, layBanner);
//                            }
//
//                            TextView txtAppName;
//                            final TextView txtBrightnessLevelPreview;
//                            ImageView imgAppIcon;
//                            CardView viewSave;
//                            ToggleButton chkAutoBrightness;
//
//                            RubberSeekBar seekbarBrightnessLevel;
//
//                            viewSeekbarContainer = dialog.findViewById(R.id.viewSeekbarContainer);
//
//                            txtAppName = dialog.findViewById(R.id.txtAppName);
//                            txtBrightnessLevelPreview = dialog.findViewById(R.id.txtBrightnessLevelPreview);
//                            imgAppIcon = dialog.findViewById(R.id.imgAppIcon);
//                            viewSave = dialog.findViewById(R.id.viewSave);
//                            chkAutoBrightness = dialog.findViewById(R.id.chkAutoBrightness);
//                            seekbarBrightnessLevel = dialog.findViewById(R.id.seekbarBrightnessLevel);
//                            seekbarBrightnessLevel.setMax((int) Math.round(MaxBrightness));
//                            seekbarBrightnessLevel.setCurrentValue((int) (MaxBrightness / 2));
//                            txtAppName.setText(appList.get(i1).getAppLabel());
//                            Picasso.get().load(appList.get(i1).getIcon()).placeholder(R.mipmap.ic_launcher).into(imgAppIcon);
//                            viewSave.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Utils.totalInterCount++;
//                                    brightnessManagerList.add(appList.get(i1));
//                                    addToList();
//                                    appList.get(i1).setBrightnessValue(getCurrentProgress);
//                                    appList.get(i1).setAutoBrightness(isAutoBrightnessChecked);
//                                    dialog.dismiss();
//                                    addToList();
//                                    Log.e("TOTAL123", String.valueOf(Utils.totalInterCount));
//                                    Log.e("TOTAL123456", String.valueOf(Utils.timesInterAd));
//                                    if (Utils.totalInterCount % Utils.timesInterAd == 0)
//                                        showAdmobInter();
//                                    else
//                                        dialog.dismiss();
//                                }
//                            });
//
//                            chkAutoBrightness.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                                @Override
//                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                                    if (isChecked) {
//                                        isAutoBrightnessChecked = true;
//                                        viewSeekbarContainer.setVisibility(View.GONE);
//                                    } else {
//                                        isAutoBrightnessChecked = false;
//                                        viewSeekbarContainer.setVisibility(View.VISIBLE);
//                                    }
//
//                                }
//                            });
//
//                            seekbarBrightnessLevel.setOnRubberSeekBarChangeListener(new RubberSeekBar.OnRubberSeekBarChangeListener() {
//                                @Override
//                                public void onProgressChanged(RubberSeekBar rubberSeekBar, int i, boolean b) {
//                                    txtBrightnessLevelPreview.setText(String.valueOf(i));
//                                    getCurrentProgress = i;
//                                }
//
//                                @Override
//                                public void onStartTrackingTouch(RubberSeekBar rubberSeekBar) {
//
//                                }
//
//                                @Override
//                                public void onStopTrackingTouch(RubberSeekBar rubberSeekBar) {
//
//                                }
//                            });
//
//                            dialog.show();
//                        } else {
//                            for (int j = 0; j < brightnessManagerList.size(); j++) {
//                                if (brightnessManagerList.get(j).getPkgName().equals(appList.get(i1).getPkgName()))
//                                    brightnessManagerList.remove(j);
//                            }
//                            addToList();
//                        }
//                    } else {
//                        Toast.makeText(context, "Please turn on service", Toast.LENGTH_SHORT).show();
//                        viewHolderNew.toggle.setChecked(false);
//                    }
//                }
//            });

            for (int j = 0; j < brightnessManagerList.size(); j++) {
                if (appList.get(i1).getPkgName().equals(brightnessManagerList.get(j).getPkgName()))
                    ((ViewHolder1) viewHolder).toggle.setChecked(true);
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
        String json = gson.toJson(brightnessManagerList);
        editor = shref.edit();
        editor.putString(key, json);
        editor.commit();
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

        if (Utils.AdmobFacebook == 2) {
            FBInterstitial.getInstance().displayFBInterstitial(context, new FBInterstitial.FbCallback() {
                public void callbackCall() {
                    dialog.dismiss();
                }

            });
        } else {
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
    }

    public void initAdmobInter() {
        mInterstitialAd = new com.google.android.gms.ads.InterstitialAd(context);
        mInterstitialAd.setAdUnitId(context.getString(R.string.inter_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.e("Admob", "The interstitial loaded.");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.e("Admob", "The interstitial wasn't loaded yet :" + errorCode);
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
