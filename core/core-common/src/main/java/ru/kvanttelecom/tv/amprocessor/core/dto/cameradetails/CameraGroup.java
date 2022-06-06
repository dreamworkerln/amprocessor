package ru.kvanttelecom.tv.amprocessor.core.dto.cameradetails;
import ru.dreamworkerln.spring.utils.common.dto.enums.AbstractEnumConverter;
import ru.dreamworkerln.spring.utils.common.dto.enums.PersistableEnum;

public enum CameraGroup implements PersistableEnum<String> {
    JURIDIC("JURIDIC"),     // Юрлица
    NATURAL("NATURAL"),     // Физлица
    INTERNAL("INTERNAL"),   // Internal ОПР камера
    TEST("TEST"),           // Test
    WAREHOUSE("WAREHOUSE"), // Склад
    VLAN("VLAN"),           // Vlan
    NAT("NAT"),             // Over NAT (have agent)
    AGENT("AGENT"),         // Have agent
    UNKNOWN("UNKNOWN"),     // Unknown
    DEPLOYING("DEPLOYING"); // Camera under constructing (should be excluded from monitoring)


    private final String value;

    @Override
    public String getValue() {
        return value;
    }

    private CameraGroup(String value) {
        this.value= value;
    }

    @javax.persistence.Converter(autoApply = true)
    public static class Converter extends AbstractEnumConverter<CameraGroup, String> {
        public Converter() {
            super(CameraGroup.class);
        }
    }
}