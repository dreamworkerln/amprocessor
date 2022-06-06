package ru.kvanttelecom.tv.amprocessor.utils.enums;

public enum StreamEventType {
    ERROR,
    INIT,
    //NONE,
    ONLINE,   // stream went online
    OFFLINE,  // stream went offline
    CREATED,  // stream was created
    UPDATED,  // stream was modified (stream informational fields as comment, etc, not StreamStatus)
    DELETED,  // stream was deleted
    ENABLED,  // stream was enabled
    DISABLED, // stream was disabled
    START_FLAPPING,
    STOP_FLAPPING
}
