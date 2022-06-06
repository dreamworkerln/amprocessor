package ru.kvanttelecom.tv.amprocessor.core.data.subject;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Map;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@type")
//@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, property="@class")
public interface Subject {
    String getName();
    String getTitle();
    Map<String,String> getReferenceUrlMap();

    //String toString(MarkupTypeEnum type);
}
