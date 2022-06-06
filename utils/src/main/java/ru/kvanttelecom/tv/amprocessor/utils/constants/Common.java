package ru.kvanttelecom.tv.amprocessor.utils.constants;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.time.Instant;

public class Common {
    public static final Instant INSTANT_ZERO = Instant.parse("0001-01-01T00:00:00Z");

    public static final String GRAFANA_KEY = "grafana";
    public static final String PROMETHEUS_INSTANCE_KEY = "instance";
    public static final String WATCHER_KEY = "watcher";
    public static final String MEDIASERVER_KEY = "mediaserver";

    public static final String MEDIASERVER_CAMERA_PATH = "/admin/#/admin/standalone/";
    //public static final String WATCHER_CAMERA_PATH = "/vsaas/v2/cameras/";

    public static final String WATCHER_API_CAMERAS_PATH ="/vsaas/api/v2/cameras/";
    public static final String WATCHER_API_ORGANISATIONS_PATH ="/vsaas/api/v2/organizations/";

    public static final String ITEMS_LIMIT = "?limit=1000000";

    public static final String UNKNOWN_CAMERA_MARKER = "UNKNOWN_CAMERA_MARKER";
    public static final String DUPLICATE_CAMERA_MARKER = "DUPLICATE_CAMERA_MARKER";



    //public static final String GRAFANA_EXPLORE_QUERY_LEFT = "/explore?orgId=1&left=";
    //public static final String GRAFANA_EXPLORE_QUERY_RIGHT = "[\"now-1h\",\"now\",\"Prometheus\",{\"exemplar\":true,\"expr\":\"stream_alive{title=\\\"STREAM_TITLE_PARAMETER\\\"}\"}]";
    //public static final String STREAM_TITLE_PARAMETER = "STREAM_TITLE_PARAMETER";



    // special mark for logging subsystem
    public static Marker UNKNOWN_MARKER = MarkerFactory.getMarker(UNKNOWN_CAMERA_MARKER);

    public static Marker DUPLICATE_MARKER = MarkerFactory.getMarker(DUPLICATE_CAMERA_MARKER);
    
}
