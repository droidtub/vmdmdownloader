package com.musicdownloader.vimeodailymotiondownloader.presenter;

import com.musicdownloader.vimeodailymotiondownloader.entity.DmVideoEntity;
import com.musicdownloader.vimeodailymotiondownloader.entity.SearchVideoEntity;
import com.musicdownloader.vimeodailymotiondownloader.model.DownloadModel;
import com.musicdownloader.vimeodailymotiondownloader.view.DailyMotionView;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Hanh Nguyen on 7/16/2017.
 */

public class DailyMotionPresenter {

    @Inject DownloadModel downloadModel;
    private DailyMotionView dailyMotionView;
    private Boolean hasMore;
    private Boolean isLoading;

    @Inject
    public DailyMotionPresenter(){

    }

    public void setView(DailyMotionView view){
       this.dailyMotionView = view;
    }

    public boolean canLoadMore(){
        return !isLoading && hasMore;
    }

    public void searchVideo(String text, int page){
        requestPhotos(text, page, true);
        dailyMotionView.showLoadingView();
        setLoading(true);
    }

    public void loadMore(String text, int page){
        requestPhotos(text, page, false);
        setLoading(true);
    }

    public void setLoading(boolean loading){
        isLoading = loading;
    }

    public void requestPhotos(String query, int page, final boolean isFirst){
        final int newPage = isFirst ? 1 : page + 1;
        downloadModel.searchVideos(query, newPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SearchVideoEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull SearchVideoEntity searchVideoEntityList) {
                        if(isFirst) {
                            dailyMotionView.hideLoadingView();
                            dailyMotionView.clearList();
                        }
                        setLoading(false);
                        setCanLoadMore(searchVideoEntityList.has_more);
                        dailyMotionView.setVideoPage(newPage);
                        for(DmVideoEntity entity : searchVideoEntityList.list){
                            dailyMotionView.insertItem(entity);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    public void setCanLoadMore(boolean hasMore){
        this.hasMore = hasMore;
    }
}
