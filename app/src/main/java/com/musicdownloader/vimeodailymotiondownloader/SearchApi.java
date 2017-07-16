package com.musicdownloader.vimeodailymotiondownloader;

import com.musicdownloader.vimeodailymotiondownloader.entity.SearchVideoEntity;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Hanh Nguyen on 7/16/2017.
 */

public interface SearchApi {

    @GET("videos")
    Observable<SearchVideoEntity> searchVideos(@Query("search") String query,
                                               @Query("page") int page,
                                               @Query("limit") int per_page,
                                               @Query("fields") String fieldResponse);
}
