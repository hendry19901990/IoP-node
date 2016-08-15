package com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.respond.checkin;

import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.respond.MsgRespond;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.util.GsonProvider;
import com.google.gson.annotations.Expose;

/**
 * Created by mati on 14/08/16.
 */
public class ClientCheckInRespond extends MsgRespond {

    @Expose(serialize = false, deserialize = false)
    private String nodeUri;
    @Expose(serialize = false, deserialize = false)
    private String homeNodePk;

    /**
     * Constructor with parameters
     *
     * @param status
     * @param details
     */
    public ClientCheckInRespond(STATUS status, String details) {
        super(status, details);
    }

    @Override
    public String toJson() {
        return GsonProvider.getGson().toJson(this);
    }

    public static ClientCheckInRespond parseContent(String content){
        return GsonProvider.getGson().fromJson(content,ClientCheckInRespond.class);
    }


    public void setNodeUri(String nodeUri) {
        this.nodeUri = nodeUri;
    }

    public void setHomeNodePk(String homeNodePk) {
        this.homeNodePk = homeNodePk;
    }

    public String getNodeUri() {
        return nodeUri;
    }

    public String getHomeNodePk() {
        return homeNodePk;
    }
}
