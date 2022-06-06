package ru.kvanttelecom.tv.amprocessor.sgrabber.services.flussonic.parsers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.amprocessor.configz.grafana.configurations.properties.GrafanaProperties;
import ru.kvanttelecom.tv.amprocessor.configz.watcher.configurations.properties.WatcherProperties;
import ru.kvanttelecom.tv.amprocessor.core.configurations.properties.CommonProperties;
import ru.kvanttelecom.tv.amprocessor.core.dto.camera.Camera;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static ru.dreamworkerln.spring.utils.common.StringUtilsEx.isBlank;
import static ru.dreamworkerln.spring.utils.common.Utils.throwIfNull;
import static ru.kvanttelecom.tv.amprocessor.utils.CommonUtils.getDomainName;
import static ru.kvanttelecom.tv.amprocessor.utils.constants.Common.*;

@Component
@Slf4j
public class WatcherStreamParser {



    @Autowired
    private GrafanaProperties grafanaProperties;
    @Autowired
    private WatcherProperties watcherProperties;
    @Autowired
    private CommonProperties commonProperties;
    @Autowired
    private ObjectMapper mapper;

    //@Autowired
    //private CameraDetailsService detailsService;

    public List<Camera> getArray(String json, Map<Long,String> organizations) {

        List<Camera> result = new ArrayList<>();
        JSONArray array = new JSONArray(json);
        // iterate over all streams from watcher
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            Camera dto = getCamera(obj, organizations);
            result.add(dto);
        }
        return result;
    }

//    public Optional<Camera> getOne(String json) {
//
//        JSONObject obj = new JSONObject(json);
//        return Optional.of(getStream(obj));
//    }


    // ------------------------------------------------------------------------

    // Get all flussonic media servers from DB
//    private void reloadServers() {
//        servers = serverService.findAll().stream()
//            .collect(Collectors.toMap(Server::getDomainName, Function.identity()));
//
//    }


    @SneakyThrows
    private Camera getCamera(JSONObject obj, Map<Long,String> organizations) {
        String name = obj.getString("name");
        String title = obj.optString("title");
        if(isBlank(title)) {
            title = name;
        }
        title = Camera.filterTitle(title);
        String comment = obj.optString("comment");
        String coordinatesString = obj.optString("coordinates");
        String postalAddress = obj.optString("postal_address");
        String instance = obj.getJSONObject("stream_status").getString("server");
        String domainName = getDomainName(instance);
        String hostname = domainName.split("\\.", 2)[0].toLowerCase();
        boolean alive = obj.getJSONObject("stream_status").optBoolean("alive", false);
        boolean enabled = obj.optBoolean("enabled", false);
        Long orgId =  obj.isNull("organization_id") ? null : obj.optLong("organization_id");
        String organization = organizations.get(orgId);
        Long agentId = obj.isNull("agent_id") ? null : obj.optLong("agent_id");
        String streamUrl = obj.optString("stream_url");

        throwIfNull(hostname, "Server " + hostname + "not found");


        String grafanaStreamUrl =
            commonProperties.getProtocol() +
                grafanaProperties.getAddress() +
                grafanaProperties.getStreamViewQuery() +
                URLEncoder.encode(title, StandardCharsets.UTF_8.toString());

        String watcherStreamUrl =
            commonProperties.getProtocol() +
                watcherProperties.getAddress() +
                watcherProperties.getCameraViewPath() +
                name;

        // Looks like flussonic mediaserver automatically redirect from http to https
        String mediaserverStreamUrl = commonProperties.getProtocol() + instance + MEDIASERVER_CAMERA_PATH + name;

        Map<String,String> referenceUrlMap = new LinkedHashMap<>();

        referenceUrlMap.put(GRAFANA_KEY, grafanaStreamUrl);
        referenceUrlMap.put(WATCHER_KEY, watcherStreamUrl);
        referenceUrlMap.put(MEDIASERVER_KEY, mediaserverStreamUrl);

        Camera result = new Camera(name, title, domainName, hostname, referenceUrlMap,
            comment, postalAddress, coordinatesString, null, organization, agentId, streamUrl);

        return result;
    }




}




/*

    String[] arr = coordinatesString.split(" ");
    Point p = null;
        if (arr.length == 2) {
            p = new Point(Double.parseDouble(arr[0]), Double.parseDouble(arr[1]));
            }
            Address address = new Address(postal_address, p);
*/


//    private CameraDetails getCameraDetails(JSONObject obj) {
//
//        CameraDetails result = null;
//
//        String comment = obj.optString("comment");
//        String name = obj.getString("name");
//
//        // Camera.config store data design:
//
//        // some-url
//        // \n
//        // \n
//        // \n
//        // \n
//        // {Escaped JSON}, that contains additional custom camera properties
//
//        try {
//            String[] tmp = comment.split("\\{", 2);
//            if (tmp.length == 2) {
//                String json = "{" + tmp[1];
//                result = mapper.readValue(StringEscapeUtils.unescapeJson(json), CameraDetails.class);
//            }
//        }
//        catch (Exception e) {
//            String msg = formatMsg("Parsing {} comment error: ", name);
//            log.warn(msg, e);
//        }
//
//        return result;
//    }