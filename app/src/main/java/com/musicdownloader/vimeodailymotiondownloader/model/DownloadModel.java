package com.musicdownloader.vimeodailymotiondownloader.model;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicdownloader.vimeodailymotiondownloader.R;
import com.musicdownloader.vimeodailymotiondownloader.entity.DownloadMissionItem;
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

    public static final int RESULT_SUCCEED = 1;
    public static final int RESULT_FAILED = -1;
    public static final int RESULT_DOWNLOADING = 0;
    @IntDef({
            DownloadModel.RESULT_DOWNLOADING,
            DownloadModel.RESULT_SUCCEED,
            DownloadModel.RESULT_FAILED})
    public @interface DownloadResultRule {}

    public static String DOWNLOAD_PATH = "/Download/DownTubeVideos/";
    private DownloadManager downloadManager;
    private long downloadId;
    private Context context;

    private static DatabaseModel databaseModel;
    private static DownloadModel instance;
    @Inject SharedPreferences sharedPreferences;

    public static final DownloadModel getInstance(Context context){
        if(instance == null){
            synchronized (DownloadModel.class){
                if(instance == null)
                    instance = new DownloadModel(context);
            }
        }
        return instance;
    }

    @Inject
    public DownloadModel(Context context){
        this.context = context;
        downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        databaseModel = DatabaseModel.getInstance(context);
    }

    public void downloadVideo(String url, String name){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.allowScanningByMediaScanner();
        request.setDestinationInExternalPublicDir(DOWNLOAD_PATH, name);
        downloadId = downloadManager.enqueue(request);

        DownloadMissionItem missionItem = new DownloadMissionItem(downloadId, name, url);
        missionItem.result = RESULT_DOWNLOADING;

        databaseModel.writeDownloadItem(missionItem);
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

    public static void downloadFinish(Context context, long missionId){
        DownloadMissionItem item = databaseModel.readDownloadItem(missionId);
        if (DownloadModel.getInstance(context).isMissionSuccess(missionId)) {
            if (item != null) {
                downloadSuccess(context, item);
                DownloadModel.getInstance(context)
                        .updateMissionResult(context, item.missionId, DownloadModel.RESULT_SUCCEED);
            }
        } else if (item != null) {
            downloadFailed(context, item);
            DownloadModel.getInstance(context)
                    .updateMissionResult(context, item.missionId, DownloadModel.RESULT_FAILED);
        }
    }

    public boolean isMissionSuccess(long missionId){
        Cursor cursor = getMissionCursor(missionId);
        if(cursor != null){
            int result = getDownloadResult(cursor);
            cursor.close();
            return  result == RESULT_SUCCEED;
        } else {
            return false;
        }
    }

    @Nullable
    private Cursor getMissionCursor(long missionId){
        Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(missionId));
        if(cursor == null){
            return null;
        } else if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            return cursor;
        } else {
            cursor.close();
            return null;
        }
    }

    private int getDownloadResult(Cursor cursor){
        switch (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            case DownloadManager.STATUS_SUCCESSFUL:
                return RESULT_SUCCEED;

            case DownloadManager.STATUS_FAILED:
            case DownloadManager.STATUS_PAUSED:
                return RESULT_FAILED;

            default:
                return RESULT_DOWNLOADING;
        }
    }

    public static void downloadSuccess(Context context, DownloadMissionItem item){

    }

    public static void downloadFailed(Context context, DownloadMissionItem item){

    }

    public static void updateMissionResult(Context context, Long missionId, int result){

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

                    String regexUrl = "\"progressive\":(.*)\\}\\,\\\"lang\\\"";
                    String regexTitle = "\\\"title\\\"\\:\\\"(.*?)\\\"\\,";
                    Matcher matcherUrl = Pattern.compile(regexUrl).matcher(bodyContent);
                    Matcher matcherTitle = Pattern.compile(regexTitle).matcher(bodyContent);
                    if(matcherUrl.find() && matcherTitle.find()) {
                        Gson gson = new Gson();
                        Type videoEntityJsonType = new TypeToken<ArrayList<VideoEntityJson>>(){}.getType();
                        videoList = gson.fromJson(matcherUrl.group(1), videoEntityJsonType);
                        sharedPreferences.edit().putString(context.getString(R.string.video_title_key), matcherTitle.group(1)).apply();
                    }
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
