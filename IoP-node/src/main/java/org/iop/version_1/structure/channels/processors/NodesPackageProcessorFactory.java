package org.iop.version_1.structure.channels.processors;

import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.PackageType;
import org.iop.version_1.structure.channels.processors.clients.ActorListRequestProcessor;
import org.iop.version_1.structure.channels.processors.clients.MessageTransmitProcessor;
import org.iop.version_1.structure.channels.processors.clients.checkin.CheckInActorRequestProcessor;
import org.iop.version_1.structure.channels.processors.clients.checkin.CheckInClientRequestProcessor;
import org.iop.version_1.structure.channels.processors.clients.checkin.CheckInNetworkServiceRequestProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * The Class <code>com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.channels.processors.NodesPackageProcessorFactory</code>
 * <p/>
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 05/08/2016.
 *
 * @author  lnacosta
 * @version 1.0
 * @since   Java JDK 1.7
 */
public class NodesPackageProcessorFactory {


    public static Map<String,PackageProcessor> getClientPackageProcessorsByPackageType() {

        Map<String,PackageProcessor> packageProcessors = new HashMap();
        packageProcessors.put(PackageType.ACTOR_LIST_REQUEST.name(),new ActorListRequestProcessor());
        packageProcessors.put(PackageType.CHECK_IN_CLIENT_REQUEST.name(),new CheckInClientRequestProcessor());
        packageProcessors.put(PackageType.CHECK_IN_NETWORK_SERVICE_REQUEST.name(),new CheckInNetworkServiceRequestProcessor());
        packageProcessors.put(PackageType.CHECK_IN_ACTOR_REQUEST.name(),new CheckInActorRequestProcessor());
        packageProcessors.put(PackageType.MESSAGE_TRANSMIT.name(),new MessageTransmitProcessor());
        //todo: faltan processors
        return packageProcessors;
    }

}
