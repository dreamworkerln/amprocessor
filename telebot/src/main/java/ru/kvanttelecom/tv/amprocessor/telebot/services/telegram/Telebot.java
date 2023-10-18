package ru.kvanttelecom.tv.amprocessor.telebot.services.telegram;

import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.AlertsFormatter;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.MarkupDetails;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.MarkupTypeEnum;
import ru.kvanttelecom.tv.amprocessor.core.data.subject.Subject;
import ru.kvanttelecom.tv.amprocessor.core.dto.camera.Camera;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.alerts.firing.FiringAlertService;
import ru.kvanttelecom.tv.amprocessor.core.telebot.service.BaseBot;

import javax.annotation.PostConstruct;

import java.util.List;

import static java.util.stream.Collectors.groupingBy;
import static ru.dreamworkerln.spring.utils.common.StringUtilsEx.isBlank;

@Service
@Slf4j
public class Telebot extends BaseBot {

    @Autowired
    private FiringAlertService firingAlertService;

    @PostConstruct
    private void init() {
        //log.info("Staring telegram Bot extends BaseBot");

        HELP_CONTENT =
                "/alerts - list of current alerts" +
                        "/alertsshort - list of current alerts short form" +
                        "\n" + "/help - this help" +
                        "\n" + "/echo [text] - echo [text]" +
                        "\n" + "/ping - echo-reply";

        handlers.put("/alerts", this::alerts);
        handlers.put("/alertsshort", this::alertsshort);


        //handlers.put("/alerts_link", this::alertsLink);
        //handlers.put("/flap",    this::flapping);


    }

    public void sendMessageToGroup(String text) {
        sendMessage(props.getBotGroup(), text);
    }

//    public void sendMessageToGroup(List<String> lines) {
//        sendMessage(props.getBotGroup(), lines);
//    }

    // ------------------------------------------------------------

    @Override
    protected void start(Long chatId, String text) {

        // permanent keyboard
        Keyboard replyKeyboardMarkup = new ReplyKeyboardMarkup("/alerts")
                .oneTimeKeyboard(false)
                .resizeKeyboard(true)
                .selective(true);
        sendMessage(chatId, HELP_CONTENT, replyKeyboardMarkup);
    }


    private void alerts(Long chatId, String unused) {
        String text = "";

        List<Alert> currentFiring = firingAlertService.get();
        text = AlertsFormatter.toString(currentFiring, MarkupTypeEnum.TELEGRAM, MarkupDetails.NONE);

        if(isBlank(text)) {
            text = "No alerts";
        }
        sendMessage(chatId, text);
    }

    // Alerts short form for Cameras
    private void alertsshort(Long chatId, String unused) {
        String text = "";

        List<Alert> currentFiring = firingAlertService.get();

        // remove Camera {IP, grafana, watcher, mediaserver, Groups} fields
        text = AlertsFormatter.toString(currentFiring, MarkupTypeEnum.TELEGRAM, MarkupDetails.CAMERAS_SHORT);

        if(isBlank(text)) {
            text = "No alerts";
        }
        sendMessage(chatId, text);
    }







//    private void alertsLink(Long chatId, String unused) {
//
//        List<Alert> alerts = alertManager.getFiringAlerts();
//        String alertsText = AlertCollectionFormatter.formatGrouped(alerts);
//        sendMessage(chatId, alertsText);
//    }

    /**
     * Show offline streams
     */
    /*
    @SneakyThrows
    private void streams(Long chatId, String text) {

        // remove bot name
        text = text.replaceAll("@.*? ", "");

        // get offline streams
        List<StreamKey> keys = streamStateService.findOffline();

        List<StreamDto> streams = streamService.findAllByKeys(keys);

        List<String> linesDown = new ArrayList<>();
        //List<String> linesFlap = new ArrayList<>();

        int count = streamService.count();

        StringBuilder sb = new StringBuilder();
        for (StreamDto stream  : streams) {

            String title = stream.getFriendlyName();
            boolean isFlapping = false;

            linesDown.add(title + "\n");

//            if(isFlapping) {
//                linesFlap.add(title + "\n");
//            }
//            else {
//                linesDown.add(title + "\n");
//            }
        }

        //linesFlap.sort(Comparator.comparing(Function.identity(), String.CASE_INSENSITIVE_ORDER));
        linesDown.sort(Comparator.comparing(Function.identity(), String.CASE_INSENSITIVE_ORDER));


        if(linesDown.size() > 0) {
            sb.append("DOWN STREAMS:\n");
            linesDown.forEach(sb::append);
        }

//        if(linesFlap.size() > 0) {
//            sb.append("\nFLAPPING STREAMS:\n");
//            linesFlap.forEach(sb::append);
//        }

//        if(count > 0 && linesDown.size() == 0 && linesFlap.size() == 0) {
//            sb.append("ALL ONLINE");
//        }

        if(count > 0 && linesDown.size() == 0) {
            sb.append("ALL ONLINE");
        }

        if(count == 0 ) {
            sb.append("NO DATA");
        }
        SendResponse response = sendMessage(chatId, sb.toString());
    }
    */
}
