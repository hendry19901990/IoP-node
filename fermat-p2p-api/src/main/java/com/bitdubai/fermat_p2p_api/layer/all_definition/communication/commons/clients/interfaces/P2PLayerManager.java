package com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.interfaces;

import com.bitdubai.fermat_api.layer.all_definition.network_service.enums.NetworkServiceType;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.exceptions.CantSendMessageException;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.Package;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.PackageContent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.request.ActorListMsgRequest;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.abstract_classes.AbstractNetworkService;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.abstract_classes.AbstractNetworkService2;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.database.entities.NetworkServiceMessage;

import javax.annotation.Nullable;

/**
 * Created by Matias Furszyfer on 2016.07.06..
 */
public interface P2PLayerManager {

    void register(AbstractNetworkService2 abstractNetworkService);

    void register(NetworkChannel networkChannel);

    void setNetworkServicesRegisteredFalse();

    //todo: ver que poner en el destinationPublicKey, yo creo que ahí deberia ir el homeNode pero tengo que ver eso
    void sendMessage(NetworkServiceMessage packageContent, NetworkServiceType networkServiceType,@Nullable  String nodeDestinationPublicKey) throws CantSendMessageException;

    /**
     *
     * @param actorListMsgRequest
     * @param networkServiceType
     * @param nodeDestinationKey if this is nul the default value is the homeNode
     */
    void sendDiscoveryMessage(ActorListMsgRequest actorListMsgRequest, NetworkServiceType networkServiceType,@Nullable String nodeDestinationKey) throws CantSendMessageException;
}
