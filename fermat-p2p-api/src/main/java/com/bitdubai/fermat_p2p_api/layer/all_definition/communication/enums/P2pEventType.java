package com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums;

import com.bitdubai.fermat_api.layer.all_definition.enums.Platforms;
import com.bitdubai.fermat_api.layer.all_definition.enums.interfaces.FermatEventEnum;
import com.bitdubai.fermat_api.layer.all_definition.events.common.GenericEventListener;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEventListener;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEventMonitor;
import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.events.NetworkClientActorFoundEvent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.events.NetworkClientActorListReceivedEvent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.events.NetworkClientActorUnreachableEvent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.events.NetworkClientCallConnectedEvent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.events.NetworkClientConnectedToNodeEvent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.events.NetworkClientConnectionClosedEvent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.events.NetworkClientConnectionLostEvent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.events.NetworkClientConnectionSuccessEvent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.events.NetworkClientNewMessageDeliveredEvent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.events.NetworkClientNewMessageFailedEvent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.events.NetworkClientNewMessageTransmitEvent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.events.NetworkClientProfileRegisteredEvent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.events.NetworkClientRegisteredEvent;

/**
 * The enum <code>com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.P2pEventType</code>
 * represent the different type for the events of cry api.<p/>
 * <p/>
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 16/09/2015.
 * Update by Roberto Requena (rart3001@gmail.com) on 17/09/2015.
 *
 * @author  lnacosta, Rart3001
 * @version 1.0
 * @since   Java JDK 1.7
 */
public enum P2pEventType implements FermatEventEnum {

    /**
     * INIT NETWORK NODE-CLIENT TEMPLATE EVENTS
     */

    NETWORK_CLIENT_ACTOR_PROFILE_REGISTERED("NCAPR"){
        public NetworkClientProfileRegisteredEvent getNewEvent() { return new NetworkClientProfileRegisteredEvent(this); }
    },
    NETWORK_CLIENT_ACTOR_PROFILE_UPDATED("NCAPU"){
        public NetworkClientProfileRegisteredEvent getNewEvent() { return new NetworkClientProfileRegisteredEvent(this); }
    },
    NETWORK_CLIENT_CONNNECTED_TO_NODE("NCCTN"){
        public NetworkClientConnectedToNodeEvent getNewEvent() {    return new NetworkClientConnectedToNodeEvent(this);}
    },
    NETWORK_CLIENT_CONNECTION_SUCCESS("NCCSU"){
        public NetworkClientConnectionSuccessEvent getNewEvent() { return new NetworkClientConnectionSuccessEvent(this); }
    },
    NETWORK_CLIENT_CONNECTION_LOST("NCCL"){
        public NetworkClientConnectionLostEvent getNewEvent() { return new NetworkClientConnectionLostEvent(this); }
    },
    NETWORK_CLIENT_CONNECTION_CLOSED("NCCC"){
        public NetworkClientConnectionClosedEvent getNewEvent() { return new NetworkClientConnectionClosedEvent(this); }
    },
    NETWORK_CLIENT_NETWORK_SERVICE_PROFILE_REGISTERED("NCNSPR"){
        public NetworkClientProfileRegisteredEvent getNewEvent() { return new NetworkClientProfileRegisteredEvent(this); }
    },
    NETWORK_CLIENT_REGISTERED("NCR"){
        public NetworkClientRegisteredEvent getNewEvent() { return new NetworkClientRegisteredEvent(this); }
    },
    NETWORK_CLIENT_ACTOR_FOUND("NCAF"){
        public NetworkClientActorFoundEvent getNewEvent() { return new NetworkClientActorFoundEvent(this); }
    },
    NETWORK_CLIENT_ACTOR_LIST_RECEIVED("NCALR"){
        public NetworkClientActorListReceivedEvent getNewEvent() { return new NetworkClientActorListReceivedEvent(this); }
    },
    NETWORK_CLIENT_ACTOR_UNREACHABLE("NCAD"){
        public NetworkClientActorUnreachableEvent getNewEvent() { return new NetworkClientActorUnreachableEvent(this); }
    },
    NETWORK_CLIENT_NEW_MESSAGE_TRANSMIT("NCNWT"){
        public NetworkClientNewMessageTransmitEvent getNewEvent() { return new NetworkClientNewMessageTransmitEvent(this); }
    },
    NETWORK_CLIENT_SENT_MESSAGE_DELIVERED("NCSENTMD"){
        public NetworkClientNewMessageDeliveredEvent getNewEvent() { return new NetworkClientNewMessageDeliveredEvent(this); }
    },
    NETWORK_CLIENT_SENT_MESSAGE_FAILED("NCSENTMF"){
        public NetworkClientNewMessageFailedEvent getNewEvent() { return new NetworkClientNewMessageFailedEvent(this); }
    },
    NETWORK_CLIENT_CALL_CONNECTED("NCACC"){
        public NetworkClientCallConnectedEvent getNewEvent() { return new NetworkClientCallConnectedEvent(this); }
    },

    /**
     * END  NETWORK NODE-CLIENT TEMPLATE EVENTS
     */

    ;

    /**
     * Represent the code of the message status
     */
    private final String code;

    /**
     * Constructor whit parameter
     *
     * @param code the valid code
     */
    P2pEventType(String code) {
        this.code = code;
    }

    public FermatEventListener getNewListener(FermatEventMonitor fermatEventMonitor) {
        return new GenericEventListener<>(this, fermatEventMonitor);
    }

    /**
     * Return the enum by the code
     *
     * @param code the valid code
     * @return P2pEventType enum
     * @throws InvalidParameterException error with is no a valid code
     */
    public static P2pEventType getByCode(String code) throws InvalidParameterException {
        for (P2pEventType p2pEventType : P2pEventType.values()) {
            if (p2pEventType.code.equals(code)) {
                return p2pEventType;
            }
        }
        throw new InvalidParameterException(InvalidParameterException.DEFAULT_MESSAGE, null, "Code Received: " + code, "This code isn't valid for the P2pEventType Enum");
    }

    @Override
    public Platforms getPlatform() {
        return null;
    }

    @Override
    public String getCode() {
        return this.code;
    }


    @Override
    public String toString() {
        return getCode();
    }

}
