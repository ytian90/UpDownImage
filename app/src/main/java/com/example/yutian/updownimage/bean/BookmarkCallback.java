package com.example.yutian.updownimage.bean;

import java.util.List;

/**
 * Created by yutian on 5/19/16.
 */
public interface BookmarkCallback {
    void onSuccess(List<String> images);
    void onError();
}
