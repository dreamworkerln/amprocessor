package ru.kvanttelecom.tv.amprocessor.core.dto.camera;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ru.dreamworkerln.spring.utils.common.dto.AbstractDto;
import ru.kvanttelecom.tv.amprocessor.core.data.subject.Subject;
import ru.kvanttelecom.tv.amprocessor.core.dto.cameradetails.CameraDetails;

import javax.validation.constraints.NotNull;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
public class Camera extends AbstractDto implements Subject {

    public static final int STREAM_TITLE_MAX_LENGTH = 25;
    public static final String STREAM_TITLE_MAX_LENGTH_REPLACER = "..";

    public static final String INCOMPLETE_CAMERA_IMPORT = "Incomplete camera import";

    //protected StreamKey key;

    @NotNull
    protected String hostname; // only unix hostame (t3)
    @NotNull
    protected String name;
    protected String title;

    @NotNull
    protected String domainName;
    private Map<String,String> referenceUrlMap;
    protected String comment;
    protected String postalAddress;
    protected String coordinates;
    protected String client;
    protected String organization;
    protected Long agentId;
    protected String streamUrl;

    @EqualsAndHashCode.Exclude
    protected CameraDetails details;

    public static String filterTitle(String title) {

        if (!title.equals(title.trim())) {
            log.debug("Stream with trimmable spaces: '{}', '{}'", title, title.trim());
        }

        return StringUtils.abbreviateMiddle(title, STREAM_TITLE_MAX_LENGTH_REPLACER, STREAM_TITLE_MAX_LENGTH).trim();
    }

    public Camera(@NotNull String name,
                  String title,
                  @NotNull String domainName,
                  @NotNull String hostname,
                  Map<String,String> referenceUrlMap,
                  String comment,
                  String postalAddress,
                  String coordinates,
                  String client,
                  String organization,
                  Long agentId,
                  String streamUrl) {

        this.name = name;
        this.title = filterTitle(title);
        this.domainName = domainName;
        this.hostname = hostname;
        this.referenceUrlMap = referenceUrlMap;
        this.comment = comment;
        this.postalAddress = postalAddress;
        this.coordinates = coordinates;
        this.client = client;
        this.organization = organization;
        this.agentId = agentId;
        this.streamUrl = streamUrl;
    }

    @Override
    public String toString() {
        return "Camera{" +
            "hostname='" + hostname + '\'' +
            ", name='" + name + '\'' +
            ", title='" + title + '\'' +
            ", domainName='" + domainName + '\'' +
            ", referenceUrlMap=" + referenceUrlMap +
            ", comment='" + comment + '\'' +
            ", postalAddress='" + postalAddress + '\'' +
            ", coordinates='" + coordinates + '\'' +
            ", client='" + client + '\'' +
            ", organization='" + organization + '\'' +
            ", agentId=" + agentId +
            ", streamUrl='" + streamUrl + '\'' +
            ", details=" + details +
            '}';
    }

//    @Override
//    public String getKey() {
//        return name;
//    }

    //    public Camera clone() {
//
//        Camera result = new Camera(name, title, domainName, hostname,
//            referenceUrlMap, comment,
//            postalAddress, coordinates, client, organization, agentId, streamUrl);
//
//        result.setDetails(details.clone());
//
//        return result;
//    }


//    @Override
//    public String toString(MarkupTypeEnum type) {
//
//        String BOLDER = "";
//
//        switch (type) {
//
//            case REDMINE:
//            case MAIL:
//                break;
//
//            case TELEGRAM:
//                BOLDER = "*";
//                break;
//        }
//
//        StringBuilder sb = new StringBuilder();
//
//        sb.append(BOLDER + title + BOLDER + "\n");
//
//        if(type == MarkupTypeEnum.REDMINE) {
//            sb.append("Организация: " + BOLDER + organization + BOLDER + "\n");
//        }
//
//        if(agentId != null) {
//            sb.append("Агент: ON"+ '\n');
//        }
//        else if (notBlank(streamUrl)) {
//            String streamIP = "CANT PARSE URL";
//            try {
//                String u = streamUrl;
//                URI uri = new URI(u);
//                streamIP = uri.getHost();
//
//            } catch (Exception skip) {
//                String message = formatMsg("Can't parse URL");
//                log.warn(message, skip);
//            }
//            sb.append("IP: "+ streamIP + "\n");
//        }
////        else {
////            sb.append("Agent == null && IP == null!\n");
////        }
//
//        // referenceURL
//        referenceUrlMap.forEach((k, v) -> sb.append(k + ":\n" + v + "\n"));
//
//        return sb.toString();
//    }

    //protected boolean alive;

    //    public static Stream fromStreamKey(StreamKey key) {
//
//        Stream result = new Stream();
//        result.setStreamKey(key);
//        result.setHostname(key.getHostname());
//        result.setName(key.getName());
//
//        return result;
//    }

//    @JsonIgnore
//    public String getFriendlyName() {
//        String result = title;
//        if(isBlank(title)) {
//            result = name + "   (" + hostname + ")";
//        }
//        return result;
//    }



}
