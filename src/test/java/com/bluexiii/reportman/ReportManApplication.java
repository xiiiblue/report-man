package com.bluexiii.reportman;

import com.bluexiii.reportman.service.ReportManService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * Created by bluexiii on 17/10/2017.
 */
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@SpringBootApplication
public class ReportManApplication {
	@Autowired
    ReportManService reportManService;

	public static void main(String[] args) {
		SpringApplication.run(ReportManApplication.class, args);
	}

}
