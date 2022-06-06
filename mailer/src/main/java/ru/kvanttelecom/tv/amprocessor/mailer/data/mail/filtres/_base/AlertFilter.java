package ru.kvanttelecom.tv.amprocessor.mailer.data.mail.filtres._base;

import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;

import java.util.function.Predicate;

public interface AlertFilter {

    /**
     * Test filter predicate
     */
    boolean test(Alert alert);

    /**
     * Return filter predicate
     */
    Predicate<Alert> predicate();
}
