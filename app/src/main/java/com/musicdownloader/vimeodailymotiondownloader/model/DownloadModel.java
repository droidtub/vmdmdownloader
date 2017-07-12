package com.musicdownloader.vimeodailymotiondownloader.model;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicdownloader.vimeodailymotiondownloader.entity.VideoEntity;
import com.musicdownloader.vimeodailymotiondownloader.entity.VideoEntityJson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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

    public Observable<List<VideoEntityJson>> getVideoList(final String url){
        return Observable.fromCallable(new Callable<List<VideoEntityJson>>() {
            @Override
            public List<VideoEntityJson> call() throws Exception {
                Document doc;
                List<VideoEntityJson> videoList = new ArrayList<>();

                try{
                    doc = Jsoup.connect(url).get();
                    String bodyContent = doc.body().toString();

                    String regexStr = "\"progressive\":(.*)\\}\\,\\\"lang\\\"";
                    Matcher m = Pattern.compile(regexStr).matcher(bodyContent);
                    Log.d("han.hanh", m.group(1));
                    Gson gson = new Gson();
                    Type videoEntityJsonType = new TypeToken<ArrayList<VideoEntityJson>>(){}.getType();
                    videoList = gson.fromJson(m.group(1), videoEntityJsonType);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return videoList;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
