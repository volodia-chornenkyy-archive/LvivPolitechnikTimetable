package com.temnoi.lvivpolitechniktimetable;

/**
 * Created by Volodia on 19.10.2014.
 */
public class ItemData {
    private String title;
    private String number;

    public ItemData(String title,String number){

        this.title = title;
        this.number = number;
    }

    public String getTitle(){
        return title;
    }

    public String  getImageUrl(){
        return number;
    }

    public void setTitle(String title){
        this.title = title;
    }
    public void setImageUrl(String number){
        this.number = number;
    }
}
