package com.temnoi.lvivpolitechniktimetable;

/**
 * Created by Volodia on 19.10.2014.
 */
public class ItemData {
    private String title;
    private int imageUrl;

    public ItemData(String title,int imageUrl){

        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getTitle(){
        return title;
    }

    public int getImageUrl(){
        return imageUrl;
    }
}
