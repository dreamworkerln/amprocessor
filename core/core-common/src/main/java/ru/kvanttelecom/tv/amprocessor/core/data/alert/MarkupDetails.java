package ru.kvanttelecom.tv.amprocessor.core.data.alert;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum MarkupDetails {
    CAMERAS_SHORT_FORM;

    public static Set<MarkupDetails> NONE = new HashSet<>();

    public static Set<MarkupDetails> CAMERAS_SHORT = new HashSet<>(List.of(MarkupDetails.CAMERAS_SHORT_FORM));
}
