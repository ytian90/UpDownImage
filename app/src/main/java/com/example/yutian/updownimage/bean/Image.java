package com.example.yutian.updownimage.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import javax.annotation.Generated;

/**
 * Created by yutian on 5/12/16.
 */
@Generated("org.jsonschema2pojo")
public class Image {

    @SerializedName("src")
    @Expose
    public String src;
    @SerializedName("width")
    @Expose
    public String width;
    @SerializedName("height")
    @Expose
    public String height;

    @Override
    public String toString() {
        return "Image{" +
                "src='" + src + '\'' +
                ", width='" + width + '\'' +
                ", height='" + height + '\'' +
                '}';
    }
}