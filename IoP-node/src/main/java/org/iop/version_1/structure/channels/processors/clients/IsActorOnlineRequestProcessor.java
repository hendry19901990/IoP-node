package org.iop.version_1.structure.channels.processors.clients;

import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.Package;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.request.IsActorOnlineMsgRequest;
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
public class IsActorOnlineRequestProcessor extends PackageProcessor {

    /**
     * Represent the LOG
     */
    private final Logger LOG = Logger.getLogger(ClassUtils.getShortClassName(isActorOnlineRequestProcessor.class));

    /**
     * Default constructor
     */
    public isActorOnlineRequestProcessor() {
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
        ActorProfile actorProfile = (ActorProfile) isActorOnlineMsgRequest.getRequestedProfile();

        try{
            //Get the profile from node database
            ActorCatalogDao actorCatalogDao = JPADaoFactory.getActorCatalogDao();
            ActorCatalog actorCatalog = actorCatalogDao.findById(actorProfile.getIdentityPublicKey());
            if(actorCatalog==null){
                //TODO: respond with status unknown
            } else{
                //Checking the session, if the session is null the actor is offline
                ProfileStatus profileStatus;
                if(actorCatalog.getSessionId()!=null){
                    profileStatus = ProfileStatus.ONLINE;
                } else {
                    profileStatus = ProfileStatus.OFFLINE;
                }
                //TODO: respond with the status from database
            }

        } catch(Exception e){
            //Todo: catch properly this
        }
    }
}
