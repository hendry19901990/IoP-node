package org.iop.version_1.structure.channels.processors.clients.checkin;

import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.Package;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.request.CheckInProfileMsgRequest;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.respond.ACKRespond;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.profiles.NetworkServiceProfile;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.HeadersAttName;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.PackageType;
import org.apache.commons.lang.ClassUtils;
import org.iop.version_1.structure.channels.endpoinsts.FermatWebSocketChannelEndpoint;
import org.iop.version_1.structure.channels.processors.PackageProcessor;
import org.iop.version_1.structure.database.jpa.daos.JPADaoFactory;
import org.iop.version_1.structure.database.jpa.entities.NetworkService;
import org.apache.log4j.Logger;

import javax.websocket.Session;

/**
 * The Class <code>com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.channels.processors.clients.CheckInNetworkServiceRequestProcessor</code>
 * process all packages received the type <code>MessageType.CHECK_IN_NETWORK_SERVICE_REQUEST</code><p/>
 * <p/>
 * Created by Roberto Requena - (rart3001@gmail.com) on 06/12/15.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class CheckInNetworkServiceRequestProcessor extends PackageProcessor {

    /**
     * Represent the LOG
     */
    private final Logger LOG = Logger.getLogger(ClassUtils.getShortClassName(CheckInNetworkServiceRequestProcessor.class));

    /**
     * Constructor
     */
    public CheckInNetworkServiceRequestProcessor() {
        super(PackageType.CHECK_IN_NETWORK_SERVICE_REQUEST);
    }

    /**
     * (non-javadoc)
     *
     * @see PackageProcessor#processingPackage(Session, Package, FermatWebSocketChannelEndpoint)
     */
    @Override
    public Package processingPackage(Session session, Package packageReceived, FermatWebSocketChannelEndpoint channel) {

        LOG.info(" ---------------------------------------------------------------------- ");
        LOG.info("Processing new package received: "+packageReceived.getPackageType());

        String destinationIdentityPublicKey = (String) session.getUserProperties().get(HeadersAttName.CPKI_ATT_HEADER_NAME);

        CheckInProfileMsgRequest messageContent = CheckInProfileMsgRequest.parseContent(packageReceived.getContent());
        NetworkServiceProfile networkServiceProfile = null;

        try {

            /*
             * Obtain the profile of the network service
             */
            networkServiceProfile = (NetworkServiceProfile) messageContent.getProfileToRegister();

            /*
             * Save the network service
             */
            NetworkService networkService = new NetworkService(networkServiceProfile);
            networkService.setSessionId(session.getId());
            JPADaoFactory.getNetworkServiceDao().save(networkService);

            /*
             * If all ok, respond whit success message
             */
            ACKRespond respondProfileCheckInMsj = new ACKRespond(packageReceived.getPackageId(),ACKRespond.STATUS.SUCCESS, ACKRespond.STATUS.SUCCESS.toString());

            return Package.createInstance(
                    respondProfileCheckInMsj.toJson(),
                    packageReceived.getNetworkServiceTypeSource(),
                    PackageType.CHECK_IN_NETWORK_SERVICE_RESPOND,
                    channel.getChannelIdentity().getPrivateKey(),
                    destinationIdentityPublicKey
            );
//            channel.sendPackage(session, respondProfileCheckInMsj.toJson(), packageReceived.getNetworkServiceTypeSource(), PackageType.ACK, destinationIdentityPublicKey);

        } catch (Exception exception) {

            try {
                LOG.info(" ---------------------------------------------------------------------- ");
                LOG.error(exception);

                /*
                 * Respond whit fail message
                 */
                ACKRespond respondProfileCheckInMsj = new ACKRespond(packageReceived.getPackageId(),ACKRespond.STATUS.FAIL, exception.getMessage());

                return Package.createInstance(
                        respondProfileCheckInMsj.toJson(),
                        packageReceived.getNetworkServiceTypeSource(),
                        PackageType.CHECK_IN_NETWORK_SERVICE_RESPOND,
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

}
