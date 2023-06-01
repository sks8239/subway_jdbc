package com.kh.mini_sample.Dao;

import com.kh.mini_sample.connection.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Repository
public class TimetalbeDao {
    JdbcTemplate jdbcTemplate;
    @Autowired
    Common common;

    public String getTimetable(int id,String dayNm) throws SQLException {
        jdbcTemplate = new JdbcTemplate(common);

        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement timetableStatement = connection.prepareStatement("SELECT * FROM timetable WHERE id = ? AND dayNm = ?")){
             timetableStatement.setInt(1,id);
             timetableStatement.setString(2,dayNm);
            try( ResultSet timetableResultSet = timetableStatement.executeQuery()){
                String schedule =null;
                while (timetableResultSet.next()) {
                    schedule = timetableResultSet.getString("schedule");
                }
                return schedule;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<List<String>> getTimetableAll(){
        jdbcTemplate = new JdbcTemplate(common);
        List<String> WeekTable = new ArrayList<>();
        List<String> HolliTable = new ArrayList<>();

        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement timetableStatement = connection.prepareStatement("SELECT * FROM timetablebackup ORDER BY ID,DAYNM")){
            try( ResultSet timetableResultSet = timetableStatement.executeQuery()){
                while (timetableResultSet.next()) {
                    String day = timetableResultSet.getString("dayNm");
                    if (day.equals("평일")) WeekTable.add(timetableResultSet.getString("schedule"));
                    else HolliTable.add(timetableResultSet.getString("schedule"));
                }
                List<List<String>> schedule = new ArrayList<>();
                schedule.add(WeekTable);
                schedule.add(HolliTable);
                return schedule;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateTimetable(int i,String dayNm,String schedule){
        jdbcTemplate = new JdbcTemplate(common);
        System.out.println("업데이트중");
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement timetableStatement = connection.prepareStatement("UPDATE TIMETABLEBACKUP SET schedule= ? WHERE ID=? AND DAYNM = ?")){
            timetableStatement.setString(1, schedule);
            timetableStatement.setInt(2, i);
            timetableStatement.setString(3, dayNm);
            timetableStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
