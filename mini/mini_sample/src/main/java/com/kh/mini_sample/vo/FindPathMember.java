package com.kh.mini_sample.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Date;
import java.util.PriorityQueue;

import static com.kh.mini_sample.vo.StationsVO.INF;
import static com.kh.mini_sample.vo.StationsVO.STATION_SIZE;

@Getter
@Setter
public class FindPathMember {
    private boolean[] check; //방문한곳인지 확인
    private long[] dist; //역까지 걸리는 코스트저장
    private int[] prev; //경로저장
    private Date[] arvTm;//역에 도착한시간
    private Date[] dptTm;//역에서 출발하는시간
    private String[] trainNumber;//기차번호
    private int []totalTrans;//환승횟수
    private long waitTime;//기다리는시간
    private long durationTime;//이동하는데 걸리는시간
    private String[] isTrans; //환승한다면 "환승"저장됨

    private String[] startDest; //지금탄 기차의 출발목적지를저장해서 환승조건으로 비교

    private Date dptTable;//지금역에서 기차가 출발하는시간
    private Date arvTable;//다음역에 기차가 도착하는시간

    public FindPathMember(int startIndex) {
        this.check = new boolean[STATION_SIZE];
        this.dist = new long[STATION_SIZE];
        this.prev = new int[STATION_SIZE];
        this.arvTm = new Date[STATION_SIZE];
        this.dptTm = new Date[STATION_SIZE];
        this.trainNumber = new String[STATION_SIZE];
        this.totalTrans = new int[STATION_SIZE];
        this.waitTime = 0; //대기한 시간;
        this.durationTime = 0; // 소요 시간;
        this.isTrans = new String[STATION_SIZE]; //환승일경우 환승표시String
        this.startDest = new String[STATION_SIZE]; //출발도착역으로 환승여부확인
        this.dptTable = null; //열차도착시간표
        this.arvTable = null; //열차출발시간표
        Arrays.fill(startDest,"");
        Arrays.fill(isTrans,"");
        Arrays.fill(totalTrans,Integer.MAX_VALUE);
        Arrays.fill(trainNumber,"");
        Arrays.fill(dist, INF);
        Arrays.fill(prev, -1); // -1은 경로 상 이전 정점이 없다는 것을 나타냅니다.
        Arrays.fill(dptTm,new Date(INF));
        this.totalTrans[startIndex] = 0;
        this.dist[startIndex] = 0;
        this.arvTm[startIndex] = new Date();
        this.arvTm[startIndex].setTime(arvTm[startIndex].getTime()-(arvTm[startIndex].getTime()%1000));
        this.dptTm[startIndex] = new Date();
        this.dptTm[startIndex].setTime(dptTm[startIndex].getTime()-(dptTm[startIndex].getTime()%1000));

    }
    public String getStartDest(int index){ return this.startDest[index];}
    public void setStartDest(int index,String startDest){
        this.startDest[index] = startDest;
    }

    public String getIsTrans(int index) { return this.isTrans[index];}

    public void setIsTrans(int index,String isTrans){ this.isTrans[index] = isTrans;}

    public boolean getCheck(int index) {
        return check[index];
    }

    public void setCheck(int index,boolean isCheck) {
        this.check[index] = isCheck;
    }

    public long getDist(int index) {
        return dist[index];
    }

    public void setDist(int index,long dist) {
        this.dist[index] = dist;
    }

    public int getPrev(int index) {
        return prev[index];
    }

    public void setPrev(int index,int prev) {
        this.prev[index] = prev;
    }

    public Date getArvTm(int index) {
        return arvTm[index];
    }

    public void setArvTm(int index,Date arvTm) {
        this.arvTm[index] = arvTm;
    }

    public Date getDptTm(int index) {
        return dptTm[index];
    }

    public void setDptTm(int index,Date dptTm) {
        this.dptTm[index] = dptTm;
    }

    public String getTrainNumber(int index) {
        return trainNumber[index];
    }

    public void setTrainNumber(int index,String trainNumber) {
        this.trainNumber[index] = trainNumber;
    }

    public int getTotalTrans(int index) {
        return totalTrans[index];
    }

    public void setTotalTrans(int index,int totalTrans) {
        this.totalTrans[index] = totalTrans;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    public long getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(long durationTime) {
        this.durationTime = durationTime;
    }

    public Date getDptTable() {
        return dptTable;
    }

    public void setDptTable(Date dptTable) {
        this.dptTable = dptTable;
    }

    public Date getArvTable() {
        return arvTable;
    }

    public void setArvTable(Date arvTable) {
        this.arvTable = arvTable;
    }

    public void setFindPathMember(PriorityQueue<NodeTransVO> pq, NodeTransVO next, int nowVertex){
        setIsTrans(next.getIndex(),next.getIsTrans());
        setDptTm(next.getIndex(),next.getDptTable());
        setTotalTrans(next.getIndex(),getTotalTrans(nowVertex)+ next.getCount());
        setTrainNumber(next.getIndex(), next.getTrainN());
        setStartDest(next.getIndex(),next.getStartDest());
        setDist(next.getIndex(), getDist(nowVertex)+ next.getCost());
        setPrev(next.getIndex(), nowVertex); // 이전 정점 정보 갱신
        setArvTm(next.getIndex(),new Date((getArvTm(nowVertex).getTime() + next.getCost())));
        pq.offer(new NodeTransVO(next.getIndex(),getDist(next.getIndex()), getTotalTrans(next.getIndex())));
    }

    public void setFindPathMember(PriorityQueue<NodeDistVO> pq, NodeDistVO next, int nowVertex){
        setIsTrans(next.getIndex(),next.getIsTrans());
        setDptTm(next.getIndex(),next.getDptTable());
        setTotalTrans(next.getIndex(),getTotalTrans(nowVertex)+ next.getCount());
        setTrainNumber(next.getIndex(), next.getTrainN());
        setStartDest(next.getIndex(),next.getStartDest());
        setDist(next.getIndex(), getDist(nowVertex)+ next.getCost());
        setPrev(next.getIndex(), nowVertex); // 이전 정점 정보 갱신
        setArvTm(next.getIndex(),new Date((getArvTm(nowVertex).getTime() + next.getCost())));
        pq.offer(new NodeDistVO(next.getIndex(),getDist(next.getIndex()), getTotalTrans(next.getIndex())));
    }

}
