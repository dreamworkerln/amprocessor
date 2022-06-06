package ru.kvanttelecom.tv.amprocessor.core.data.subject;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;
import ru.dreamworkerln.spring.utils.common.dto.AbstractDto;

import java.util.Map;

@Data
public class DefaultSubject extends AbstractDto implements Subject {

    private String name;
    private String title;
    private Map<String,String> referenceUrlMap;

    @JsonCreator
    public DefaultSubject(String name, String title, Map<String, String> referenceUrlMap) {
        this.name = name;
        this.title = title;
        this.referenceUrlMap = referenceUrlMap;
    }


//    @Override
//    public String toString(MarkupTypeEnum type) {
//
//        String BOLDER = "";
//
//        StringBuilder sb = new StringBuilder();
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
//        sb.append(BOLDER + title + BOLDER + "\n");
//
//        // referenceURL
//        if(referenceUrlMap.size() > 0) {
//            referenceUrlMap.forEach((k, v) -> sb.append(k + ":\n" + v + "\n"));
//        }
//        return sb.toString();
//    }
//
}
