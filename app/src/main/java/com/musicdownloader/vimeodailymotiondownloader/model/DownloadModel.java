package com.musicdownloader.vimeodailymotiondownloader.model;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.musicdownloader.vimeodailymotiondownloader.entity.VideoEntity;

import javax.inject.Inject;

/**
 * Created by Hanh Nguyen on 7/10/2017.
 */

public class DownloadModel {
    private DownloadManager downloadManager;
    private long downloadId;
    private Context context;
    @Inject
    public DownloadModel(Context context){
        this.context = context;
        downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public void downloadVideo(VideoEntity entity){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(entity.getVideoUrl()));
        request.allowScanningByMediaScanner();
        downloadId = downloadManager.enqueue(request);

        /*DownloadMissionItem missionItem = new DownloadMissionItem(item);
        missionItem.missionId = downloadId;
        missionItem.name = name;
        missionItem.result = RESULT_DOWNLOADING;

        databaseModel.writeDownloadItem(missionItem);
        listener.onStartDownload();*/
    }

    public void loadPhoto(ImageView imageView, String thumbnailUrl){
        DrawableRequestBuilder<String> builder = Glide
                .with(context)
                .load(thumbnailUrl)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                });

        builder.into(imageView);
    }
}
