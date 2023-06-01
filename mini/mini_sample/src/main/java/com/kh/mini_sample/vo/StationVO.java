package com.kh.mini_sample.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class StationVO {
    private int index;
    private String ln_cd;
    private  String name;
    private String stin_cd;
    private List<String> transfer;
    private List<String> transTime;
    private List<String> edge;


    private List<List<String>> WeekArvTime;
    private List<List<String>> WeekDptTime;
    private List<List<String>> HoliArvTime;
    private List<List<String>> HoliDptTime;
    private List<List<String>> WeekArvTrnNo;
    private List<List<String>> WeekDptTrnNo;

    private List<List<String>> HoliArvTrnNo;
    private List<List<String>> HoliDptTrnNo;
    private List<List<String>> WeekDest;
    private List<List<String>> HoliDest;

    public StationVO(int index, String ln_cd, String name,String stin_cd,List<String> edge,List<String> transfer,List<String> transTime) {
        this.index = index;
        this.ln_cd = ln_cd;
        this.name = name;
        this.stin_cd = stin_cd;
        this.edge = edge;
        this.transTime = transTime;
        this.transfer = transfer;
        this.WeekArvTime = new ArrayList<>(); //시간이 index번호로 들어가고 분정보가 index안에 값
        this.WeekDptTime = new ArrayList<>();
        this.WeekArvTrnNo = new ArrayList<>();
        this.WeekDptTrnNo = new ArrayList<>();
        this.HoliArvTime = new ArrayList<>(); //시간이 index번호로 들어가고 분정보가 index안에 값
        this.HoliDptTime = new ArrayList<>();
        this.HoliArvTrnNo = new ArrayList<>();
        this.HoliDptTrnNo = new ArrayList<>();
        this.WeekDest = new ArrayList<>();
        this.HoliDest = new ArrayList<>();

        for (int i = 0; i < 24; i++) {
            WeekArvTime.add(new ArrayList<>());
            WeekDptTime.add(new ArrayList<>());
            WeekArvTrnNo.add(new ArrayList<>());
            WeekDptTrnNo.add(new ArrayList<>());

            HoliArvTime.add(new ArrayList<>());
            HoliDptTime.add(new ArrayList<>());
            HoliArvTrnNo.add(new ArrayList<>());
            HoliDptTrnNo.add(new ArrayList<>());

            WeekDest.add(new ArrayList<>());
            HoliDest.add(new ArrayList<>());
        }
    }


}