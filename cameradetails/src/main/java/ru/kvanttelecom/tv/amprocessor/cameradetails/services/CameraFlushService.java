package ru.kvanttelecom.tv.amprocessor.cameradetails.services;

import com.hazelcast.topic.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import ru.kvanttelecom.tv.amprocessor.core.db.services.CameraDetailsService;
import ru.kvanttelecom.tv.amprocessor.core.dto.camera.Camera;
import ru.kvanttelecom.tv.amprocessor.core.dto.cameradetails.CameraDetails;
import ru.kvanttelecom.tv.amprocessor.core.dto.cameradetails.CameraGroup;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services._base.messages.BaseMessage;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.cameras.CameraService;
import ru.kvanttelecom.tv.amprocessor.utils.functions.TriFunction;

import javax.annotation.PostConstruct;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Set;


/**
 * Persist camera.details to DB
 *
 * Listening Hazelcast.Topic TOPIC_CAMERAS_FLUSH messages,
 * Message containing cameras names to flush
 */
@Service
@Slf4j
public class CameraFlushService extends CameraService {

    @Autowired
    private CameraDetailsService detailsService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    // alias
    interface DetailsAct extends TriFunction<Camera,CameraDetails,CameraDetails,CameraDetails> {}

//    List<DetailsAct> createNew;
//    List<DetailsAct> saveDB;
//    List<DetailsAct> assignAct;
//    List<DetailsAct> deliverAct;


    @SuppressWarnings("unchecked")
    List<DetailsAct>[][] m = (List<DetailsAct>[][]) Array.newInstance(List.class, 2, 2);


    @PostConstruct
    private void init() {
        addRequestHandler(this::flush);

        // do not listen responses from itself
        removeResponseHandler(defaultResponseListenerRegistration);

            /*
            oldDetails  details

            1           1         -> Save details to DB
            0           1         -> Save details to DB
            0           0         -> Create new details, save details to DB
            1           0         -> use oldDetails(uploaded from DB)

            */

        m[1][1] = List.of(this::saveToDB);
        m[0][1] = List.of(this::saveToDB);
        m[0][0] = List.of(this::createNew, this::saveToDB);
        m[1][0] = List.of(this::useOld);
    }

    /**
     * Create/update CameraDetails
     * <br>
     * Agreement:
     *   Camera.details == null => upload from DB == null => create new CameraDetails()
     * @param message camera names to persist
     */
    public void flush(Message<BaseMessage<Set<Camera>>> message)  {

        // Create database transaction
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactionTemplate.setName("flush");

        Set<Camera> set = message.getMessageObject().body;
        int id = message.getMessageObject().id;

        transactionTemplate.execute(status -> {

            set.forEach(camera -> {

                // hazelcast block call like put, so data is consistent around cluster
                CameraDetails details = camera.getDetails();

                // Load details from DB
                CameraDetails oldDetails = detailsService.get(camera.getName());

                int OLD = oldDetails == null ? 0 : 1;
                int NEW = details == null ? 0 : 1;

                List<DetailsAct> actions = m[OLD][NEW];
                if (actions == null) {
                    throw new IllegalStateException("Invalid logic: Camera: " +
                        camera.getName() + "oldDetails: " + oldDetails + " details: " + details);
                }

                // applying actions
                for (DetailsAct act : actions) {
                    details = act.apply(camera, oldDetails, details);
                }

                // Save camera modification back to cache
                camera.setDetails(details);
                save(camera);
            });

            log.debug("Cameras flushed: {}", set.size());
            return null;
        });
        reply(id, null);
    }



    private CameraDetails createNew(Camera camera, CameraDetails detailsOld, CameraDetails details) {
        log.debug("Create CameraDetails: {}", camera.getName());

        CameraDetails result = new CameraDetails(camera.getName());
        // Add groups

        // ToDo: uncomment adding CameraGroup.DEPLOYING
        //result.addGroup(CameraGroup.DEPLOYING);

        // Cameras with agent (commonly behind NAT)
        if(camera.getAgentId() != null) {
            result.addGroup(CameraGroup.AGENT); // Camera have agent
            result.addGroup(CameraGroup.NAT);
        }

        // JURIDIC Cameras without agent (in VLAN) (Maybe not all cameras, some juridic may be with Agent behind NAT)
        if(camera.getAgentId() == null) {
            result.addGroup(CameraGroup.JURIDIC);
            result.addGroup(CameraGroup.VLAN);   // Cameras in vlan
        }

        return result;
    }

    private CameraDetails saveToDB(Camera camera, CameraDetails detailsOld, CameraDetails details) {
        log.debug("Save CameraDetails: {}", camera.getName());
        return detailsService.save(details);
    }

    private CameraDetails useOld(Camera camera, CameraDetails detailsOld, CameraDetails details) {
        log.debug("Load CameraDetails: {}", camera.getName());
        return detailsOld;
    }





}
