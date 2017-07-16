package com.musicdownloader.vimeodailymotiondownloader.entity;

import java.util.List;

/**
 * Created by Hanh Nguyen on 7/16/2017.
 */

public class SearchVideoEntity {
    public int page;
    public int limit;
    public boolean explicit;
    public int total;
    public boolean has_more;

    public List<DmVideoEntity> list;
}
