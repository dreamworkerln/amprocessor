package ru.kvanttelecom.tv.amprocessor.sgrabber.services.flussonic.downloaders;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.dreamworkerln.spring.utils.common.rest.RestClient;
import ru.kvanttelecom.tv.amprocessor.configz.watcher.configurations.properties.WatcherProperties;
import ru.kvanttelecom.tv.amprocessor.core.configurations.properties.CommonProperties;
import ru.kvanttelecom.tv.amprocessor.core.services.downloader.DownloaderAbstract;
import ru.kvanttelecom.tv.amprocessor.sgrabber.services.flussonic.parsers.WatcherOrganizationParser;

import javax.annotation.PostConstruct;
import java.util.Map;

import static ru.dreamworkerln.spring.utils.common.StringUtilsEx.formatMsg;
import static ru.kvanttelecom.tv.amprocessor.configz.watcher.configurations.WatcherSpringBeanConfigurations.REST_CLIENT_WATCHER;
import static ru.kvanttelecom.tv.amprocessor.utils.constants.Common.ITEMS_LIMIT;
import static ru.kvanttelecom.tv.amprocessor.utils.constants.Common.WATCHER_API_ORGANISATIONS_PATH;

/**
 *
 */

@Service
@Slf4j
public class WatcherOrganizationDownloader extends DownloaderAbstract {

    @Autowired
    WatcherProperties watcherProperties;
    @Autowired
    CommonProperties commonProperties;

    @Autowired
    WatcherOrganizationParser orgParser;


    public WatcherOrganizationDownloader(@Qualifier(REST_CLIENT_WATCHER) RestClient restClient) {
        super(restClient);
    }

    @PostConstruct
    private void init() {
        DOWNLOAD_ERROR = "Watcher download organisations error:";
        EMPTY_RESPONSE = "Response <Flussonic Watcher>: json<organisations> == empty";
    }

    @PostConstruct
    private void postConstruct() {}


    /**
     * Get organization map from Watcher
     * @return List<String>
     */
    public Map<Long,String> getAll() {

        Map<Long, String> result;

        String url = commonProperties.getProtocol() +
            watcherProperties.getAddress() +
            WATCHER_API_ORGANISATIONS_PATH +  ITEMS_LIMIT;


        ResponseEntity<String> resp = get(url);
        String body = resp.getBody();

        try {
            result = orgParser.getMap(body);
        }
        catch (Exception rethrow) {
            String message = formatMsg("Watcher parse organization error:" + " {}, {}", resp.getStatusCode(), body);
            throw new RuntimeException(message, rethrow);
        }
        
        return result;
    }
    // -------------------------------------------------------------------------------

}
