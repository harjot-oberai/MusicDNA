package com.sdsmdg.harjot.MusicDNA.fragments.AboutFragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdsmdg.harjot.MusicDNA.activities.HomeActivity;
import com.sdsmdg.harjot.MusicDNA.R;
import com.sdsmdg.harjot.MusicDNA.activities.SplashActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {

    ImageView githubLinkBtn, reviewLinkBtn, shareLink, backBtn;

    TextView fragTitle, openSourceLicense, versiontTextView;

    View bottomMarginLayout;

    PackageInfo pInfo;

    String versionName;
    int versionCode;

    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // To add underline effect in open source license textView
        SpannableString content = new SpannableString("view license");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);

        // Gets version and build number from package manager
        try {
            pInfo = view.getContext().getPackageManager().getPackageInfo(view.getContext().getPackageName(), 0);
            versionName = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        versiontTextView = (TextView) view.findViewById(R.id.about_version_text);

        openSourceLicense = (TextView) view.findViewById(R.id.about_license_text);
        openSourceLicense.setText(content);
        openSourceLicense.setTextColor(HomeActivity.themeColor);

        githubLinkBtn = (ImageView) view.findViewById(R.id.about_github_link);
        reviewLinkBtn = (ImageView) view.findViewById(R.id.about_rate_link);
        shareLink = (ImageView) view.findViewById(R.id.about_share_link);

        backBtn = (ImageView) view.findViewById(R.id.about_back_btn);
        fragTitle = (TextView) view.findViewById(R.id.about_fragment_title);

        bottomMarginLayout = view.findViewById(R.id.bottom_margin_layout);

        versiontTextView.setText("Version " + versionName);

        if (SplashActivity.tf4 != null) {
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
            fragTitle.setTypeface(com.sdsmdg.harjot.MusicDNA.activities.SplashActivity.tf4);
        }

        if (HomeActivity.isReloaded) {
            bottomMarginLayout.getLayoutParams().height = 0;
        }
        else {
            bottomMarginLayout.getLayoutParams().height = com.sdsmdg.harjot.MusicDNA.utilities.CommonUtils.dpTopx(65, getContext());
        }

        openSourceLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayOpenSourceLicenses();
            }
        });

        githubLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://github.com/harjot-oberai/MusicStreamer");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        reviewLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + view.getContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    Log.e("AboutFragment.java: " + Thread.currentThread().getStackTrace()[2].getLineNumber(), e.getMessage());
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + view.getContext().getPackageName())));
                }
            }
        });

        shareLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_application_text) +
                        " " + getResources().getString(R.string.music_dna_short_link));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
    }

    public void displayOpenSourceLicenses(){
        final AlertDialog alertDialog = new AlertDialog.Builder(this.getContext()).create();
        alertDialog.setTitle("Music DNA");
        alertDialog.setMessage(getResources().getString(R.string.license_text));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "view license", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getResources().getString(R.string.license_link))));
            }
        });
        alertDialog.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(HomeActivity.themeColor);
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(HomeActivity.themeColor);
            }
        });

        alertDialog.show();
    }

}