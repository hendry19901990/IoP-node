package com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.rest.services;

import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.DiscoveryQueryParameters;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.enums.ProfileStatus;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.profiles.ActorProfile;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.util.GsonProvider;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.NetworkNodePluginRoot;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.context.NodeContext;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.context.NodeContextItem;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.database.jpa.daos.JPADaoFactory;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.database.jpa.entities.ActorCatalog;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.exceptions.CantReadRecordDataBaseException;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.rest.RestFulServices;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ClassUtils;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.GZIP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The Class <code>com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.rest.Profiles</code>
 * <p/>
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 18/05/2016.
 *
 * @author  lnacosta
 * @version 1.0
 * @since   Java JDK 1.7
 */
@Path("/profiles")
public class Profiles implements RestFulServices {

    /**
     * Represent the LOG
     */
    private final Logger LOG = Logger.getLogger(ClassUtils.getShortClassName(Profiles.class));

    /**
     * Represent the pluginRoot
     */
    private NetworkNodePluginRoot pluginRoot;

    @POST
    @GZIP
    @Path("/actors")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getList(@FormParam("client_public_key") String clientIdentityPublicKey, @FormParam("discovery_params") String discoveryParam){

        LOG.info(" --------------------------------------------------------------------- ");
        LOG.info("Profiles - Starting listActors");
        JsonObject jsonObjectRespond = new JsonObject();

        try{

            /*
             * Construct the json object
             */
            DiscoveryQueryParameters discoveryQueryParameters = DiscoveryQueryParameters.parseContent(discoveryParam);

            LOG.info("clientIdentityPublicKey  = " + clientIdentityPublicKey);
            LOG.info("discoveryQueryParameters = " + discoveryQueryParameters.toJson());

            /*
             * hold the result list
             */
            List<ActorProfile> resultList = filterActors(discoveryQueryParameters, clientIdentityPublicKey);

            LOG.info("filteredLis.size() =" + resultList.size());

            /*
             * Convert the list to json representation
             */
            String jsonListRepresentation = GsonProvider.getGson().toJson(resultList, new TypeToken<List<ActorProfile>>() {
            }.getType());

            System.out.println(jsonListRepresentation);

            /*
             * Create the respond
             */
            jsonObjectRespond.addProperty("data", jsonListRepresentation);


        } catch (Exception e){

            LOG.error("requested list is not available.", e);
            jsonObjectRespond.addProperty("failure", "Requested list is not available");
        }

        String jsonString = GsonProvider.getGson().toJson(jsonObjectRespond);

        LOG.debug("jsonString.length() = " + jsonString.length());

        return Response.status(200).entity(jsonString).build();

    }


    @GET
    @GZIP
    @Path("/actor/photo/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFullPhoto(@PathParam("id") String actorIdentityPublicKey){

        LOG.info(" --------------------------------------------------------------------- ");
        LOG.info("Profiles - Starting listActors");
        JsonObject jsonObjectRespond = new JsonObject();

        try{

            LOG.info("actorIdentityPublicKey  = " + actorIdentityPublicKey);
            ActorCatalog actorsCatalog = JPADaoFactory.getActorCatalogDao().findById(actorIdentityPublicKey);

            /*
             * Create the respond
             */
            if (actorsCatalog.getPhoto() != null && actorsCatalog.getPhoto().length > 0) {
                jsonObjectRespond.addProperty("photo", Base64.encodeBase64String(actorsCatalog.getPhoto()));
                jsonObjectRespond.addProperty("success", Boolean.TRUE);
            }else {
                jsonObjectRespond.addProperty("success", Boolean.FALSE);
                jsonObjectRespond.addProperty("failure", "Actor photo is not available");
            }

        } catch (Exception e){

            e.printStackTrace();

            LOG.warn("Actor photo is not available, or actor no exist");
            jsonObjectRespond.addProperty("success", Boolean.FALSE);
            jsonObjectRespond.addProperty("failure", "Actor photo is not available, or actor no exist");
        }

        String jsonString = GsonProvider.getGson().toJson(jsonObjectRespond);
        return Response.status(200).entity(jsonString).build();

    }



    /**
     * Filter all actor component profiles from database that match with the given parameters.
     * We'll use the @clientIdentityPublicKey param to filter the actors who belongs to the client asking.
     *
     * @param discoveryQueryParameters parameters of the discovery done by the user.
     *
     * @return a list of actor profiles.
     */
    private List<ActorProfile> filterActors(final DiscoveryQueryParameters discoveryQueryParameters,
                                            final String                   clientIdentityPublicKey ) throws CantReadRecordDataBaseException {

        Map<String, ActorProfile> profileList = new HashMap<>();

        List<ActorCatalog> actorsList;

        int max    = 10;
        int offset =  0;

        if (discoveryQueryParameters.getMax() != null && discoveryQueryParameters.getMax() > 0)
            max = (discoveryQueryParameters.getMax() > 100) ? 100 : discoveryQueryParameters.getMax();

        if (discoveryQueryParameters.getOffset() != null && discoveryQueryParameters.getOffset() >= 0)
            offset = discoveryQueryParameters.getOffset();

        System.out.println("The max and offset applied in database are: max=" + max + " | offset=" + offset);

        actorsList = JPADaoFactory.getActorCatalogDao().findAll(discoveryQueryParameters, clientIdentityPublicKey, max, offset);

        if (discoveryQueryParameters.isOnline() != null && discoveryQueryParameters.isOnline())
            for (ActorCatalog actorsCatalog : actorsList)
             profileList.put(actorsCatalog.getId(), buildActorProfileFromActorCatalogRecordAndSetStatus(actorsCatalog, discoveryQueryParameters.getOriginalPhoto()));
        else
            for (ActorCatalog actorsCatalog : actorsList)
                profileList.put(actorsCatalog.getId(), buildActorProfileFromActorCatalogRecord(actorsCatalog, discoveryQueryParameters.getOriginalPhoto()));

        return new ArrayList<>(profileList.values());
    }

    /**
     * Build an Actor Profile from an Actor Catalog record.
     */
    private ActorProfile buildActorProfileFromActorCatalogRecord(final ActorCatalog actor, final Boolean originalPhoto){

        ActorProfile actorProfile = new ActorProfile();

        actorProfile.setIdentityPublicKey(actor.getId());
        actorProfile.setAlias(actor.getAlias());
        actorProfile.setName(actor.getName());
        actorProfile.setActorType(actor.getActorType());

        if(originalPhoto != null && originalPhoto)
            actorProfile.setPhoto(actor.getPhoto());
        else
            actorProfile.setPhoto(actor.getThumbnail());

        actorProfile.setExtraData(actor.getExtraData());
        actorProfile.setLocation(actor.getLocation());

        return actorProfile;
    }

    /**
     * Build an Actor Profile from an Actor Catalog record and set its status.
     */
    private ActorProfile buildActorProfileFromActorCatalogRecordAndSetStatus(final ActorCatalog actor, final Boolean originalPhoto){

        ActorProfile actorProfile = new ActorProfile();

        actorProfile.setIdentityPublicKey(actor.getId());
        actorProfile.setAlias(actor.getAlias());
        actorProfile.setName(actor.getName());
        actorProfile.setActorType(actor.getActorType());

        if(originalPhoto != null && originalPhoto)
            actorProfile.setPhoto            (actor.getPhoto());
        else
            actorProfile.setPhoto            (actor.getThumbnail());

        actorProfile.setExtraData        (actor.getExtraData());
        actorProfile.setLocation         (actor.getLocation());

        actorProfile.setStatus           (isActorOnline(actor));

        return actorProfile;
    }

    /**
     * Through this method we're going to determine a status for the actor profile.
     * First we'll check if the actor belongs to this node:
     *   if it belongs we'll check directly if he is online in the check-ins table
     *   if not we'll call to the other node.
     *
     * @param actorsCatalog  the record of the profile from the actors catalog table.
     *
     * @return an element of the ProfileStatus enum.
     */
    private ProfileStatus isActorOnline(ActorCatalog actorsCatalog) {

        try {

            if(actorsCatalog.getHomeNode().getId().equals(getPluginRoot().getIdentity().getPublicKey())) {

                if (actorsCatalog.getSession() != null)
                    return ProfileStatus.ONLINE;
                else
                    return ProfileStatus.OFFLINE;

            } else {

                return isActorOnlineInOtherNode(actorsCatalog);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ProfileStatus.UNKNOWN;
        }
    }

    /**
     * Through this method we're going to determine a status for the actor profile calling another node.
     *
     * @param actorsCatalog  the record of the profile from the actors catalog table.
     *
     * @return an element of the ProfileStatus enum.
     */
    private ProfileStatus isActorOnlineInOtherNode(final ActorCatalog actorsCatalog) {

        try {

            String nodeUrl = actorsCatalog.getHomeNode().getIp()+":"+actorsCatalog.getHomeNode().getDefaultPort();

            URL url = new URL("http://" + nodeUrl + "/fermat/rest/api/v1/online/component/actor/" + actorsCatalog.getId());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String respond = reader.readLine();

            if (conn.getResponseCode() == 200 && respond != null && respond.contains("success")) {

                JsonObject respondJsonObject = (JsonObject) GsonProvider.getJsonParser().parse(respond.trim());
                return respondJsonObject.get("isOnline").getAsBoolean() ? ProfileStatus.ONLINE : ProfileStatus.OFFLINE;

            } else {
                return ProfileStatus.UNKNOWN;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ProfileStatus.UNKNOWN;
        }
    }

    /**
     * Through this method we'll get the plugin root.
     *
     * @return a plugin root object.
     */
    private NetworkNodePluginRoot getPluginRoot() {

        if (pluginRoot == null)
            pluginRoot = (NetworkNodePluginRoot) NodeContext.get(NodeContextItem.PLUGIN_ROOT);

        return pluginRoot;
    }

}
