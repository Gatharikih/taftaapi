package org.tafta.taftaapi;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.tafta.taftaapi.repo.DbConnectionsHandler;

//@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@SpringBootApplication
@Slf4j
public class TaftaapiApplication {
	@Autowired
	DbConnectionsHandler connectionsHandler;

	public static void main(String[] args) {
		SpringApplication.run(TaftaapiApplication.class, args);
	}

	/**
	 * On application exit
	 */
	@PreDestroy
	public void onExit() {
		try {
			connectionsHandler.destroyAllConnections(); // destroy all db connections
		} catch (Exception e) {
			log.error("ERROR WHILE EXITING THE APPLICATION: " + e.getMessage());
		}
	}
}