package com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.respond;

import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.enums.ProfileStatus;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.profiles.ActorProfile;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.util.GsonProvider;

import java.util.UUID;

/**
 * Created by Manuel Perez P. (darkpriestrelative@gmail.com) on 17/08/16.
 */
public class IsActorOnlineMsgRespond extends MsgRespond {

    /**
     * Represents the query Id
     */
    private UUID queryId;

    /**
     * Represents the actor profile
     */
    private ActorProfile requestedProfile;

    /**
     * Represents the profile status from the requested profile
     */
    private ProfileStatus profileStatus;

    /**
     * Constructor with parameters
     *
     * @param packageId
     * @param status
     * @param details
     */
    public IsActorOnlineMsgRespond(
            UUID packageId,
            STATUS status,
            String details,
            ActorProfile requestedProfile,
            ProfileStatus profileStatus,
            UUID queryId
            ) {
        super(
                packageId,
                status,
                details);
        this.requestedProfile = requestedProfile;
        this.profileStatus = profileStatus;
        this.queryId = queryId;
    }

    /**
     * This method returns the query Id
     * @return
     */
    public UUID getQueryId() {
        return queryId;
    }

    /**
     * This method returns the requested profile
     * @return
     */
    public ActorProfile getRequestedProfile() {
        return requestedProfile;
    }

    /**
     * This method returns the profile status
     * @return
     */
    public ProfileStatus getProfileStatus() {
        return profileStatus;
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
    public static IsActorOnlineMsgRespond parseContent(String content) {
        return GsonProvider.getGson().fromJson(content, IsActorOnlineMsgRespond.class);
    }
}
