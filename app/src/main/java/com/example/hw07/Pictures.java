/*

Assignment: Homework07
Group: B8
Group Members:
Anisha Kakwani
Hiten Changlani
 */
package com.example.hw07;

import java.io.Serializable;
import java.util.ArrayList;

public class Pictures implements Serializable {
    String name;
    String userID;
    String photoref;
    int noOflikes;
    String dateValue;
    String id;
    ArrayList<String> likeBy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPhotoref() {
        return photoref;
    }

    public void setPhotoref(String photoref) {
        this.photoref = photoref;
    }

    public int getNoOflikes() {
        return noOflikes;
    }

    public void setNoOflikes(int noOflikes) {
        this.noOflikes = noOflikes;
    }

    public String getDateValue() {
        return dateValue;
    }

    public void setDateValue(String dateValue) {
        this.dateValue = dateValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getLikeBy() {
        return likeBy;
    }

    public void setLikeBy(ArrayList<String> likeBy) {
        this.likeBy = likeBy;
    }


}
