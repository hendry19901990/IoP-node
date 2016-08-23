package org.iop.version_1.structure.channels.processors.clients;

import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.Package;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.request.IsActorOnlineMsgRequest;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.request.SubscriberMsgRequest;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.respond.ACKRespond;
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
import org.iop.version_1.structure.database.jpa.entities.EventListener;

import javax.websocket.Session;
import java.io.IOException;

/**
 * Created by Manuel Perez P. (darkpriestrelative@gmail.com) on 16/08/16.
 */
public class SubscribersRequestProcessor extends PackageProcessor {

    /**
     * Represent the LOG
     */
    private final Logger LOG = Logger.getLogger(ClassUtils.getShortClassName(SubscribersRequestProcessor.class));

    /**
     * Default constructor
     */
    public SubscribersRequestProcessor() {
        super(PackageType.SUBSCRIBER);
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
        SubscriberMsgRequest subscriberMsgRequest = SubscriberMsgRequest
                .parseContent(packageReceived.getContent());


        try{


            JPADaoFactory.getEventListenerDao().save(new EventListener(packageReceived.getPackageId().toString(),session.getId(),subscriberMsgRequest.getEventCode(),subscriberMsgRequest.getCondition()));

            //Respond the request
            ACKRespond ackRespond = new ACKRespond(packageReceived.getPackageId(),
                    ACKRespond.STATUS.SUCCESS,
                    ACKRespond.STATUS.SUCCESS.toString());

            //Create instance
            if (session.isOpen()) {

                return Package.createInstance(
                        ackRespond.toJson(),
                        PackageType.ACK,
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
                ACKRespond ackRespond = new ACKRespond(
                        packageReceived.getPackageId(),
                        ACKRespond.STATUS.FAIL,
                        exception.getLocalizedMessage()
                );

                return Package.createInstance(
                        ackRespond.toJson()                      ,
                        PackageType.ACK                         ,
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
