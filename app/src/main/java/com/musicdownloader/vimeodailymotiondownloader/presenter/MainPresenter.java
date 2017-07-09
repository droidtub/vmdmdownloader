package com.musicdownloader.vimeodailymotiondownloader.presenter;

import com.musicdownloader.vimeodailymotiondownloader.view.MainView;

import javax.inject.Inject;

/**
 * Created by Hanh Nguyen on 7/8/2017.
 */

public class MainPresenter {

    private MainView mainView;

    @Inject
    public MainPresenter(){

    }

    public void setView(MainView view){
        mainView = view;
    }
}
