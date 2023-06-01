package com.kh.mini_sample.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public abstract class NodeVO  {

    private int index;
    private long cost;
    private String trainN;

    private String isTrans;
    private int count;

    private Date dptTable;
    private String startDest;
    public NodeVO(){}

    public void setNext(String trainNumber,int transCount,long cost,String isTrans,Date dptTable,String startDest){
        setCost(cost);
        setCount(transCount);
        setTrainN(trainNumber);
        setIsTrans(isTrans);
        setDptTable(dptTable);
        setStartDest(startDest);
    }
    public void setNext(String trainNumber,int transCount,Date dptTable,String isTrans){
        setCount(transCount);
        setTrainN(trainNumber);
        setDptTable(dptTable);
        setIsTrans(isTrans);
    }

}

