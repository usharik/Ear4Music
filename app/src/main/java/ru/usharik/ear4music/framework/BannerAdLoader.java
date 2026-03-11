package ru.usharik.ear4music.framework;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public final class BannerAdLoader {
    private BannerAdLoader() {
    }

    public static AdView loadAnchoredBanner(Activity activity,
                                            FrameLayout container,
                                            String adUnitId,
                                            AdView currentAdView) {
        destroy(container, currentAdView);
        if (adUnitId == null || adUnitId.trim().isEmpty()) {
            return null;
        }

        AdView adView = new AdView(activity);
        adView.setAdUnitId(adUnitId);
        adView.setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, getAdWidth(container)));

        container.removeAllViews();
        container.addView(adView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        adView.loadAd(new AdRequest.Builder().build());
        return adView;
    }

    public static void destroy(FrameLayout container, AdView adView) {
        if (adView == null) {
            return;
        }
        ViewGroup parent = (ViewGroup) adView.getParent();
        if (parent != null) {
            parent.removeView(adView);
        }
        adView.destroy();
        if (container != null) {
            container.removeAllViews();
        }
    }

    private static int getAdWidth(FrameLayout container) {
        int adWidthPixels = container.getWidth();
        if (adWidthPixels <= 0) {
            DisplayMetrics displayMetrics = container.getResources().getDisplayMetrics();
            adWidthPixels = displayMetrics.widthPixels;
        }
        float density = container.getResources().getDisplayMetrics().density;
        return Math.max(1, (int) (adWidthPixels / density));
    }
}