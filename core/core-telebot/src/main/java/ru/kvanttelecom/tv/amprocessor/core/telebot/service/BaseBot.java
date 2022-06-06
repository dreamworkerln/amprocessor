package ru.kvanttelecom.tv.amprocessor.core.telebot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.dreamworkerln.spring.utils.common.threadpool.BlockingJobPool;
import ru.dreamworkerln.spring.utils.common.threadpool.JobResult;

import ru.kvanttelecom.tv.amprocessor.core.telebot.configurations.properties.TelebotProperties;
import ru.kvanttelecom.tv.amprocessor.utils.Direction;


import javax.annotation.PostConstruct;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;


import static org.springframework.util.StringUtils.trimWhitespace;
import static ru.dreamworkerln.spring.utils.common.StringUtilsEx.formatMsg;
import static ru.dreamworkerln.spring.utils.common.StringUtilsEx.isBlank;
import static ru.kvanttelecom.tv.amprocessor.utils.CommonUtils.calculateNewTimeout;

@Service
@Slf4j
public abstract class BaseBot {
    private static final int TELEGRAM_MAX_MESSAGE_LENGTH = 4000;
    private static final Duration TELEGRAM_SEND_TIMEOUT_MIN = Duration.ofSeconds(8);
    private static final Duration TELEGRAM_SEND_TIMEOUT_MAX = Duration.ofSeconds(128);
    private final AtomicReference<Duration> telegramSendTimeout = new AtomicReference<>(TELEGRAM_SEND_TIMEOUT_MIN);

    // messages handlers
    protected final ConcurrentMap<String, BiConsumer<Long, String>> handlers = new ConcurrentHashMap<>();

    protected DecimalFormat df;
    protected TelegramBot bot;



    protected String HELP_CONTENT =
        "/help - this help" +
            "\n" + "/echo [text] - echo [text]" +
            "\n" + "/ping - echo-reply";

    // Non-negative AtomicInteger incrementator
    //private final LongUnaryOperator idIncrementator = (i) -> i == Long.MAX_VALUE ? 0 : i + 1;
    // connection id generator
    //private final AtomicLong idGen =  new AtomicLong();
    //private final ConcurrentMap<Long, SendMessage> messageQueue = new ConcurrentHashMap<>();

    private final BlockingJobPool<SendMessage,SendResponse> jobPool =
        new BlockingJobPool<>(1, TELEGRAM_SEND_TIMEOUT_MIN, null, "telegramPool");

    //private Map<Long, AtomicLong> locks = new ConcurrentHashMap<>();

    //private final Deque<SimpleMessage> messageQueue = new ConcurrentLinkedDeque<>();

    private final BlockingDeque<SimpleMessage> messageQueue = new LinkedBlockingDeque<>();

    @Autowired
    protected TelebotProperties props;

    @PostConstruct
    protected void postConstruct() {
        log.info("Staring telegram bot");

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        otherSymbols.setDecimalSeparator('.');

        //df = new DecimalFormat("#.###", otherSymbols);
        df = new DecimalFormat("#", otherSymbols);

        handlers.put("/start",   this::start);
        handlers.put("/help",    this::help);
        handlers.put("/echo",    this::echo);
        handlers.put("/ping",    this::ping);
//        handlers.put("/streams", this::streams);
//        handlers.put("/flap",    this::flapping);



        bot = new TelegramBot.Builder(props.getBotToken()).build();

        bot.setUpdatesListener(updates -> {

            // process updates
            for (Update update : updates) {

                try {

                    Long chatId = update.message().chat().id();
                    String text = update.message().text();

                    if (isBlank(text)) {
                        continue;
                    }

                    String[] commandArray = text.split("[ @]");
                    if (commandArray.length > 0) {
                        String command = commandArray[0];

                        if (handlers.containsKey(command)) {
                            String body = trimWhitespace(text.substring(command.length()));

                            handlers.get(command).accept(chatId, body);
                        }
                    }

                }
                catch (Exception e) {
                    String msg = formatMsg("Bot receive/handle update: {}\nError: ", update);
                    log.error(msg, e);
                }
            }

            // return id of last processed update or confirm them all
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });

    }


    /**
     * Startup runner
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        // initializing and starting message sending loop
        Thread thread = new Thread(this::sendingMessageLoop);
        thread.setDaemon(false);
        thread.setName("TelebotSndMsgLoop");
        thread.start();
    }



    // -------------------------------------------------------------------------------

    protected void help(Long chatId, String text) {
        sendMessage(chatId, HELP_CONTENT);
        //"\n" + "Streams info: " + PROTOCOL + props.getAddress() + "/streams" + "\n";
    }

    protected void echo(Long chatId, String text) {
        sendMessage(chatId, text);

    }

    protected void ping(Long chatId, String text) {
        sendMessage(chatId, "pong");
    }

    protected abstract void start(Long chatId, String text);



    /**
     * Send message to telegram
     * @param chatId whom
     * @param message text
     */
    @SneakyThrows
    public void sendMessage(Long chatId, String message) {
        sendMessage(chatId, message, null);
    }

//    @SneakyThrows
//    public void sendMessage(Long chatId, List<String> lines) {
//        sendMessage(chatId, lines, null);
//    }

//    /**
//     * Send message to telegram
//     * @param chatId whom
//     * @param message text
//     * @param keyboard
//     * <br>
//     */
//    @SneakyThrows
//    public void sendMessage(Long chatId, List<String> lines, Keyboard keyboard) {
//
//        if (lines.size() == 0) {
//            return;
//        }
//        messageQueue.put(new SimpleMessage(chatId, lines, keyboard));
//    }

    @SneakyThrows
    public void sendMessage(Long chatId, String message, Keyboard keyboard) {

        if (isBlank(message)) {
            return;
        }
        messageQueue.offer(new SimpleMessage(chatId, message, keyboard));
    }

    @SneakyThrows
    private void sendingMessageLoop() {

        while(!Thread.currentThread().isInterrupted()) {

            try {

                // will wait till new messages have arrived
                SimpleMessage simpleMessage = messageQueue.take();

                Long chatId = simpleMessage.getChatId();
                Keyboard keyboard = simpleMessage.getKeyboard();

                List<String> lines = simpleMessage.getLines();

                // будем отправлять по одному chunk
                for (int i = 0; i < lines.size(); i++) {

                    String chunk = lines.get(i).replace("_", "\\_");

                    SendMessage msg = new SendMessage(chatId, chunk).parseMode(ParseMode.Markdown);

                    // add keyboard to last message
                    if (keyboard != null && i == lines.size() - 1) {
                        msg.replyMarkup(keyboard);
                    }

                    boolean sendOk;
                    do {
                        //log.debug("Telebot sending: {}", chunk);
                        log.debug("Telebot - sending message");

                        // отправка
                        JobResult<SendMessage, SendResponse> jobResult = jobPool.execTimeout(msg,
                            a -> new JobResult<>(msg, bot.execute(msg)), telegramSendTimeout.get());

                        Throwable exception = jobResult.getException();
                        SendResponse response = jobResult.getResult();
                        
                        sendOk = exception == null && response != null && response.isOk();

                        log.debug("Telebot send result: {}", sendOk);

                        if (!sendOk) {
                            log.error("Telebot response: {} ", response, exception);

                            // дополнительно ждем
                            log.debug("Waiting : {} sec", telegramSendTimeout.get().toSeconds());
                            TimeUnit.SECONDS.sleep(telegramSendTimeout.get().toSeconds());

                            // увеличиваем timeout
                            calculateNewTimeout(Direction.UP, telegramSendTimeout, TELEGRAM_SEND_TIMEOUT_MIN, TELEGRAM_SEND_TIMEOUT_MAX);
                        } else {
                            // decrease timeout
                            calculateNewTimeout(Direction.DOWN, telegramSendTimeout, TELEGRAM_SEND_TIMEOUT_MIN, TELEGRAM_SEND_TIMEOUT_MAX);
                        }
                    }
                    while (!sendOk);
                    log.debug("Telebot have been send");
                }
            }
            catch (InterruptedException e) {
                log.warn(this.getClass().getSimpleName() +  ".sendingMessageLoop() was interrupted!");
                Thread.currentThread().interrupt();
            }
            catch(Exception skip) {
                log.error("SOMETHING BAD HAVE BEEN HAPPENED, CONTINUE: ", skip);
            }
        }
    }


    // =========================================================================


    private static List<String> stringSplitter(String text) {

        if(text.charAt(text.length() - 1) != '\n') {
            text = text.concat("\n");
        }

        List<String> result = new ArrayList<>();
        int length; // = Integer.MAX_VALUE;


        while ((length = Math.min(text.length(), TELEGRAM_MAX_MESSAGE_LENGTH)) > 0) {

            while (length > 0 && text.charAt(length - 1) != '\n') {
                length--;
            }
            result.add(text.substring(0, length));
            text = text.substring(length);
        }

        return result;
    }


    /*
    private void calculateNewDuration(int direction) {

        telegramSendTimeout.getAndUpdate(d -> {

            Duration result = d;
            if (direction == 1) {
                result = d.multipliedBy(2);
            }
            if (direction == -1) {
                result = d.dividedBy(2);
            }

            if (result.toSeconds() > TELEGRAM_SEND_TIMEOUT_MAX.toSeconds() ||
                result.toSeconds() < TELEGRAM_SEND_TIMEOUT_MIN.toSeconds()) {
                result = d;
            }
            return result;
        });
    }
    */



    @Data
    private static class SimpleMessage {
        private Long chatId;
        List<String> lines;
        private Keyboard keyboard;

        public SimpleMessage(Long chatId, List<String> lines, Keyboard keyboard) {
            this.chatId = chatId;
            this.lines = lines;
            this.keyboard = keyboard;
        }

        public SimpleMessage(Long chatId, String text, Keyboard keyboard) {
            this.chatId = chatId;
            // разбивка по страницам 4k (telegram API не принимает сообщение большего размера)
            this.lines = stringSplitter(text);
            this.keyboard = keyboard;
        }
    }


}


/*

AtomicLong lock;
        synchronized(this) {
            lock = locks.computeIfAbsent(chatId, l -> new AtomicLong(0));
            lock.incrementAndGet();
            log.debug("locking: {}", lock.get());
        }

        // блокируется отправка сообщений только к одному и тому же пользователю
        //Object lock = locks.computeIfAbsent(chatId, l -> new Object());
        synchronized (lock) {
            log.debug("lock entered: {}", lock.get());

            // разбивка по страницам 4k (telegram API не принимает сообщение большего размера)
            List<String> lines = stringSplitter(message);

            // будем отправлять по одному chunk
            for (int i = 0; i < lines.size(); i++) {

                String chunk = lines.get(i);

                SendMessage msg = new SendMessage(chatId, chunk).parseMode(ParseMode.Markdown);

                // add keyboard to last message
                if (keyboard != null && i == lines.size() - 1) {
                    msg.replyMarkup(keyboard);
                }
                Throwable exception;

                boolean sendOk;
                do {

                    log.debug("Telebot sending: {}", chunk);

                    // отправка
                    JobResult<SendMessage, SendResponse> jobResult = jobPool.execTimeout(msg,
                        a -> new JobResult<>(msg, bot.execute(msg)), telegramSendTimeout.get());

                    exception = jobResult.getException();
                    result = jobResult.getResult();
                    log.debug("Telebot send result: {}", result);

                    sendOk = (result != null) && (result.message() != null);
                    if (!sendOk) {

                        log.debug("Telebot send exception:", exception);
                        log.debug("Telebot send result: null");

                        // дополнительно ждем
                        Thread.sleep(telegramSendTimeout.get().toMillis());
                        // увеличиваем timeout
                        calculateNewDuration(+1);
                    } else {
                        // decrease timeout
                        calculateNewDuration(-1);
                    }
                }
                while (!sendOk);
                log.debug("Telebot have been send");
            }

            synchronized(this) {
                if(lock.decrementAndGet() == 0) {
                    locks.remove(chatId);
                }
                log.debug("lock exiting: {}", lock.get());
            }

        } //synchronized (lock)



 */