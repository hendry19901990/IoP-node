package org.iop.version_1.structure.util;

import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.Package;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.util.GsonProvider;
import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;
import org.iop.version_1.structure.channels.endpoinsts.clients.conf.ClientChannelConfigurator;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * The Class <code>com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.util.PackageEncoder</code>
 * encode the package object to json string format
 * <p/>
 * Created by Roberto Requena - (rart3001@gmail.com) on 30/11/15.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class PackageEncoder implements Encoder.Text<Package>{

    private final Logger LOG = Logger.getLogger(ClassUtils.getShortClassName(PackageEncoder.class.getName()));
    /**
     * (non-javadoc)
     * @see Text#encode(Object)
     */
    @Override
    public String encode(Package packageToSend) throws EncodeException {
        return (packageToSend!=null)? GsonProvider.getGson().toJson(packageToSend):null;
    }

    @Override
    public void init(EndpointConfig config) {
    }

    @Override
    public void destroy() {
        LOG.info("PackageEnconder destroy method");
    }

}
