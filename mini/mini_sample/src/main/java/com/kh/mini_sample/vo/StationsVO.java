package com.kh.mini_sample.vo;
import com.kh.mini_sample.connection.Common;
import lombok.Getter;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.json.JSONArray;

@Component
@Getter
public class StationsVO {

    public final static int STATION_SIZE = 775;

    static final Long INF = Long.MAX_VALUE;
    StationVO[] stations;
    ArrayList<ArrayList<NodeDistVO>> graphDist;
    ArrayList<ArrayList<NodeTransVO>> graphTrans;
    @Autowired
    public StationsVO(Common common)  {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(common);
        stations = new StationVO[STATION_SIZE];

        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
             Statement stationStatement = connection.createStatement();
             ResultSet stationResultSet = stationStatement.executeQuery("SELECT * FROM station");
             Statement timetableStatement = connection.createStatement();
             ResultSet timetableResultSet = timetableStatement.executeQuery("SELECT * FROM timetable")) {

            while (stationResultSet.next()) {
                int index = Integer.parseInt(stationResultSet.getString("ID"));
                String name = stationResultSet.getString("STIN_NM");
                String ln_cd = stationResultSet.getString("LN_CD");
                String stin_cd = stationResultSet.getString("STIN_CD");
                String[] splitEdge = stationResultSet.getString("EDGE") != null ?
                        stationResultSet.getString("EDGE").split(":") : new String[0];
                List<String> edge = Arrays.asList(splitEdge);

                String[] splitTrans = stationResultSet.getString("TRANS") != null ?
                        stationResultSet.getString("TRANS").split(":") : new String[0];
                List<String> trans = Arrays.asList(splitTrans);


                String[] splitTime = stationResultSet.getString("TIME") != null ? stationResultSet.getString("TIME").split(":") : new String[0];
                List<String> transTime = Arrays.asList(splitTime);

                stations[index] = new StationVO(index, ln_cd, name,stin_cd, edge, trans, transTime);
            }

            while (timetableResultSet.next()) {
                int index = Integer.parseInt(timetableResultSet.getString("ID"));
                String schedule = timetableResultSet.getString("schedule");

                // JSON 문자열을 파싱하여 JSONObject 또는 JSONArray로 변환
                JSONArray jsonArray = new JSONArray(schedule);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    String dayCd = jsonObject.get("dayCd").toString();
                    String arvTime = jsonObject.get("arvTm").toString().trim();
                    String arvTrnNo = jsonObject.get("trnNo").toString();
                    String dptTime = jsonObject.get("dptTm").toString().trim();
                    String dptTrnNo = jsonObject.get("trnNo").toString();
                    String dest = jsonObject.get("tmnStinCd").toString();
                    updateScheduleArray(index, stations, arvTime, dptTime, arvTrnNo, dptTrnNo, dayCd, dest);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //////////////////////////////////////////////////그래프 초기화문구
        graphDist = new ArrayList<>();
        graphTrans =  new ArrayList<>();
        for (int i = 0; i < STATION_SIZE; i++)  {
            graphTrans.add(new ArrayList<>());
            graphDist.add(new ArrayList<>());
        }

        addEdgesToGraph(graphDist, graphTrans, stations);
        ///////////////////////////////////////////////////////////////////////////
    }

    public void updateStations(Common common){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(common);

        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
             Statement stationStatement = connection.createStatement();
             ResultSet stationResultSet = stationStatement.executeQuery("SELECT * FROM station");
             Statement timetableStatement = connection.createStatement();
             ResultSet timetableResultSet = timetableStatement.executeQuery("SELECT * FROM timetable")) {

            while (stationResultSet.next()) {
                int index = Integer.parseInt(stationResultSet.getString("ID"));
                String name = stationResultSet.getString("STIN_NM");
                String ln_cd = stationResultSet.getString("LN_CD");
                String stin_cd = stationResultSet.getString("STIN_CD");

                String[] splitEdge = stationResultSet.getString("EDGE") != null ?
                        stationResultSet.getString("EDGE").split(":") : new String[0];
                List<String> edge = Arrays.asList(splitEdge);

                String[] splitTrans = stationResultSet.getString("TRANS") != null ?
                        stationResultSet.getString("TRANS").split(":") : new String[0];
                List<String> trans = Arrays.asList(splitTrans);


                String[] splitTime = stationResultSet.getString("TIME") != null ? stationResultSet.getString("TIME").split(":") : new String[0];
                List<String> transTime = Arrays.asList(splitTime);

                stations[index] = new StationVO(index, ln_cd, name,stin_cd, edge, trans, transTime);
            }

            while (timetableResultSet.next()) {
                int index = Integer.parseInt(timetableResultSet.getString("ID"));
                String schedule = timetableResultSet.getString("schedule");

                // JSON 문자열을 파싱하여 JSONObject 또는 JSONArray로 변환
                JSONArray jsonArray = new JSONArray(schedule);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    String dayCd = jsonObject.get("dayCd").toString();
                    String arvTime = jsonObject.get("arvTm").toString().trim();
                    String arvTrnNo = jsonObject.get("trnNo").toString();
                    String dptTime = jsonObject.get("dptTm").toString().trim();
                    String dptTrnNo = jsonObject.get("trnNo").toString();
                    String dest = jsonObject.get("tmnStinCd").toString();
                    updateScheduleArray(index, stations, arvTime, dptTime, arvTrnNo, dptTrnNo, dayCd, dest);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        addEdgesToGraph(graphDist, graphTrans, stations);
    }

    public void addEdgesToGraph(ArrayList<ArrayList<NodeDistVO>> graphDist, ArrayList<ArrayList<NodeTransVO>> graphTrans, StationVO[] stations) {
        for (int i = 0; i < stations.length; i++) {
            for (int j = 0; j < stations[i].getEdge().size(); j++) {
                String edgeStr = stations[i].getEdge().get(j);
                if (edgeStr.isEmpty()) {
                    // 빈 문자열인 경우 예외 처리
                    continue;
                }
                int next = Integer.parseInt(edgeStr);
                graphDist.get(i).add(new NodeDistVO(next, INF, 0));
                graphDist.get(next).add(new NodeDistVO(i, INF, 0));
                graphTrans.get(i).add(new NodeTransVO(next, INF, 0));
                graphTrans.get(next).add(new NodeTransVO(i, INF, 0));
            }
        }

        for (int i = 0; i < stations.length; i++) {
            for (int j = 0; j < stations[i].getTransfer().size(); j++) {
                String transferStr = stations[i].getTransfer().get(j);
                if (transferStr.isEmpty()) {
                    // 빈 문자열인 경우 예외 처리
                    continue;
                }
                int next = Integer.parseInt(transferStr);
                int time = Integer.parseInt(stations[i].getTransTime().get(j));
                time = time * 810;
                graphDist.get(i).add(new NodeDistVO(next, time, 0));
                graphDist.get(next).add(new NodeDistVO(i, time, 0));
                graphTrans.get(i).add(new NodeTransVO(next, time, 0));
                graphTrans.get(next).add(new NodeTransVO(i, time, 0));
            }
        }
    }
    public void updateScheduleArray(int index, StationVO[] stations, String arvTime, String dptTime, String arvTrnNo, String dptTrnNo, String dayCd,String dest) {
        if (dayCd.equals("8")) {
            updateScheduleHour(stations[index].getWeekArvTime(), arvTime, stations[index].getWeekArvTrnNo(),arvTrnNo);
            updateScheduleHour(stations[index].getWeekDptTime(), dptTime, stations[index].getWeekDptTrnNo(),dptTrnNo,stations[index].getWeekDest(),dest);
        } else if (dayCd.equals("9")) {
            updateScheduleHour(stations[index].getHoliArvTime(), arvTime, stations[index].getHoliArvTrnNo(),arvTrnNo);
            updateScheduleHour(stations[index].getHoliDptTime(), dptTime, stations[index].getHoliDptTrnNo(),dptTrnNo,stations[index].getHoliDest(),dest);
        }
    }

    public void updateScheduleHour(List<List<String>> scheduleHour, String time, List<List<String>> scheduleTrn,String trnNo) {
        if (time.length() >= 6) {
            int hour = Integer.parseInt(time.substring(0, 2));
            List<String> hourList = scheduleHour.get(hour);
            List<String> trnList = scheduleTrn.get(hour);

            // 새 값을 삽입할 위치의 인덱스를 찾습니다.
            int index = 0;
            while (index < hourList.size() && hourList.get(index).compareTo(time) <= 0) {
                index++;
            }
            // 올바른 위치에 새 값을 삽입합니다.
            hourList.add(index, time);
            trnList.add(index, trnNo);
        }
    }
    public void updateScheduleHour(List<List<String>> scheduleHour, String time, List<List<String>> scheduleTrn,String trnNo,List<List<String>> scheduleDest,String dest) {
        if (time.length() >= 6) {
            int hour = Integer.parseInt(time.substring(0, 2));
            List<String> hourList = scheduleHour.get(hour);
            List<String> trnList = scheduleTrn.get(hour);
            List<String> destList = scheduleDest.get(hour);
            // 새 값을 삽입할 위치의 인덱스를 찾습니다.
            int index = 0;
            while (index < hourList.size() && hourList.get(index).compareTo(time) <= 0) {
                index++;
            }
            // 올바른 위치에 새 값을 삽입합니다.
            hourList.add(index, time);
            trnList.add(index, trnNo);
            destList.add(index,dest);
        }
    }
}
