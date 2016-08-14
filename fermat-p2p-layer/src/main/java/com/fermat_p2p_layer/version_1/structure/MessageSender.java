package com.fermat_p2p_layer.version_1.structure;

import com.bitdubai.fermat_api.layer.all_definition.network_service.enums.NetworkServiceType;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.exceptions.CantSendMessageException;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.interfaces.NetworkChannel;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.PackageContent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.database.entities.NetworkServiceMessage;

import java.util.UUID;

/**
 * Created by mati on 14/08/16.
 * Esta clase deberia guardar la relación entre channel + public key del componente. así se sabe hacia donde va el mensaje y si el canal debe seguir abierto
 */
public class MessageSender {


    private final NetworkChannel client;

    public MessageSender(NetworkChannel client) {
        this.client = client;
    }

    public void sendMessage(NetworkServiceMessage networkServiceMessage, NetworkServiceType networkServiceType, String destinationPublicKey) throws CantSendMessageException {
        UUID packageId = client.sendMessage(networkServiceMessage,networkServiceType,destinationPublicKey);
    }
}
