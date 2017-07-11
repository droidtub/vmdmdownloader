package com.musicdownloader.vimeodailymotiondownloader.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.musicdownloader.vimeodailymotiondownloader.R;
import com.musicdownloader.vimeodailymotiondownloader.entity.VideoEntity;
import com.musicdownloader.vimeodailymotiondownloader.presenter.VimeoPresenter;
import com.musicdownloader.vimeodailymotiondownloader.view.VimeoView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Hanh Nguyen on 7/8/2017.
 */

public class VimeoActivity extends BaseActivity implements VimeoView {

    @BindView(R.id.vimeo_toolbar) Toolbar toolbar;
    @BindView(R.id.webView) WebView webView;
    @BindView(R.id.adView) FrameLayout adView;
    @BindView(R.id.btn_download) FloatingActionButton downloadBtn;

    @Inject SharedPreferences sharedPreferences;
    @Inject VimeoPresenter vimeoPresenter;
    private String vimeoUrl = "https://vimeo.com";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vimeo);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);
        vimeoPresenter.setView(this);
    }

    @Override
    public void onStart(){
        super.onStart();
        initView();
    }

    private void initView(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //initBannerAds();
        initWebView();
    }

    private void initBannerAds(){
        AdView view = new AdView(this);
        view.setAdSize(AdSize.SMART_BANNER);
        //view.setAdUnitId(sharedPreferences.getString(getString(R.string.banner_id_key), ""));
        adView.addView(view);
        AdRequest adRequest = new AdRequest.Builder().build();
        view.loadAd(adRequest);
    }

    private void initWebView(){
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(), "Android");
        webView.loadUrl(vimeoUrl);
    }

    @Override
    public void startVimeoVideoActivity(ArrayList<VideoEntity> list) {
        Intent intent = new Intent(this, VimeoVideoActivity.class);
        intent.putParcelableArrayListExtra("video_list", list);
        startActivity(intent);
    }
    private class GetHTML extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String s = getHtmlText(params[0]);
            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("han.hanh", s);
        }
    }

    private String getHtmlText(String currentUrl){
        Document doc;
        String jsonString = "";
        try{
            doc = Jsoup.connect(currentUrl).get();
            String body = doc.getElementsByTag("script").text();
            Log.d("han.hanh", body);
            String startString = "\"progressive\":";
            String endString = "},\"lang\"";
            //int start = body.indexOf("\"progressive\":");
            //int end = body.indexOf("},\"lang\"");
            int start = body.indexOf(startString);
            Log.d("han.hanh", start + "");
            int end = body.indexOf(endString);
            Log.d("han.hanh", end + "");
            jsonString = body.substring(start + startString.length() - 1, end);
            //jsonString = body.substring(start + 12, end);
            Log.d("han.hanh",jsonString);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    @OnClick(R.id.btn_download)
    public void injectJS(){
        Log.d("han.hanh", webView.getUrl().substring(18));
        String currentUrl = "https://player.vimeo.com/video/" + webView.getUrl().substring(18);
        Log.d("han.hanh", currentUrl);


        new GetHTML().execute(currentUrl);
        //vimeoPresenter.injectJS(webView, "js/extractVideoUrls.js");
    }

    public class WebAppInterface{
        WebAppInterface(){
        }

        @JavascriptInterface
        public void addVideo(String videoUrl, String thumbnailUrl, String title){
            vimeoPresenter.addVideo(new VideoEntity(videoUrl, thumbnailUrl, title));
        }

        @JavascriptInterface
        public void showVimeoVideoActivity(){
            vimeoPresenter.startVimeoVideoActivity();
        }

        @JavascriptInterface
        public void showVimeoNoVideoDialog(){

        }
    }
}
