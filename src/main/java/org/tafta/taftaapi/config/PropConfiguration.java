package org.tafta.taftaapi.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author Gathariki Ngigi
 * Created on April 27, 2023.
 * Time 0908h
 *
 * All Enviroment properties and Enviroment variables configured are access through this class
 */

@Configuration
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PropConfiguration {
    @Value("${app.config.is_app_live}")
    private boolean isApplicationLive;

    @Value("${app.db.host}")
    private String dbHost ;
    @Value("${app.db.database}")
    private String database ;
    @Value("${app.db.username}")
    private String dbUsername ;
    @Value("${app.db.password}")
    private String dbPassword ;
    @Value("${app.db.port}")
    private String dbPort ;

    @Value("${app.services.switch}")
    private boolean switchService;
    @Value("${app.services.query}")
    private boolean queryService;

    // Services
    @Value("${app.services.switch}")
    private boolean isSwitchServiceEnabled;
    @Value("${app.services.query}")
    private boolean isQueryServiceEnabled;
    @Value("${app.services.notification}")
    private boolean isNotificationServiceEnabled;

    @Value("${app.config.files_path}")
    private String filesPath;
}
