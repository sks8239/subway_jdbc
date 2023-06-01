package com.kh.mini_sample.Dao;

import com.kh.mini_sample.connection.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Repository
public class CalenderDao {
    JdbcTemplate jdbcTemplate;
    @Autowired
    Common common;
    public static final String WEEKDAY = "평일";
    public static final String HOLIDAY = "휴일";

    public List<Boolean> getTimetable() throws SQLException {
        jdbcTemplate = new JdbcTemplate(common);
        LocalDateTime today = LocalDateTime.now();
        List<Boolean> holidayInfo = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String todayStr = today.format(formatter);
        String currentDay = today.getDayOfWeek().toString();

        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement timetableStatement = connection.prepareStatement("SELECT locdate FROM calenders WHERE locdate = ?")){
            timetableStatement.setString(1,todayStr);
            try( ResultSet timetableResultSet = timetableStatement.executeQuery()){
               if(timetableResultSet.next()){
                   holidayInfo.add(true);
               }else{
                   if( currentDay.equals("SUNDAY") || currentDay.equals("SATURDAY")){
                       holidayInfo.add(true);
                   }else holidayInfo.add(false);
               }
            }
            LocalDateTime tomorrow = today.plusDays(1);
            String tomorrowStr = tomorrow.format(formatter);
            timetableStatement.setString(1, tomorrowStr);
            currentDay = tomorrow.getDayOfWeek().toString();

            try (ResultSet resultSet = timetableStatement.executeQuery()) {
                if (resultSet.next()) {
                    holidayInfo.add(true);
                } else {
                    if( currentDay.equals("SUNDAY") || currentDay.equals("SATURDAY")){
                        holidayInfo.add(true);
                    }else holidayInfo.add(false);
                }
            }
            LocalDateTime yesterday = today.minusDays(1);
            String yesterdayStr = yesterday.format(formatter);
            timetableStatement.setString(1, yesterdayStr);
            currentDay = yesterday.getDayOfWeek().toString();

            try (ResultSet resultSet = timetableStatement.executeQuery()) {
                if (resultSet.next()) {
                    holidayInfo.add(true);
                } else {
                    if( currentDay.equals("SUNDAY") || currentDay.equals("SATURDAY")){
                        holidayInfo.add(true);
                    }else holidayInfo.add(false);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return holidayInfo;
    }
}
