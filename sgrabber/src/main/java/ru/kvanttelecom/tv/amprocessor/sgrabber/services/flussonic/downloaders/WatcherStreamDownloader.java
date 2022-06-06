package ru.kvanttelecom.tv.amprocessor.sgrabber.services.flussonic.downloaders;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.dreamworkerln.spring.utils.common.rest.RestClient;
import ru.kvanttelecom.tv.amprocessor.configz.watcher.configurations.properties.WatcherProperties;
import ru.kvanttelecom.tv.amprocessor.core.configurations.properties.CommonProperties;
import ru.kvanttelecom.tv.amprocessor.core.dto.camera.Camera;
import ru.kvanttelecom.tv.amprocessor.core.services.downloader.DownloaderAbstract;
import ru.kvanttelecom.tv.amprocessor.sgrabber.services.flussonic.parsers.WatcherStreamParser;

import javax.annotation.PostConstruct;

import java.util.List;
import java.util.Map;

import static ru.dreamworkerln.spring.utils.common.StringUtilsEx.*;
import static ru.kvanttelecom.tv.amprocessor.configz.watcher.configurations.WatcherSpringBeanConfigurations.REST_CLIENT_WATCHER;
import static ru.kvanttelecom.tv.amprocessor.utils.constants.Common.ITEMS_LIMIT;
import static ru.kvanttelecom.tv.amprocessor.utils.constants.Common.WATCHER_API_CAMERAS_PATH;

/**
 *
 */

@Service
@Slf4j
public class WatcherStreamDownloader extends DownloaderAbstract {

    @Autowired
    WatcherProperties watcherProperties;
    @Autowired
    CommonProperties commonProperties;
    @Autowired
    WatcherStreamParser streamParser;

    @Autowired
    ObjectMapper mapper;

    public WatcherStreamDownloader(@Qualifier(REST_CLIENT_WATCHER) RestClient restClient) {
        super(restClient);
    }


    @PostConstruct
    private void init() {
        DOWNLOAD_ERROR = "Watcher download cameras error:";
        EMPTY_RESPONSE = "Response <Flussonic Watcher>: json<cameras> == empty";
    }


    /**
     * Get Cameras list from Watcher
     *
     * @return List<Camera>
     */
    public List<Camera> getAll(Map<Long, String> organizations) {

        List<Camera> result;

        // Watcher API cameras - get all
        String url = commonProperties.getProtocol() +
            watcherProperties.getAddress() +
            WATCHER_API_CAMERAS_PATH +
            ITEMS_LIMIT;

        ResponseEntity<String> resp = get(url);
        String body = resp.getBody();

        try {
            result = streamParser.getArray(body, organizations);
        } catch (Exception rethrow) {
            String message = formatMsg("Watcher parse streams error:" + " {}, {}", resp.getStatusCode(), body);
            throw new RuntimeException(message, rethrow);
        }

        return result;
    }
}



//    /**
//     * Get one camera from Watcher
//     * @return Optional<Camera>
//     */
//    public Optional<Camera> getOne(StreamKey streamKey) {
//        Optional<Camera> result;
//
//
//        String url = props.getProtocol() +
//            props.getWatcher().getAddress() +
//            "/vsaas/api/v2/cameras/" + streamKey.getName();
//
//        ResponseEntity<String> resp = super.get(url);
//        String body = resp.getBody();
//
//        try {
//            result = streamParser.getOne(body);
//        }
//        catch (Exception rethrow) {
//            String message = formatMsg("Watcher parse camera error:" + " {}, {}", resp.getStatusCode(), body);
//            throw new RuntimeException(message, rethrow);
//        }
//
//        return result;
//    }








    // -------------------------------------------------------------------------------






//    /**
//     * WARN NOT USED
//     */
//    private String login() {
//
//        String result;
//
//        JSONObject login = new JSONObject();
//
//        String username = watcherProperties.getUsername();
//        String password = watcherProperties.getPassword();
//
//        login.put("login", username);
//        login.put("password", password);
//        ResponseEntity<String> resp = null;
//        String body = null;
//
//        try {
//
//            String loginUrl = watcherProperties.getAddress();
//            log.debug("POST: {}", loginUrl);
//            resp = restClient.post(loginUrl, login.toString());
//
//            body = resp.hasBody() ? resp.getBody() : null;
//            throwIfBlank(body, "Response <Flussonic Watcher>: json<login> == empty");
//
//            Assert.notNull(body, "body == null");
//            JSONObject response = new JSONObject(body);
//            result = response.getString("session");
//        }
//        catch (Exception rethrow) {
//            String message = "Watcher authentication error: ";
//            if(resp != null) {
//                message += formatMsg("Watcher authentication error:" + " {}, {}", resp.getStatusCode(), body);
//            }
//            throw new RuntimeException(message, rethrow);
//        }
//        return result;
//    }
//}
//
//
//
//    public void saveStreamComment(Camera camera) {
//
//        try {
//            // Watcher API camera
//            String url = commonProperties.getProtocol() +
//                watcherProperties.getAddress() +
//                WATCHER_API_CAMERAS_PATH +
//                camera.getName();
//
//            JSONObject json = new JSONObject();
//
//            // Trash
////            String trash = commonProperties.getProtocol() +
////                watcherProperties.getAddress() +
////                WATCHER_API_CAMERAS_PATH + camera.getName().toString() +
////                "\n\n\n\n\n\n\n\n\n\n" +
////                StringEscapeUtils.escapeJson(mapper.writeValueAsString(camera.getCameraProperties()));
//
//            String newUrl = commonProperties.getProtocol() + watcherProperties.getAddress() +
//                WATCHER_API_CAMERAS_PATH + camera.getName().toString();
//
//            // Replace to new comment
//            json.put("comment", newUrl);
//
//            // put to watcher
//            ResponseEntity<String> resp = put(url, json.toString());
//            log.debug("PUT response: {}", resp);
//        }
//        catch (Exception skip) {
//            log.error("Can't update Camera comment in Watcher: ", skip);
//        }
//    }
