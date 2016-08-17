package com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.request;

import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.PackageContent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.profiles.Profile;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.util.GsonProvider;

/**
 * This class represents the the message to request if an actor is online.
 * Created by Manuel Perez (darkpriestrelative@gmail.com) on 16/08/16.
 */
public class IsActorOnlineMsgRequest extends PackageContent {

    /**
     * Represents the requested profile
     */
    private Profile requestedProfile;

    /**
     * Default construc with parameters
     * @param requestedProfile
     */
    public IsActorOnlineMsgRequest(Profile requestedProfile) {

        this.requestedProfile = requestedProfile;
    }

    /**
     * This method returns the requested profile
     *
     * @return requestedProfile
     */
    public Profile getRequestedProfile() {

        return requestedProfile;
    }

    /**
     * Generate the json representation
     * @return String
     */
    @Override
    public String toJson() {
        return GsonProvider.getGson().toJson(this, getClass());
    }

    /**
     * Get the object
     *
     * @param content
     * @return PackageContent
     */
    public static IsActorOnlineMsgRequest parseContent(String content) {
        return GsonProvider.getGson().fromJson(content, IsActorOnlineMsgRequest.class);
    }
}
