package org.iop.version_1.structure.channels.processors.clients;

import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.Package;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.request.IsActorOnlineMsgRequest;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.request.UpdateActorProfileMsgRequest;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.respond.ACKRespond;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.respond.IsActorOnlineMsgRespond;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.enums.ProfileStatus;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.profiles.ActorProfile;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.HeadersAttName;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.PackageType;
import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;
import org.iop.version_1.structure.channels.endpoinsts.FermatWebSocketChannelEndpoint;
import org.iop.version_1.structure.channels.processors.PackageProcessor;
import org.iop.version_1.structure.database.jpa.daos.ActorCatalogDao;
import org.iop.version_1.structure.database.jpa.daos.JPADaoFactory;
import org.iop.version_1.structure.database.jpa.entities.ActorCatalog;

import javax.websocket.Session;
import java.io.IOException;

/**
 * Created by Manuel Perez P. (darkpriestrelative@gmail.com) on 16/08/16.
 */
public class UpdateProfileRequestProcessor extends PackageProcessor {

    /**
     * Represent the LOG
     */
    private final Logger LOG = Logger.getLogger(ClassUtils.getShortClassName(UpdateProfileRequestProcessor.class));

    /**
     * Default constructor
     */
    public UpdateProfileRequestProcessor() {
        super(PackageType.UPDATE_ACTOR_PROFILE_REQUEST);
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
        UpdateActorProfileMsgRequest isActorOnlineMsgRequest = UpdateActorProfileMsgRequest
                .parseContent(packageReceived.getContent());

        //Profile requested
        ActorProfile actorProfile = (ActorProfile) isActorOnlineMsgRequest.getProfileToUpdate();

        try{
            //Get the profile from node database
            ActorCatalogDao actorCatalogDao = JPADaoFactory.getActorCatalogDao();

            ActorCatalog actorCatalog = new ActorCatalog(actorProfile,null,actorProfile.getHomeNodePublicKey(),null);


            actorCatalogDao.update(actorCatalog);

            //Respond the request
            ACKRespond isActorOnlineMsgRespond = new ACKRespond(
                    packageReceived.getPackageId(),
                    IsActorOnlineMsgRespond.STATUS.SUCCESS,
                    IsActorOnlineMsgRespond.STATUS.SUCCESS.toString());

            //Create instance
            if (session.isOpen()) {

                return Package.createInstance(
                        isActorOnlineMsgRespond.toJson()                      ,
                        packageReceived.getNetworkServiceTypeSource()                  ,
                        PackageType.ACK                         ,
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
                ACKRespond actorListMsgRespond = new ACKRespond(
                        packageReceived.getPackageId(),
                        IsActorOnlineMsgRespond.STATUS.FAIL,
                        exception.getLocalizedMessage()
                );

                return Package.createInstance(
                        actorListMsgRespond.toJson()                      ,
                        packageReceived.getNetworkServiceTypeSource()                  ,
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
