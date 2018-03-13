package com.ty.entity;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

public class PropagateVO {
    private Integer id;
    private String topTitle;
    private MultipartFile topImg;
    private String mainDesc;
    private String title1;
    private MultipartFile img1;
    private String desc1;
    private String title2;
    private MultipartFile img2;
    private String desc2;
    private ArrayList isUpdateImg;


    public ArrayList getIsUpdateImg() {
        return isUpdateImg;
    }

    public void setIsUpdateImg(ArrayList isUpdateImg) {
        this.isUpdateImg = isUpdateImg;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTopTitle() {
        return topTitle;
    }

    public void setTopTitle(String topTitle) {
        this.topTitle = topTitle;
    }

    public MultipartFile getTopImg() {
        return topImg;
    }

    public void setTopImg(MultipartFile topImg) {
        this.topImg = topImg;
    }

    public String getMainDesc() {
        return mainDesc;
    }

    public void setMainDesc(String mainDesc) {
        this.mainDesc = mainDesc;
    }

    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public MultipartFile getImg1() {
        return img1;
    }

    public void setImg1(MultipartFile img1) {
        this.img1 = img1;
    }

    public String getDesc1() {
        return desc1;
    }

    public void setDesc1(String desc1) {
        this.desc1 = desc1;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public MultipartFile getImg2() {
        return img2;
    }

    public void setImg2(MultipartFile img2) {
        this.img2 = img2;
    }

    public String getDesc2() {
        return desc2;
    }

    public void setDesc2(String desc2) {
        this.desc2 = desc2;
    }

    @Override
    public String toString() {
        return "PropagateVO{" +
                "id=" + id +
                ", topTitle='" + topTitle + '\'' +
                ", topImg=" + topImg +
                ", mainDesc='" + mainDesc + '\'' +
                ", title1='" + title1 + '\'' +
                ", img1=" + img1 +
                ", desc1='" + desc1 + '\'' +
                ", title2='" + title2 + '\'' +
                ", img2=" + img2 +
                ", desc2='" + desc2 + '\'' +
                '}';
    }
}
