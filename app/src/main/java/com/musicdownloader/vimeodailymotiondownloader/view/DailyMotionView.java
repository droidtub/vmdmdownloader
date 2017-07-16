package com.musicdownloader.vimeodailymotiondownloader.view;

import android.widget.ImageView;

import com.google.android.gms.ads.InterstitialAd;
import com.musicdownloader.vimeodailymotiondownloader.entity.DmVideoEntity;

/**
 * Created by Hanh Nguyen on 7/16/2017.
 */

public interface DailyMotionView {

    void showLoadingView();
    void hideLoadingView();
    void setVideoPage(int page);
    void insertItem(DmVideoEntity entity);
    void loadThumbnailImage(ImageView view, String url);
    void clearList();
}
