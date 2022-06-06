package ru.kvanttelecom.tv.amprocessor.mailer.services.mail;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.dreamworkerln.spring.utils.common.threadpool.BlockingJobPool;
import ru.dreamworkerln.spring.utils.common.threadpool.JobResult;
import ru.kvanttelecom.tv.amprocessor.mailer.configurations.properties.MailProperties;
import ru.kvanttelecom.tv.amprocessor.utils.Direction;

import java.time.Duration;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static ru.dreamworkerln.spring.utils.common.StringUtilsEx.notBlank;
import static ru.kvanttelecom.tv.amprocessor.utils.CommonUtils.calculateNewTimeout;

/**
 * MAYBE EXTRACT TO SINGLE MICROSERVICE (Connect with GRAPHQL/AMQP)
 */
@Service
@Slf4j
public class MailSender {

    private static final Duration MAIL_SEND_TIMEOUT_MIN = Duration.ofSeconds(32);
    private static final Duration MAIL_SEND_TIMEOUT_MAX = Duration.ofSeconds(1024);
    private final AtomicReference<Duration> mailSendTimeout = new AtomicReference<>(MAIL_SEND_TIMEOUT_MIN);

    private final BlockingJobPool<SimpleMailMessage, Void> jobPool =
        new BlockingJobPool<>(1, MAIL_SEND_TIMEOUT_MIN, null, "mailPool");

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private MailProperties mailProps;

    //private final BlockingDeque<SimpleMailMessage> messageQueue = new LinkedBlockingDeque<>();

    //private final Deque<SimpleMailMessage> messageQueue = new ConcurrentLinkedDeque<>();
    private final BlockingDeque<SimpleMailMessage> messageQueue = new LinkedBlockingDeque<>();


    /**
     * Startup runner
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        // initializing and starting message sending loop
        Thread thread = new Thread(this::sendingMessageLoop);
        thread.setDaemon(false);
        thread.setName("MailSndMsgLoop");
        thread.start();
    }


    public void send(String subject, String to, String text) {

        //subject = "Prometheus alert (under construction v0.2)";

        if (notBlank(text)) {
            SimpleMailMessage message = createSimpleMessage(subject, to, text);
            messageQueue.offer(message);
        }

//        // add message to messageQueue
//        if (notBlank(text)) {
//            for (String to : mailProps.getTo()) {
//                SimpleMailMessage message = createSimpleMessage("Prometheus alert (under construction v0.2)", to, text);
//                messageQueue.offer(message);
//            }
//        }
    }




    @SneakyThrows
    private void sendingMessageLoop() {

        while(!Thread.currentThread().isInterrupted()) {

            try {
                // try to send first message from messageQueue -------------------
                SimpleMailMessage message = messageQueue.take();

                boolean sendOk;
                do {

                    log.debug("Sending mail message, subject: \"{}\", to: {}", message.getSubject(), message.getTo());

                    // отправка
                    JobResult<SimpleMailMessage, Void> jobResult =
                        jobPool.execTimeout(message, m -> {
                                javaMailSender.send(m);
                                return new JobResult<>(m);
                            },
                            mailSendTimeout.get());

                    Throwable exception = jobResult.getException();
                    sendOk = exception == null;

                    // принудительно ждем после успешной/неуспешной отправки каждого письма
                    TimeUnit.SECONDS.sleep(mailProps.getWaitIntervalBetweenSendSec());

                    if (!sendOk) {
                        log.error("Mail send exception:", exception);
                        // дополнительно ждем
                        log.debug("Waiting : {} sec", mailSendTimeout.get().toSeconds());
                        TimeUnit.SECONDS.sleep(mailSendTimeout.get().toSeconds());

                        // увеличиваем timeout
                        calculateNewTimeout(Direction.UP, mailSendTimeout, MAIL_SEND_TIMEOUT_MIN, MAIL_SEND_TIMEOUT_MAX);
                    } else {
                        // decrease timeout
                        calculateNewTimeout(Direction.DOWN, mailSendTimeout, MAIL_SEND_TIMEOUT_MIN, MAIL_SEND_TIMEOUT_MAX);
                    }
                }
                while (!sendOk);

            }
            catch (InterruptedException e) {
                log.warn(this.getClass().getSimpleName() +  ".sendingMessageLoop() was interrupted!");
                Thread.currentThread().interrupt();
            }
            catch (Exception skip) {
                log.error("Mail sendingMessageLoop error, continue:", skip);
            }
        }

    }


    /*

    public void send(String text) {

        try {


            // try to send first message from messageQueue -------------------
            SimpleMailMessage message = messageQueue.poll();
            if(message!= null) {

                boolean sendOk;
                do {

                    log.debug("Sending mail message, subject: \"{}\", to: {}", message.getSubject(), message.getTo());

                    // отправка
                    JobResult<SimpleMailMessage,Void> jobResult =
                        jobPool.execTimeout(message, m -> {
                                javaMailSender.send(m);
                                return new JobResult<>(m);
                            }, mailSendTimeout.get());

                    Throwable exception = jobResult.getException();
                    sendOk = exception == null;

                    if (!sendOk) {
                        log.debug("Telebot send exception:", exception);
                        // дополнительно ждем
                        TimeUnit.SECONDS.sleep(mailSendTimeout.get().toSeconds());

                        // увеличиваем timeout
                        calculateNewDuration(+1);
                    } else {
                        // decrease timeout
                        calculateNewDuration(-1);
                    }
                }
                while (!sendOk);
            }

        }
        catch (Exception skip) {
            log.error("Mail scheduler error:", skip);
        }
    }
    */



    // ============================================================

    private SimpleMailMessage createSimpleMessage(String subject, String to, String msg) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProps.getUsername());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(msg);
        return message;
    }


}
