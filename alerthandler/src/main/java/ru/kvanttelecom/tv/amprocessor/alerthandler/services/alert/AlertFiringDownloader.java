package ru.kvanttelecom.tv.amprocessor.alerthandler.services.alert;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.dreamworkerln.spring.utils.common.rest.RestClient;
import ru.kvanttelecom.tv.amprocessor.configz.prometheus.configurations.properties.PrometheusProperties;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.AlertStatus;
import ru.kvanttelecom.tv.amprocessor.core.services.downloader.DownloaderAbstract;

import javax.annotation.PostConstruct;
import java.util.List;

import static ru.kvanttelecom.tv.amprocessor.configz.prometheus.configurations.PrometheusBeanConfigurations.REST_TEMPLATE_PROMETHEUS;


/**
 * Get currently firing alerts
 */
@Service
@Slf4j
public class AlertFiringDownloader extends DownloaderAbstract {

    @Autowired
    PrometheusProperties promProps;

    @Autowired
    private AlertParser parser;

    public AlertFiringDownloader(@Qualifier(REST_TEMPLATE_PROMETHEUS) RestClient restClient) {
        super(restClient);
    }


    @PostConstruct
    private void init() {}

    /**
     * Get organization map from Watcher
     * @return List<String>
     */
    public List<Alert> getFiring() {

        List<Alert> result = null;

        ResponseEntity<String> resp = restClient.get(
            promProps.getProtocol() +
                promProps.getAddress() +
                PrometheusProperties.REST_API_GET_ALERTS_PATH);

        if(resp.hasBody()) {
            result = parser.parse(resp.getBody());
            result.removeIf(a -> a.getStatus() != AlertStatus.FIRING);
        }
        
        return result;
    }
    // -------------------------------------------------------------------------------

}
