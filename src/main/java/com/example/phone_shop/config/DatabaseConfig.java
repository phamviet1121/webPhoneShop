package com.example.phone_shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");  // Driver MySQL
        dataSource.setUrl("jdbc:mysql://localhost:3306/phone_shop"); // URL cơ sở dữ liệu
        dataSource.setUsername("root");  // Tên người dùng cơ sở dữ liệu
        dataSource.setPassword("1121");  // Mật khẩu cơ sở dữ liệu

        return dataSource;
    }
     @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
