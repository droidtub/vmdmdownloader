package com.musicdownloader.vimeodailymotiondownloader.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.musicdownloader.vimeodailymotiondownloader.R;
import com.musicdownloader.vimeodailymotiondownloader.entity.DmVideoEntity;
import com.musicdownloader.vimeodailymotiondownloader.presenter.DailyMotionPresenter;
import com.musicdownloader.vimeodailymotiondownloader.view.DailyMotionView;
import com.musicdownloader.vimeodailymotiondownloader.view.adapter.DMVideoAdapter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Hanh Nguyen on 7/8/2017.
 */

public class DailyMotionActivity extends BaseActivity implements DailyMotionView{

    @Inject DailyMotionPresenter dailyMotionPresenter;
    @Inject DMVideoAdapter adapter;

    @BindView(R.id.dailymotion_toolbar) Toolbar toolbar;
    @BindView(R.id.search_btn) ImageButton searchBtn;
    @BindView(R.id.videos_recyclerView) RecyclerView recyclerView;
    @BindView(R.id.search_box) EditText searchBox;

    private String queryText;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dailymotion);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);
        dailyMotionPresenter.setView(this);
        initView();
    }

    private void initView(){
        setSupportActionBar(toolbar);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter.setView(this);
        recyclerView.addOnScrollListener(scrollListener);
    }

    @OnClick(R.id.search_btn)
    public void searchVideo(){
        String text = searchBox.getText().toString();
        if(!text.equals("")) {
            queryText = text;
            dailyMotionPresenter.searchVideo(queryText, adapter.getPageCount());
            hideKeyboard();
        }
    }

    private void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
            int totalItemCount = recyclerView.getAdapter().getItemCount() - 1;
            if (dailyMotionPresenter.canLoadMore()
                    && lastVisibleItem >= totalItemCount - 10 && totalItemCount > 0 && dy > 0) {
                dailyMotionPresenter.loadMore(queryText, adapter.getPageCount());
            }
        }
    };

    @Override
    public void showLoadingView() {

    }

    @Override
    public void hideLoadingView() {

    }

    @Override
    public void setVideoPage(int page) {

    }

    @Override
    public void insertItem(DmVideoEntity entity) {

    }

    @Override
    public void loadThumbnailImage(ImageView view, String url) {

    }

    @Override
    public void clearList() {

    }
}

