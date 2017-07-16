package com.musicdownloader.vimeodailymotiondownloader.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.musicdownloader.vimeodailymotiondownloader.view.DailyMotionView;

import javax.inject.Inject;

/**
 * Created by Hanh Nguyen on 7/16/2017.
 */

public class DMVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    DailyMotionView dailyMotionView;

    @Inject
    public DMVideoAdapter(){

    }

    public void setView(DailyMotionView view){
        this.dailyMotionView = view;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public int getPageCount(){
        return 0;
    }
}
