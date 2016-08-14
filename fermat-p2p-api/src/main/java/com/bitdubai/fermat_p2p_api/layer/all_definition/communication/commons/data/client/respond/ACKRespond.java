package com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.respond;

import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.PackageContent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.util.GsonProvider;
import com.google.gson.annotations.Expose;

import java.util.UUID;

/**
 * Created by mati on 14/08/16.
 */
public class ACKRespond extends MsgRespond {

    @Expose(serialize = true, deserialize = true)
    private UUID packageId;

    /**
     * Constructor with parameters
     *
     * @param status
     * @param details
     */
    public ACKRespond(STATUS status, String details,UUID packageId) {
        super(status, details);
        this.packageId = packageId;
    }

    public UUID getPackageId() {
        return packageId;
    }

    public void setPackageId(UUID packageId) {
        this.packageId = packageId;
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
    public static ACKRespond parseContent(String content) {
        return GsonProvider.getGson().fromJson(content, ACKRespond.class);
    }

    @Override
    public String toString() {
        return "ACKRespond{" +
                "packageId=" + packageId +
                '}';
    }
}
