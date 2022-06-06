package ru.kvanttelecom.tv.amprocessor.sgrabber.services.flussonic.parsers;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class WatcherOrganizationParser {


    public static final int ORGANIZATION_TITLE_LENGTH_MAX = 35;

    public Map<Long,String> getMap(String json) {

        Map<Long,String> result = new HashMap<>();
        JSONArray array = new JSONArray(json);
        // iterate over all streams from watcher
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);

            String title = obj.optString("title");
            title = title.substring(0, Math.min(title.length(), ORGANIZATION_TITLE_LENGTH_MAX));
            long id = obj.optLong("id");
            result.put(id, title);
        }
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
