package org.iop.version_1.structure.channels.processors.clients;

import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.Package;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.request.IsActorOnlineMsgRequest;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.respond.IsActorOnlineMsgRespond;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.enums.ProfileStatus;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.HeadersAttName;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.PackageType;
import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;
import org.iop.version_1.structure.channels.endpoinsts.FermatWebSocketChannelEndpoint;
import org.iop.version_1.structure.channels.processors.PackageProcessor;
import org.iop.version_1.structure.context.SessionManager;
import org.iop.version_1.structure.database.jpa.daos.JPADaoFactory;

import javax.websocket.Session;
import java.io.IOException;

/**
 * Created by Manuel Perez P. (darkpriestrelative@gmail.com) on 16/08/16.
 */
public class IsActorOnlineRequestProcessor extends PackageProcessor {

    /**
     * Represent the LOG
     */
    private final Logger LOG = Logger.getLogger(ClassUtils.getShortClassName(IsActorOnlineRequestProcessor.class));

    /**
     * Default constructor
     */
    public IsActorOnlineRequestProcessor() {
        super(PackageType.IS_ACTOR_ONLINE);
    }

    /**
     * This method process the request message
     * @param session that send the package
     * @param packageReceived to process
     * @param channel
     * @return
     * @throws IOException
     */
    @Override
    public Package processingPackage(
            Session session,
            Package packageReceived,
            FermatWebSocketChannelEndpoint channel) throws IOException {

        LOG.info("Processing new package received: " + packageReceived.getPackageType());

        //Represents the requester pk
        String destinationIdentityPublicKey = (String) session
                .getUserProperties()
                .get(HeadersAttName.CPKI_ATT_HEADER_NAME);

        //Parsing the json String
        IsActorOnlineMsgRequest isActorOnlineMsgRequest = IsActorOnlineMsgRequest
                .parseContent(packageReceived.getContent());

        //Profile requested
        String actorProfilePublicKey = isActorOnlineMsgRequest.getRequestedProfilePublicKey();

        try{

            ProfileStatus profileStatus = ProfileStatus.OFFLINE;

            /*
             * Get the actorSessionId
             */
            String actorSessionId = JPADaoFactory.getActorCatalogDao().findValueById(destinationIdentityPublicKey, String.class, "sessionId");

            /*
             * Validate the session
             */
            if(actorSessionId != null &&
                    !actorSessionId.isEmpty() &&
                        SessionManager.exist(actorSessionId)){

                profileStatus = ProfileStatus.ONLINE;
            }

            //Respond the request
            IsActorOnlineMsgRespond isActorOnlineMsgRespond = new IsActorOnlineMsgRespond(packageReceived.getPackageId(),
                                                                                          IsActorOnlineMsgRespond.STATUS.SUCCESS,
                                                                                          IsActorOnlineMsgRespond.STATUS.SUCCESS.toString(),
                                                                                          actorProfilePublicKey, profileStatus,
                                                                                          packageReceived.getNetworkServiceTypeSource().getCode());

            //Create instance
            if (session.isOpen()) {

                return Package.createInstance(
                        isActorOnlineMsgRespond.toJson(),
                        packageReceived.getNetworkServiceTypeSource(),
                        PackageType.IS_ACTOR_ONLINE,
                        channel.getChannelIdentity().getPrivateKey(),
                        destinationIdentityPublicKey
                );

            } else {
                throw new IOException("connection is not opened.");
            }

        } catch(Exception exception){
            try {
                exception.printStackTrace();
                LOG.error(exception.getMessage());
                /*
                 * Respond whit fail message
                 */
                IsActorOnlineMsgRespond actorListMsgRespond = new IsActorOnlineMsgRespond(
                        packageReceived.getPackageId(),
                        IsActorOnlineMsgRespond.STATUS.FAIL,
                        exception.getLocalizedMessage(),
                        null,
                        null,
                        (packageReceived == null ? null : packageReceived.getNetworkServiceTypeSource().toString())
                );

                return Package.createInstance(
                        actorListMsgRespond.toJson()                      ,
                        packageReceived.getNetworkServiceTypeSource()                  ,
                        PackageType.IS_ACTOR_ONLINE                         ,
                        channel.getChannelIdentity().getPrivateKey(),
                        destinationIdentityPublicKey
                );

            } catch (Exception e) {
                e.printStackTrace();
                LOG.error(e);
                return null;
            }
        }
    }
}
