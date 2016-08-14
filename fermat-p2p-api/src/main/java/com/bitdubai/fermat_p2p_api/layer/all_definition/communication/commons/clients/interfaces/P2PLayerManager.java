package com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.interfaces;

import com.bitdubai.fermat_api.layer.all_definition.network_service.enums.NetworkServiceType;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.Package;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.PackageContent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.abstract_classes.AbstractNetworkService;

/**
 * Created by Matias Furszyfer on 2016.07.06..
 */
public interface P2PLayerManager {

    void register(AbstractNetworkService abstractNetworkService);

    void register(NetworkChannel networkChannel);

    void setNetworkServicesRegisteredFalse();

    void sendMessage(PackageContent packageContent, NetworkServiceType networkServiceType,String destinationPublicKey);



}
