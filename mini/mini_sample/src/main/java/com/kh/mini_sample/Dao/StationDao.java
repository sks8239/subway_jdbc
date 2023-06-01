package com.kh.mini_sample.Dao;

import com.kh.mini_sample.connection.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StationDao {
    JdbcTemplate jdbcTemplate;
    @Autowired
    Common common;
    public List<String> getURL() throws SQLException {
        jdbcTemplate = new JdbcTemplate(common);

        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement timetableStatement = connection.prepareStatement("SELECT URL FROM station")){

            try( ResultSet timetableResultSet = timetableStatement.executeQuery()){
                List<String> url = new ArrayList<>();
                while (timetableResultSet.next()) {
                    url.add(timetableResultSet.getString("URL"));
                }
                return url;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
