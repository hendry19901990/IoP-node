package com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.rest;

import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.rest.services.*;
import com.google.common.collect.ImmutableSet;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Set;

/**
 * The Class <code>com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.rest.JaxRsActivator</code>
 * <p/>
 * Created by Roberto Requena - (rart3001@gmail.com) on 13/11/15.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
@ApplicationPath("/rest/api/v1")
public class JaxRsActivator extends Application {

   /* @Inject
    private Instance<RestFulServices> services;

    @Override
    public Set<Class<?>> getClasses() {

        final Set<Class<?>> resourceList = new LinkedHashSet<Class<?>>();

        for (RestFulServices restFulServices: services) {
            resourceList.add(restFulServices.getClass());
        }

        return resourceList;
    } */

    private static final ImmutableSet<Class<?>> services = ImmutableSet.of(
            AvailableNodes.class,
            HelloResource.class,
            OnlineComponents.class,
            Profiles.class,
            Nodes.class,
            NetworkData.class,
            Monitoring.class,
            UserAuth.class,
            ConfigurationService.class,
            DeveloperDatabaseResource.class,
            DataBases.class,
            Actors.class
    );

    @Override
    public Set<Class<?>> getClasses() {
        return services;
    }

}