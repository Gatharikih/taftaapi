package org.tafta.taftaapi.repo;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;

/**
 * @author Gathariki Ngigi
 * Created on April 27, 2023.
 * Time 0958h
 */
@Slf4j
@Component
public class DbConnectionsHandler {
    @Autowired
    private DataSource dataSource;

    public synchronized Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (Exception e) {
            throw new RuntimeException("Please try after sometime");
        }
    }

    /**
     * Kill all Db related configuration
     */
    public void destroyAllConnections(){
        log.info("*******************************************************************");
        log.info("*********** Destruction of All Db Connection **********************");

        dataSource.setPoolProperties(null);
        dataSource.close(true);

        log.info("*************************** DONE ***********************************");
        log.info("********************************************************************");
    }
}