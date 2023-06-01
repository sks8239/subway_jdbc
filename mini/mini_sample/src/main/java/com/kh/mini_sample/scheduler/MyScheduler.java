package com.kh.mini_sample.scheduler;

import com.kh.mini_sample.Dao.StationDao;
import com.kh.mini_sample.Dao.TimetalbeDao;
import com.kh.mini_sample.connection.Common;
import com.kh.mini_sample.vo.StationsVO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

import static com.kh.mini_sample.Dao.CalenderDao.HOLIDAY;
import static com.kh.mini_sample.Dao.CalenderDao.WEEKDAY;
import static com.kh.mini_sample.vo.StationsVO.STATION_SIZE;

@Repository
public class MyScheduler {
    @Autowired
    StationsVO stationsVO;
    @Autowired
    private TimetalbeDao timetalbeDao;
    @Autowired
    private StationDao stationDao;

    @Autowired
    Common common;
//    @Scheduled(cron = "0 0 0 * * *") // 매일 0시 0분 0초에 실행
    public void updateMyDB() throws SQLException {
        List<List<String>> schedule = timetalbeDao.getTimetableAll();
        List<String> testUrl = stationDao.getURL();
        for (int i = 0; i < STATION_SIZE; i++) {
            URL url = null;
            HttpURLConnection con = null;

            for (int j = 8; j <= 9; j++) {
                JSONObject result = null;
                StringBuilder sb = new StringBuilder();
                JSONArray timeTable = null;
                try {
                    sb = new StringBuilder();
                    url = new URL(testUrl.get(i) + j);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Content-type", "application/json");
                    con.setDoOutput(true);

                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    con.disconnect();
                    Object jsonValue = new JSONTokener(sb.toString()).nextValue();

                    if (jsonValue instanceof JSONObject)   result = (JSONObject) jsonValue;   // JSON 객체 처리
                    if (result != null && result.has("body")) timeTable = (JSONArray) result.get("body");  // timeTable을 사용하는 로직

                    if (timeTable != null) {
                        switch (j) {
                            case 8:
                                if (timeTable.toString().equals(schedule.get(0).get(i))) System.out.println("일치함");
                                else timetalbeDao.updateTimetable(i,WEEKDAY,timeTable.toString());
                                break;
                            case 9:
                                if (timeTable.toString().equals(schedule.get(1).get(i))) System.out.println("일치함");
                                else timetalbeDao.updateTimetable(i,HOLIDAY,timeTable.toString());
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        stationsVO.updateStations(common);
    }

}
