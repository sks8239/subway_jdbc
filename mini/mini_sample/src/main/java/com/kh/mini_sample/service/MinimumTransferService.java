package com.kh.mini_sample.service;

import com.kh.mini_sample.vo.*;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

import static com.kh.mini_sample.service.MinimumTimeService.*;

@Service
public class MinimumTransferService {
    private final List<Date[]> arvTmList = new ArrayList<>();
    private final List<Date[]> dptTmList = new ArrayList<>();
    private final List<long[]> distList = new ArrayList<>();
    private final List<int[]> prevList = new ArrayList<>();
    private final List<Integer> shortest = new ArrayList<>();
    private final List<Integer> startList = new ArrayList<>();
    private final List<Integer> destList = new ArrayList<>();
    private final List<String[]> isTransList = new ArrayList<>();

    //노드의 크기, 출발지
    public PathVO Dijkstra(String start, String dest, StationVO[] stations, ArrayList<ArrayList<NodeTransVO>> orinGraph, List<Boolean> whatDayList) throws ParseException {
        removeList(arvTmList, dptTmList, distList, prevList, shortest, startList, destList, isTransList);
        Map<String, ArrayList<Integer>> stationMap = findStation(start, dest, stations);
        boolean whatDay;
        //시작역0번리스트부터
        for (int s = 0; s < stationMap.get(start).size(); s++) {
            int startIndex = stationMap.get(start).get(s);
            //도착역 0번리스트부터
            for (int d = 0; d < stationMap.get(dest).size(); d++) {
                ArrayList<ArrayList<NodeTransVO>> graph = deepCopy(orinGraph);
                PriorityQueue<NodeTransVO> pq = new PriorityQueue<>();
                pq.offer(new NodeTransVO(startIndex, 0, 0));
                FindPathMember findPathMember = new FindPathMember(startIndex);

                int destIndex = stationMap.get(dest).get(d);

                while (!pq.isEmpty()) {
                    int nowVertex = pq.poll().getIndex();
                    if (findPathMember.getCheck()[nowVertex]) continue;
                    findPathMember.setCheck(nowVertex, true);
                    for (NodeTransVO next : graph.get(nowVertex)) {
                        LocalDateTime localDateTime = LocalDateTime.now();
                        int currentHour = localDateTime.getHour(); //현재시간
                        String today = localDateTime.getYear() + String.format("%02d", localDateTime.getMonthValue()) + String.format("%02d", localDateTime.getDayOfMonth());
                        boolean isTransfer = stations[nowVertex].getTransfer().contains(String.valueOf(next.getIndex())) || stations[next.getIndex()].getTransfer().contains(String.valueOf(nowVertex));
                        String transAble = "";
                        int trainCheck = 0;
                        if (isTransfer) {
                            transAble = "환승";
                            findPathMember.setDptTable(findPathMember.getArvTm(nowVertex));
                            next.setNext(transAble, 1, findPathMember.getDptTable(), transAble);
                        } else {

                            for (int i = currentHour; i < currentHour + ONE_DAY; i++) {
                                whatDay = (i == 0) ? whatDayList.get(2) : (i == ONE_DAY) ? whatDayList.get(0) : (i >= 25) ? whatDayList.get(1) : whatDayList.get(0);
                                if (i == ONE_DAY) {
                                    today = String.valueOf(Integer.parseInt(today) + 1); // 하루 더해주고
                                }

                                boolean isHoliday = whatDay;
                                List<String> whatDayDpt = isHoliday
                                        ? stations[nowVertex].getHoliDptTime().get(i % ONE_DAY) : stations[nowVertex].getWeekDptTime().get(i % ONE_DAY);
                                List<String> whatDayDptTrn = isHoliday
                                        ? stations[nowVertex].getHoliDptTrnNo().get(i % ONE_DAY) : stations[nowVertex].getWeekDptTrnNo().get(i % ONE_DAY);
                                List<String> whatDayDest = isHoliday
                                        ? stations[nowVertex].getHoliDest().get(i % ONE_DAY) : stations[nowVertex].getWeekDest().get(i % ONE_DAY);
                                for (int j = 0; j < whatDayDpt.size(); j++) {
                                    int trainN = -1;
                                    int trans = 0;
                                    int isArv = 0;
                                    if(!whatDayDest.get(j).contains(stations[nowVertex].getStin_cd())){
                                        List<List<String>> whatDayArvTrn = isHoliday
                                                ? stations[nowVertex].getHoliArvTrnNo() : stations[nowVertex].getWeekArvTrnNo();
                                        int p = (i % ONE_DAY) == 0 ? ONE_DAY-1 : (i % ONE_DAY)-1;
                                        if(whatDayArvTrn.get(p++).contains(whatDayDptTrn.get(j))) isArv++;
                                        p = (p == ONE_DAY) ? 0 : p;
                                        if(whatDayArvTrn.get(p).contains(whatDayDptTrn.get(j))) isArv++;
                                        if(isArv==0) continue;
                                    }
                                    findPathMember.setDptTable(sdf.parse(today + whatDayDpt.get(j))); //열차가 출발하는 시간
                                    findPathMember.setWaitTime((findPathMember.getDptTable().getTime() - findPathMember.getArvTm(nowVertex).getTime())); //열차가 오는데 걸리는시간
                                    if (nowVertex != startIndex && !findPathMember.getStartDest(nowVertex).equals(whatDayDest.get(j))) {
                                        if ((!findPathMember.getTrainNumber(nowVertex).equals("환승")) && stations[nowVertex].getLn_cd().equals(stations[next.getIndex()].getLn_cd())) {
                                            findPathMember.setWaitTime(findPathMember.getDptTable().getTime() - (findPathMember.getArvTm(nowVertex).getTime() + WALK_TIME)); //열차가 오는데 걸리는시간에 같은역에서 다른열차로 갈아타는시간 WALK_TIME더함
                                            if (findPathMember.getWaitTime() >= 0) {
                                                transAble = "환승";
                                                findPathMember.setWaitTime(findPathMember.getWaitTime() + WALK_TIME);
                                                trans=1;
                                            }
                                        }
                                    }
                                    if (findPathMember.getWaitTime() < 0) continue;
                                    //열차가 떠난시간에서 다음역에 도착하는게 다음시간이라도 찾도록 열차를 확인하는코드
                                    for (int k = 0; k < 2; k++) {
                                        List<String> whatDayArv = isHoliday
                                                ? stations[next.getIndex()].getHoliArvTime().get((i + k) % ONE_DAY) : stations[next.getIndex()].getWeekArvTime().get((i + k) % ONE_DAY);
                                        List<String> whatDayArvTrn = isHoliday
                                                ? stations[next.getIndex()].getHoliArvTrnNo().get((i + k) % ONE_DAY) : stations[next.getIndex()].getWeekArvTrnNo().get((i + k) % ONE_DAY);
                                        trainN = whatDayArvTrn.indexOf(whatDayDptTrn.get(j)); //열차 방향확인 //다음시간도 고려해줘야함
                                        if (trainN != -1) {
                                            findPathMember.setArvTable(sdf.parse(today + whatDayArv.get(trainN)));
                                            if ((i % ONE_DAY) + k == ONE_DAY)
                                                findPathMember.setArvTable(sdf.parse((Integer.parseInt(today) + 1) + whatDayArv.get(trainN))); //만약다음날짜면 하루를더해주고 arvTable초기화
                                            break;
                                        }
                                    }
                                    if (trainN == -1) continue;

                                    findPathMember.setDurationTime(findPathMember.getArvTable().getTime() - findPathMember.getDptTable().getTime()); //이동 소요시간
                                    if (findPathMember.getDurationTime() >= 0) {
                                        trainCheck++;
                                        if (findPathMember.getWaitTime() + findPathMember.getDurationTime() < next.getCost()) {
                                            next.setNext(whatDayDptTrn.get(j), trans, findPathMember.getWaitTime() + findPathMember.getDurationTime(), transAble, findPathMember.getDptTable(), whatDayDest.get(j));
                                        }
                                    }
                                    if (trainCheck == 2) break;
                                }
                                if (trainCheck == 2) break; //
                            }
                        }
                        if (next.getCost() != INF) {
                            if (findPathMember.getTotalTrans(next.getIndex()) > findPathMember.getTotalTrans(nowVertex) + next.getCount()) {
                                findPathMember.setFindPathMember(pq, next, nowVertex);
                            } else if (findPathMember.getTotalTrans(next.getIndex()) == findPathMember.getTotalTrans(nowVertex) + next.getCount()) {
                                if (findPathMember.getDist(next.getIndex()) >= findPathMember.getDist(nowVertex) + next.getCost()) {
                                    findPathMember.setFindPathMember(pq, next, nowVertex);
                                }
                            }
                        }
                    }
                }
                SetPathList(startIndex, destIndex, findPathMember); //이번경로 리스트 저장
                //경로 비교후 최단거리가 아닌리스트 제거
                if (shortest.size() > 1) {
                    if (shortest.get(0) < shortest.get(1)) {
                        removeList(arvTmList, dptTmList, distList, prevList, shortest, startList, destList, 1, isTransList);
                    } else if (shortest.get(0).equals(shortest.get(1))) {
                        if (distList.get(0)[destList.get(0)] >= distList.get(1)[destList.get(1)]) {
                            removeList(arvTmList, dptTmList, distList, prevList, shortest, startList, destList, 0, isTransList);
                        } else
                            removeList(arvTmList, dptTmList, distList, prevList, shortest, startList, destList, 1, isTransList);
                    } else
                        removeList(arvTmList, dptTmList, distList, prevList, shortest, startList, destList, 0, isTransList);
                }
            }
        }

        if (distList.get(0)[destList.get(0)] == INF) {
            return null;
        } else {
            List<String> path = new ArrayList<>();
            addPath(prevList.get(0), destList.get(0), stations, arvTmList.get(0), path, dptTmList.get(0), destList.get(0), isTransList.get(0));
            return new PathVO("최소환승", (distList.get(0)[destList.get(0)] / 60 / 1000), path, arvTmList.get(0)[startList.get(0)], arvTmList.get(0)[destList.get(0)]); //소요시간,경로,출발시간,도착시간
        }

    }

    public void SetPathList(int startIndex, int destIndex, FindPathMember findPathMember) {
        startList.add(startIndex);
        destList.add(destIndex);
        shortest.add(findPathMember.getTotalTrans(destIndex));
        arvTmList.add(findPathMember.getArvTm());
        distList.add(findPathMember.getDist());
        prevList.add(findPathMember.getPrev());
        dptTmList.add(findPathMember.getDptTm());
        isTransList.add(findPathMember.getIsTrans());
    }


    // 경로 추가 함수
    public void addPath(int[] prev, int i, StationVO[] stations, Date[] arvTm, List<String> path, Date[] dptTm, Integer dest, String[] isTrans) {
        if (prev[i] != -1) {
            addPath(prev, prev[i], stations, arvTm, path, dptTm, dest, isTrans);
        }
        if (prev[i] == -1) {
            path.add("출발시간 : " + arvTm[i].toString());
            path.add(stations[i].getIndex() + " : " + stations[i].getName() + " : ");
        } else if (i == dest) {
            path.set(path.size() - 1, path.get(path.size() - 1) + dptTm[i].toString() + " " + isTrans[i]);
            path.add(stations[i].getIndex() + " : " + stations[i].getName() + " : " + arvTm[i].toString());
        } else {
            path.set(path.size() - 1, path.get(path.size() - 1) + dptTm[i].toString() + " " + isTrans[i]);
            path.add(stations[i].getIndex() + " : " + stations[i].getName() + " : ");
        }
    }

    public Map<String, ArrayList<Integer>> findStation(String start, String dest, StationVO[] stations) {
        ArrayList<Integer> stationS = new ArrayList<>();
        ArrayList<Integer> stationD = new ArrayList<>();
        for (int i = 0; i < stations.length; i++) {
            if (stations[i].getName().equals(start)) stationS.add(i);
            if (stations[i].getName().equals(dest)) stationD.add(i);
        }
        Map<String, ArrayList<Integer>> stationMap = new HashMap<>();
        stationMap.put(start, stationS);
        stationMap.put(dest, stationD);
        return stationMap;
    }

    public void removeList(List<Date[]> arvTmList, List<Date[]> dptTmList, List<long[]> distList, List<int[]> prevList, List<Integer> shortest, List<Integer> startList, List<Integer> destList, int i, List<String[]> isTransList) {
        arvTmList.remove(i);
        dptTmList.remove(i);
        distList.remove(i);
        prevList.remove(i);
        shortest.remove(i);
        startList.remove(i);
        destList.remove(i);
        isTransList.remove(i);
    }

    public void removeList(List<Date[]> arvTmList, List<Date[]> dptTmList, List<long[]> distList, List<int[]> prevList, List<Integer> shortest, List<Integer> startList, List<Integer> destList, List<String[]> isTransList) {
        arvTmList.clear();
        dptTmList.clear();
        distList.clear();
        prevList.clear();
        shortest.clear();
        startList.clear();
        destList.clear();
        isTransList.clear();
    }

    public ArrayList<ArrayList<NodeTransVO>> deepCopy(ArrayList<ArrayList<NodeTransVO>> graph) {
        ArrayList<ArrayList<NodeTransVO>> newGraph = new ArrayList<>();
        for (int i = 0; i < graph.size(); i++) {
            newGraph.add(new ArrayList<>());
            for (NodeTransVO node : graph.get(i)) {
                NodeTransVO newNode = new NodeTransVO(node.getIndex(), node.getCost(), node.getCount());
                newGraph.get(i).add(newNode);
            }
        }
        return newGraph;
    }

}