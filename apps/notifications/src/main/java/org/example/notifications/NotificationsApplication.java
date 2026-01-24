package org.example.notifications;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.example")
public class NotificationsApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationsApplication.class, args);
	}

}
