package com.fermat_p2p_layer.version_1.structure;

import com.bitdubai.fermat_api.layer.all_definition.network_service.enums.NetworkServiceType;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.exceptions.CantSendMessageException;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.interfaces.NetworkChannel;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.PackageContent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.request.ActorListMsgRequest;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.database.entities.NetworkServiceMessage;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.PackageType;
import com.fermat_p2p_layer.version_1.P2PLayerPluginRoot;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mati on 14/08/16.
 * Esta clase deberia guardar la relación entre channel + public key del componente. así se sabe hacia donde va el mensaje y si el canal debe seguir abierto
 */
public class MessageSender {


    private final P2PLayerPluginRoot p2PLayerPluginRoot;

    /**
     * PackageId + NetworkServiceType
     */
    private ConcurrentHashMap<UUID,String> messagesSendedWaitingForAck;


    public MessageSender(P2PLayerPluginRoot p2PLayerPluginRoot) {
        this.p2PLayerPluginRoot = p2PLayerPluginRoot;
        messagesSendedWaitingForAck = new ConcurrentHashMap<>();
    }

    public void sendMessage(NetworkServiceMessage networkServiceMessage, NetworkServiceType networkServiceType, String nodeDestinationPublicKey) throws CantSendMessageException {
        UUID packageId = p2PLayerPluginRoot.getNetworkClient().sendMessage(networkServiceMessage, PackageType.MESSAGE_TRANSMIT,networkServiceType,nodeDestinationPublicKey);
        messagesSendedWaitingForAck.put(packageId,networkServiceType.getCode());
    }

    public void sendDiscoveryMessage(ActorListMsgRequest networkServiceMessage, NetworkServiceType networkServiceType, String nodeDestinationPublicKey) throws CantSendMessageException {
        //todo: esto deberia ser para todos los discovery y no solo para el actorList
        UUID packageId = p2PLayerPluginRoot.getNetworkClient().sendMessage(networkServiceMessage,PackageType.ACTOR_LIST_REQUEST,networkServiceType,nodeDestinationPublicKey);
        messagesSendedWaitingForAck.put(packageId,networkServiceType.getCode());
    }

    /**
     *
     * @param packageId
     * @return network service type
     */
    public String packageAck(UUID packageId){
        return messagesSendedWaitingForAck.remove(packageId);
    }
}
