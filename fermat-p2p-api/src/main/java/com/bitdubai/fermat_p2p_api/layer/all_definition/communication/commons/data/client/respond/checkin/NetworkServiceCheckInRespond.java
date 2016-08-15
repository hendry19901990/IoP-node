package com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.respond.checkin;

import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.respond.MsgRespond;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.util.GsonProvider;
import com.google.gson.annotations.Expose;

/**
 * Created by mati on 14/08/16.
 */
public class NetworkServiceCheckInRespond extends MsgRespond {

    @Expose(serialize = true, deserialize = true)
    private String publicKey;

    /**
     * Constructor with parameters
     *
     * @param status
     * @param details
     */
    public NetworkServiceCheckInRespond(STATUS status, String details) {
        super(status, details);
    }

    @Override
    public String toJson() {
        return GsonProvider.getGson().toJson(this);
    }

    public static NetworkServiceCheckInRespond parseContent(String content){
        return GsonProvider.getGson().fromJson(content,NetworkServiceCheckInRespond.class);
    }


    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
