package com.kh.mini_sample.connection;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;


@Component
public class Common extends DriverManagerDataSource {
    // 오라클 설정 정보 (JDBC 연결)

    public Common(){
    this.setDriverClassName("org.mariadb.jdbc.Driver");
    this.setUrl("jdbc:mariadb://localhost:3306/subway");
    this.setUsername("root");
    this.setPassword("1234");
    }

}