package ru.kvanttelecom.tv.amprocessor.core.services.downloader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import ru.dreamworkerln.spring.utils.common.rest.RestClient;

import static ru.dreamworkerln.spring.utils.common.StringUtilsEx.throwIfBlank;


@Slf4j
public class DownloaderAbstract {

    protected String EMPTY_RESPONSE = "Response is empty";
    protected String DOWNLOAD_ERROR = "Download error";

    protected RestClient restClient;

    public DownloaderAbstract(RestClient restClient) {
        this.restClient = restClient;
    }

    protected ResponseEntity<String> get(String url) {
        ResponseEntity<String> result;
        try {
            //log.debug("GET: {}", url);
            result = restClient.get(url);
            throwIfBlank(result.getBody(), EMPTY_RESPONSE);
        }
        catch (Exception rethrow) {
            throw new RuntimeException(DOWNLOAD_ERROR, rethrow);
        }
        return result;
    }

    protected ResponseEntity<String> post(String url, String json) {
        ResponseEntity<String> result;
        try {
            log.debug("POST: {}", url);
            result = restClient.post(url, json);
            //throwIfBlank(result.getBody(), EMPTY_RESPONSE);
        }
        catch (Exception rethrow) {
            throw new RuntimeException("POST ERROR", rethrow);
        }
        return result;
    }

    protected ResponseEntity<String> put(String url, String json) {
        ResponseEntity<String> result;
        try {
            log.debug("PUT: {}", url);
            result = restClient.put(url, json);
        }
        catch (Exception rethrow) {
            throw new RuntimeException("PUT ERROR", rethrow);
        }
        return result;
    }

}
