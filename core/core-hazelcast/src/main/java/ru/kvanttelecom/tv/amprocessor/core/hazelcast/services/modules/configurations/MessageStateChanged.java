package ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.modules.configurations;

import ru.kvanttelecom.tv.amprocessor.core.hazelcast.data.ModuleState;

import java.io.Serializable;

public class MessageStateChanged implements Serializable {
    public final String moduleName;
    public final ModuleState state;

    public MessageStateChanged(String moduleName, ModuleState state) {
        this.moduleName = moduleName;
        this.state = state;
    }
}
