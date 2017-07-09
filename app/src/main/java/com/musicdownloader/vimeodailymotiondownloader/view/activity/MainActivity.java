package com.musicdownloader.vimeodailymotiondownloader.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.musicdownloader.vimeodailymotiondownloader.R;
import com.musicdownloader.vimeodailymotiondownloader.presenter.MainPresenter;
import com.musicdownloader.vimeodailymotiondownloader.view.MainView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Hanh Nguyen on 7/8/2017.
 */

public class MainActivity extends BaseActivity implements MainView{

    @Inject MainPresenter mainPresenter;

    @BindView(R.id.main_toolbar) Toolbar toolbar;
    @BindView(R.id.vimeo_fab) FloatingActionButton vimeoFab;
    @BindView(R.id.dm_fab) FloatingActionButton dmFab;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getActivityComponent().inject(this);
        mainPresenter.setView(this);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermissions(){
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(
                    new String[] {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permission, grantResult);
        for (int i = 0; i < permission.length; i ++) {
            switch (permission[i]) {
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    if (grantResult[i] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

    @OnClick(R.id.vimeo_fab)
    public void startVimeoActivity(){
        startActivity(new Intent(this, VimeoActivity.class));
    }

    @OnClick(R.id.dm_fab)
    public void startDailyMotionActivit(){
        startActivity(new Intent(this, DailyMotionActivity.class));
    }
}
