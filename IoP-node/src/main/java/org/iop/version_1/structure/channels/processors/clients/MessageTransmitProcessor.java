package org.iop.version_1.structure.channels.processors.clients;

import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.Package;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.respond.ACKRespond;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.respond.MsgRespond;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.database.entities.NetworkServiceMessage;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.HeadersAttName;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.PackageType;
import org.apache.commons.lang.ClassUtils;
import org.iop.version_1.structure.channels.endpoinsts.FermatWebSocketChannelEndpoint;
import org.iop.version_1.structure.channels.processors.PackageProcessor;
import org.iop.version_1.structure.context.NodeContext;
import org.iop.version_1.structure.context.SessionManager;
import org.iop.version_1.structure.database.jpa.daos.JPADaoFactory;
import org.jboss.logging.Logger;

import javax.websocket.SendHandler;
import javax.websocket.SendResult;
import javax.websocket.Session;

/**
 * The Class <code>com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.channels.processors.clients.MessageTransmitProcessor</code>
 * process all packages received the type <code>PackageType.MESSAGE_TRANSMIT</code><p/>
 *
 * Created by Roberto Requena - (rart3001@gmail.com) on 30/04/16.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class MessageTransmitProcessor extends PackageProcessor {

    /**
     * Represent the LOG
     */
    private final Logger LOG = Logger.getLogger(ClassUtils.getShortClassName(MessageTransmitProcessor.class));

    /**
     * Represent the clientsSessionMemoryCache instance
     */
    private final SessionManager clientsSessionMemoryCache;

    /**
     * Constructor
     */
    public MessageTransmitProcessor() {
        super(PackageType.MESSAGE_TRANSMIT);
        this.clientsSessionMemoryCache = NodeContext.getSessionManager();
    }

    /**
     * (non-javadoc)
     * @see PackageProcessor#processingPackage(Session, Package, FermatWebSocketChannelEndpoint)
     */
    @Override
    public Package processingPackage(final Session session, final Package packageReceived, final FermatWebSocketChannelEndpoint channel) {

        LOG.info("Processing new package received "+packageReceived.getPackageType());
        String senderIdentityPublicKey = (String) session.getUserProperties().get(HeadersAttName.CPKI_ATT_HEADER_NAME);
        final NetworkServiceMessage messageContent = NetworkServiceMessage.parseContent(packageReceived.getContent());

        final String destinationIdentityPublicKey = packageReceived.getDestinationPublicKey();
        LOG.info("Package destinationIdentityPublicKey =  "+destinationIdentityPublicKey);

        try {
            /*
             * Get the connection to the destination
             */
            String actorSessionId = JPADaoFactory.getActorCatalogDao().findValueById(destinationIdentityPublicKey,String.class,"sessionId");
            Session clientDestination = clientsSessionMemoryCache.get(actorSessionId);

            if (clientDestination != null) {

                clientDestination.getAsyncRemote().sendObject(packageReceived, new SendHandler() {
                    @Override
                    public void onResult(SendResult result) {

                    try {
                        if (result.isOK()) {

                            ACKRespond messageTransmitRespond = new ACKRespond(MsgRespond.STATUS.SUCCESS, MsgRespond.STATUS.SUCCESS.toString(), packageReceived.getPackageId());

                            channel.sendPackage(session, messageTransmitRespond.toJson(), packageReceived.getNetworkServiceTypeSource(), PackageType.ACK, destinationIdentityPublicKey);
                            LOG.info("Message transmit successfully");
                        } else {
                            ACKRespond messageTransmitRespond = new ACKRespond(
                                    MsgRespond.STATUS.FAIL,
                                    (result.getException() != null ? result.getException().getMessage() : "destination not available"),
                                    packageReceived.getPackageId());
                            channel.sendPackage(session, messageTransmitRespond.toJson(), packageReceived.getNetworkServiceTypeSource(), PackageType.ACK, destinationIdentityPublicKey);
                            LOG.info("Message cannot be transmitted", result.getException());
                        }
                    } catch (Exception ex) {
                        LOG.error("Cannot send message to counter part.", ex);
                    }
                    }
                });

            } else {

                /*
                 * Notify to de sender the message can not transmit
                 */
                ACKRespond ackRespond = new ACKRespond(MsgRespond.STATUS.FAIL, "The destination is not more available", packageReceived.getPackageId());

                LOG.info("The destination is not more available, Message not transmitted");
                return Package.createInstance(
                        ackRespond.toJson(),
                        packageReceived.getNetworkServiceTypeSource(),
                        PackageType.ACK,
                        channel.getChannelIdentity().getPrivateKey(),
                        destinationIdentityPublicKey
                );
//                channel.sendPackage(session, messageTransmitRespond.toJson(), packageReceived.getNetworkServiceTypeSource(), PackageType.ACK, destinationIdentityPublicKey);


            }

            LOG.info("------------------ Processing finish ------------------");

        } catch (Exception exception){

            try {
            
                LOG.error(exception);

                ACKRespond ackRespond = new ACKRespond(MsgRespond.STATUS.FAIL, exception.getMessage(), packageReceived.getPackageId());
                return Package.createInstance(
                        ackRespond.toJson(),
                        packageReceived.getNetworkServiceTypeSource(),
                        PackageType.ACK,
                        channel.getChannelIdentity().getPrivateKey(),
                        destinationIdentityPublicKey
                );
//                channel.sendPackage(session, messageTransmitRespond.toJson(), packageReceived.getNetworkServiceTypeSource(), PackageType.ACK, destinationIdentityPublicKey);

            } catch (Exception e) {
                e.printStackTrace();
                LOG.error(e);
            }
        }
        return null;
    }

}
