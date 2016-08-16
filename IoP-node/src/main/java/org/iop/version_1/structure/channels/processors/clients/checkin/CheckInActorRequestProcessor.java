package org.iop.version_1.structure.channels.processors.clients.checkin;

import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.Package;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.request.CheckInProfileMsgRequest;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.respond.ACKRespond;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.database.exceptions.CantInsertRecordDataBaseException;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.database.exceptions.CantReadRecordDataBaseException;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.database.exceptions.CantUpdateRecordDataBaseException;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.profiles.ActorProfile;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.HeadersAttName;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.PackageType;
import org.apache.commons.lang.ClassUtils;
import org.iop.version_1.IoPNodePluginRoot;
import org.iop.version_1.structure.channels.endpoinsts.FermatWebSocketChannelEndpoint;
import org.iop.version_1.structure.channels.processors.PackageProcessor;
import org.iop.version_1.structure.context.NodeContext;
import org.iop.version_1.structure.context.NodeContextItem;
import org.iop.version_1.structure.database.jpa.daos.ActorCatalogDao;
import org.iop.version_1.structure.database.jpa.daos.JPADaoFactory;
import org.iop.version_1.structure.database.jpa.entities.ActorCatalog;
import org.iop.version_1.structure.database.jpa.entities.NodeCatalog;
import org.iop.version_1.structure.util.ThumbnailUtil;
import org.apache.log4j.Logger;

import javax.websocket.Session;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;

/**
 * The Class <code>com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.channels.processors.clients.CheckInActorRequestProcessor</code>
 * process all packages received the type <code>MessageType.CHECK_IN_ACTOR_REQUEST</code><p/>
 *
 * Created by Roberto Requena - (rart3001@gmail.com) on 06/12/15.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class CheckInActorRequestProcessor extends PackageProcessor {

    /**
     * Represent the LOG
     */
    private final Logger LOG = Logger.getLogger(ClassUtils.getShortClassName(CheckInActorRequestProcessor.class));

    /**
     * Constructor
     */
    public CheckInActorRequestProcessor() {
        super(PackageType.CHECK_IN_ACTOR_REQUEST);
    }


    /**
     * (non-javadoc)
     * @see PackageProcessor#processingPackage(Session, Package, FermatWebSocketChannelEndpoint)
     */
    @Override
    public Package processingPackage(Session session, Package packageReceived, FermatWebSocketChannelEndpoint channel) {

        LOG.info("Processing new package received: " + packageReceived.getPackageType());

        String destinationIdentityPublicKey = (String) session.getUserProperties().get(HeadersAttName.CPKI_ATT_HEADER_NAME);

        CheckInProfileMsgRequest messageContent = CheckInProfileMsgRequest.parseContent(packageReceived.getContent());

        ActorProfile actorProfile = (ActorProfile) messageContent.getProfileToRegister();

        try {

            ActorCatalogDao actorCatalogDao = JPADaoFactory.getActorCatalogDao();

            if (actorCatalogDao.exist(actorProfile.getIdentityPublicKey())) {
                checkUpdatesAndCheckIn(actorProfile, actorCatalogDao,session.getId());
            } else {
                createAndCheckIn(actorProfile, actorCatalogDao,session.getId());
            }
            LOG.info("Registered new Actor = "+actorProfile.getName());
            /*
             * If all ok, respond whit success message
             */
            ACKRespond respondProfileCheckInMsj = new ACKRespond(packageReceived.getPackageId(),ACKRespond.STATUS.SUCCESS, ACKRespond.STATUS.SUCCESS.toString());

            return Package.createInstance(
                    respondProfileCheckInMsj.toJson()                      ,
                    packageReceived.getNetworkServiceTypeSource()                  ,
                    PackageType.CHECK_IN_ACTOR_RESPONSE                         ,
                    channel.getChannelIdentity().getPrivateKey(),
                    destinationIdentityPublicKey
            );

        }catch (Exception exception){

            try {
                exception.printStackTrace();
                LOG.error(exception);

                /*
                 * Respond whit fail message
                 */
                ACKRespond respondProfileCheckInMsj = new ACKRespond(packageReceived.getPackageId(),ACKRespond.STATUS.FAIL, exception.getLocalizedMessage());

                return Package.createInstance(
                        respondProfileCheckInMsj.toJson()                      ,
                        packageReceived.getNetworkServiceTypeSource()                  ,
                        PackageType.CHECK_IN_ACTOR_RESPONSE                         ,
                        channel.getChannelIdentity().getPrivateKey(),
                        destinationIdentityPublicKey
                );

//                channel.sendPackage(session, respondProfileCheckInMsj.toJson(), packageReceived.getNetworkServiceTypeSource(), PackageType.ACK, destinationIdentityPublicKey);

            } catch (Exception e) {
                e.printStackTrace();
                LOG.error(e);
                return null;
            }
        }
    }

    private ActorCatalog checkUpdatesAndCheckIn(ActorProfile actorProfile, ActorCatalogDao actorCatalogDao, String sessionId) throws CantReadRecordDataBaseException, CantUpdateRecordDataBaseException {

        ActorCatalog actorsCatalogToUpdate = actorCatalogDao.findById(actorProfile.getIdentityPublicKey());

        boolean hasChanges = false;

        if (!actorProfile.getName().equals(actorsCatalogToUpdate.getName())) {
            actorsCatalogToUpdate.setName(actorProfile.getName());
            hasChanges = true;
        }

        if (!actorProfile.getAlias().equals(actorsCatalogToUpdate.getAlias())) {
            actorsCatalogToUpdate.setAlias(actorProfile.getAlias());
            hasChanges = true;
        }

        if (!Arrays.equals(actorProfile.getPhoto(), actorsCatalogToUpdate.getPhoto())) {
            actorsCatalogToUpdate.setPhoto(actorProfile.getPhoto());
            hasChanges = true;
        }

        if (!getNetworkNodePluginRoot().getNodeProfile().getIdentityPublicKey().equals(actorsCatalogToUpdate.getHomeNode().getId())) {
            actorsCatalogToUpdate.setHomeNode(new NodeCatalog(getNetworkNodePluginRoot().getNodeProfile().getIdentityPublicKey()));
            hasChanges = true;
        }

        if (actorProfile.getLocation() != null && actorsCatalogToUpdate.getLocation() != null && !actorProfile.getLocation().equals(actorsCatalogToUpdate.getLocation())) {
            actorsCatalogToUpdate.setLocation(actorProfile.getLocation().getLatitude(), actorProfile.getLocation().getLongitude());
            hasChanges = true;
        }

        LOG.info("hasChanges = "+hasChanges);

        if (sessionId!=null){

            Timestamp currentMillis = new Timestamp(System.currentTimeMillis());
            LOG.info("Updating profile");

            actorsCatalogToUpdate.setSession(sessionId);
            actorsCatalogToUpdate.setLastConnection(currentMillis);
            actorsCatalogToUpdate.setLastUpdateTime(currentMillis);
//
            if (hasChanges) {
                actorsCatalogToUpdate.setVersion(actorsCatalogToUpdate.getVersion() + 1);
                actorsCatalogToUpdate.setTriedToPropagateTimes(0);
            }
//            actorsCatalogToUpdate.setPendingPropagations(ActorsCatalogPropagationConfiguration.DESIRED_PROPAGATIONS);

            actorCatalogDao.update(actorsCatalogToUpdate);

        }else{
            throw new RuntimeException("ActorCheckIn: SessionId null");
        }

        return actorsCatalogToUpdate;
    }

    private ActorCatalog createAndCheckIn(ActorProfile actorProfile, ActorCatalogDao actorCatalogDao,String sessionId) throws IOException, CantInsertRecordDataBaseException, CantReadRecordDataBaseException {

        /*
         * Generate a thumbnail for the image
         */
        byte[] thumbnail = null;
        if (actorProfile.getPhoto() != null && actorProfile.getPhoto().length > 0) {
            thumbnail = ThumbnailUtil.generateThumbnail(actorProfile.getPhoto());
        }


        System.out.println("YO SOY LA PUBLIC KEY DEL NODO=!!!=!!=! "+getNetworkNodePluginRoot().getNodeProfile().getIdentityPublicKey());
        /*
         * Create the actor catalog
         */
        ActorCatalog actorCatalog = new ActorCatalog(actorProfile, thumbnail, getNetworkNodePluginRoot().getNodeProfile().getIdentityPublicKey(), "");

        Timestamp currentMillis = new Timestamp(System.currentTimeMillis());

        actorCatalog.setLastConnection(currentMillis);
        actorCatalog.setLastUpdateTime(currentMillis);
        actorCatalog.setSession(sessionId);
//        actorCatalog.setLastUpdateType(ActorCatalogUpdateTypes.ADD);
        actorCatalog.setVersion(0);
        actorCatalog.setTriedToPropagateTimes(0);
//        actorCatalog.setPendingPropagations(ActorsCatalogPropagationConfiguration.DESIRED_PROPAGATIONS);

        /*
         * Save into data base
         */
        actorCatalogDao.persist(actorCatalog);
//System.out.println("Actor Persisted: "+actorCatalog);
        return actorCatalog;
    }

    private IoPNodePluginRoot pluginRoot;

    private IoPNodePluginRoot getNetworkNodePluginRoot() {

        if (pluginRoot == null)
            pluginRoot = (IoPNodePluginRoot) NodeContext.get(NodeContextItem.PLUGIN_ROOT);

        return pluginRoot;
    }

}
