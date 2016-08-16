package com.fermat_p2p_layer.version_1;

import com.bitdubai.fermat_api.CantStartPluginException;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.abstract_classes.AbstractPlugin;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededAddonReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.EventManager;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginVersionReference;
import com.bitdubai.fermat_api.layer.all_definition.enums.Addons;
import com.bitdubai.fermat_api.layer.all_definition.enums.Layers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Platforms;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEvent;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEventHandler;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEventListener;
import com.bitdubai.fermat_api.layer.all_definition.network_service.enums.NetworkServiceType;
import com.bitdubai.fermat_api.layer.all_definition.util.Version;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.events.*;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.exceptions.CantSendMessageException;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.interfaces.NetworkChannel;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.interfaces.P2PLayerManager;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.client.request.ActorListMsgRequest;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.enums.UpdateTypes;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.abstract_classes.AbstractActorNetworkService2;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.abstract_classes.AbstractNetworkService2;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.database.entities.NetworkServiceMessage;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.profiles.ActorProfile;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.P2pEventType;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.events.*;
import com.fermat_p2p_layer.version_1.structure.MessageSender;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Matias Furszyfer on 2016.07.06..
 */
public class P2PLayerPluginRoot extends AbstractPlugin implements P2PLayerManager {

    @NeededAddonReference(platform = Platforms.PLUG_INS_PLATFORM, layer = Layers.PLATFORM_SERVICE, addon = Addons.EVENT_MANAGER)
    private EventManager eventManager;


    private ConcurrentHashMap<NetworkServiceType, AbstractNetworkService2> networkServices;
    private NetworkChannel client;

    private MessageSender messageSender;

    /**
     * Represent the communicationSupervisorPendingMessagesAgent
     */
//    private CommunicationSupervisorPendingMessagesAgent communicationSupervisorPendingMessagesAgent;

    private List<FermatEventListener> listenersAdded;


    public P2PLayerPluginRoot() {
        super(new PluginVersionReference(new Version()));
    }

    @Override
    public void start() throws CantStartPluginException {
        networkServices = new ConcurrentHashMap<>();
        this.listenersAdded        = new CopyOnWriteArrayList<>();

        messageSender = new MessageSender(this);

        /**
         * Initialize event listeners
         */
        initializeNetworkServiceListeners();

        try {
//            this.communicationSupervisorPendingMessagesAgent = new CommunicationSupervisorPendingMessagesAgent(this);
//            this.communicationSupervisorPendingMessagesAgent.start();
        }catch (Exception e){
            e.printStackTrace();

        }


        super.start();
    }


    /**
     * Initializes all event listener and configure
     */
    private void initializeNetworkServiceListeners() {

        /*
         * 1. Listen and handle Network Client Registered Event
         */
        FermatEventListener networkClientRegistered = eventManager.getNewListener(P2pEventType.NETWORK_CLIENT_REGISTERED);
        networkClientRegistered.setEventHandler(new FermatEventHandler() {
            @Override
            public void handleEvent(FermatEvent fermatEvent) throws FermatException {

                if (client.isConnected()) {
                    for (final AbstractNetworkService2 abstractNetworkService : networkServices.values()) {
                        try {
                            System.out.println(abstractNetworkService.getProfile().getNetworkServiceType() + ": se está por registrar..." + abstractNetworkService.isRegistered());
                            if (!abstractNetworkService.isRegistered())
                                messageSender.registerNetworkServiceProfile(abstractNetworkService.getProfile());
                            else System.out.println("Ns: "+abstractNetworkService.getNetworkServiceType()+", already registered..");
                        } catch (FermatException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    System.out.println("algo feo pasó");
                }
            }
        });
        eventManager.addListener(networkClientRegistered);
        listenersAdded.add(networkClientRegistered);

        /*
         * 2. Listen and handle Network Client Network Service Registered Event
         */
        FermatEventListener networkCLientProfileRegistered = eventManager.getNewListener(P2pEventType.NETWORK_CLIENT_NETWORK_SERVICE_PROFILE_REGISTERED);
        networkCLientProfileRegistered.setEventHandler(new FermatEventHandler<NetworkClientProfileRegisteredEvent>() {
            @Override
            public void handleEvent(NetworkClientProfileRegisteredEvent fermatEvent) throws FermatException {
                System.out.println("NETWORK SERVICES STARTED:" + networkServices.size());
                NetworkServiceType networkServiceType = messageSender.packageAck(fermatEvent.getPackageId());
                System.out.println("NETWORK SERVICE TYPE ? "+networkServiceType);
                AbstractNetworkService2 abstractNetworkService2 = networkServices.get(networkServiceType);
                if (abstractNetworkService2.isStarted()){
                    abstractNetworkService2.handleNetworkServiceRegisteredEvent();
                }else{
                    System.out.println("NetworkClientProfileRegisteredEvent Ns: "+abstractNetworkService2.getNetworkServiceType()+" is not started");
                }
            }
        });
        eventManager.addListener(networkCLientProfileRegistered);
        listenersAdded.add(networkCLientProfileRegistered);

        /*
         * 3. Listen and handle Network Client Connection Closed Event
         */
        FermatEventListener connectionClosed = eventManager.getNewListener(P2pEventType.NETWORK_CLIENT_CONNECTION_CLOSED);
        connectionClosed.setEventHandler(new FermatEventHandler() {
            @Override
            public void handleEvent(FermatEvent fermatEvent) throws FermatException {
                System.out.println("NETWORK SERVICES STARTED:" + networkServices.size());
                setNetworkServicesRegisteredFalse();
            }
        });
        eventManager.addListener(connectionClosed);
        listenersAdded.add(connectionClosed);

        /*
         * 4. Listen and handle Network Client Connection Lost Event
         */
        FermatEventListener connectionLostListener = eventManager.getNewListener(P2pEventType.NETWORK_CLIENT_CONNECTION_LOST);
        connectionLostListener.setEventHandler(new FermatEventHandler<NetworkClientConnectionLostEvent>() {
            @Override
            public void handleEvent(NetworkClientConnectionLostEvent fermatEvent) throws FermatException {
                System.out.println("P2PLayer, NetworkClientConnectionLostEvent");
                for (AbstractNetworkService2 abstractNetworkService2 : getNetworkServices()) {
                    if (abstractNetworkService2.isStarted())
                        abstractNetworkService2.handleNetworkClientConnectionLostEvent(fermatEvent.getCommunicationChannel());
                }
            }
        });
        eventManager.addListener(connectionLostListener);
        listenersAdded.add(connectionLostListener);

         /*
         * 5. Listen and handle Network Client Sent Message Delivered Event
         */
//        FermatEventListener networkClientCallConnectedListener = eventManager.getNewListener(P2pEventType.NETWORK_CLIENT_CALL_CONNECTED);
//        networkClientCallConnectedListener.setEventHandler(new NetworkClientCallConnectedEventHandler(this));
//        eventManager.addListener(networkClientCallConnectedListener);
//        listenersAdded.add(networkClientCallConnectedListener);

        /*
         * 6. Listen and handle Actor Found Event
         */
//        FermatEventListener actorFoundListener = eventManager.getNewListener(P2pEventType.NETWORK_CLIENT_ACTOR_FOUND);
//        actorFoundListener.setEventHandler(new NetworkClientActorFoundEventHandler(this));
//        eventManager.addListener(actorFoundListener);
//        listenersAdded.add(actorFoundListener);

        /*
         * 7. Listen and handle Network Client New Message Transmit Event
         */
        FermatEventListener newMessageTransmitListener = eventManager.getNewListener(P2pEventType.NETWORK_CLIENT_NEW_MESSAGE_TRANSMIT);
        newMessageTransmitListener.setEventHandler(new FermatEventHandler<NetworkClientNewMessageTransmitEvent>() {
            @Override
            public void handleEvent(NetworkClientNewMessageTransmitEvent fermatEvent) throws FermatException {

                AbstractNetworkService2 abstractNetworkService2 = networkServices.get(fermatEvent.getNetworkServiceTypeSource());

                if (abstractNetworkService2.isStarted())
                    abstractNetworkService2.onMessageReceived(fermatEvent.getContent());
                else System.out.println("NetworkService message recive event problem: network service off , NS:"+abstractNetworkService2.getProfile().getNetworkServiceType());

            }
        });
        eventManager.addListener(newMessageTransmitListener);
        listenersAdded.add(newMessageTransmitListener);


        FermatEventListener actorRegistered = eventManager.getNewListener(P2pEventType.NETWORK_CLIENT_ACTOR_PROFILE_REGISTERED);
        actorRegistered.setEventHandler(new FermatEventHandler<NetworkClientProfileRegisteredEvent>() {
            @Override
            public void handleEvent(NetworkClientProfileRegisteredEvent fermatEvent) throws FermatException {

                System.out.println("NETWORK_CLIENT_ACTOR_PROFILE_REGISTERED -> NETWORK SERVICES STARTED:" + networkServices.size());
                NetworkServiceType networkServiceType = messageSender.packageAck(fermatEvent.getPackageId());
                System.out.println("NETWORK_CLIENT_ACTOR_PROFILE_REGISTERED -> ACTOR REGISTERING NETWORK SERVICE TYPE ? "+networkServiceType);
                AbstractNetworkService2 abstractNetworkService2 = networkServices.get(networkServiceType);
                if (abstractNetworkService2.isStarted()){
                    ((AbstractActorNetworkService2)abstractNetworkService2).onActorRegistered(fermatEvent.getPublicKey());
                }else{
                    System.out.println("NETWORK_CLIENT_ACTOR_PROFILE_REGISTERED -> NetworkClientProfileRegisteredEvent Ns: "+abstractNetworkService2.getNetworkServiceType()+" is not started");
                }
            }
        });
        eventManager.addListener(actorRegistered);
        listenersAdded.add(actorRegistered);

        /*
         * 8. Listen and handle Network Client Sent Message Delivered Event
         */
//        FermatEventListener sentMessageDeliveredListener = eventManager.getNewListener(P2pEventType.NETWORK_CLIENT_SENT_MESSAGE_DELIVERED);
//        sentMessageDeliveredListener.setEventHandler(new NetworkClientSentMessageDeliveredEventHandler(this));
//        eventManager.addListener(sentMessageDeliveredListener);
//        listenersAdded.add(sentMessageDeliveredListener);

        /*
         * 9. Listen and handle Network Client Actor Unreachable Event
         */
//        FermatEventListener actorUnreachableListener = eventManager.getNewListener(P2pEventType.NETWORK_CLIENT_ACTOR_UNREACHABLE);
//        actorUnreachableListener.setEventHandler(new NetworkClientActorUnreachableEventHandler(this));
//        eventManager.addListener(actorUnreachableListener);
//        listenersAdded.add(actorUnreachableListener);

        /*
         * 10. Listen and handle Network Client Actor List Received Event
         */
        FermatEventListener actorListReceivedListener = eventManager.getNewListener(P2pEventType.NETWORK_CLIENT_ACTOR_LIST_RECEIVED);
        actorListReceivedListener.setEventHandler(new FermatEventHandler<NetworkClientActorListReceivedEvent>() {
            @Override
            public void handleEvent(NetworkClientActorListReceivedEvent fermatEvent) throws FermatException {
                NetworkServiceType networkServiceType = messageSender.packageAck(fermatEvent.getPackageId());
                //todo: no hace falta pasar el type del ns acá..
                AbstractNetworkService2 abstractNetworkService2 = networkServices.get(fermatEvent.getNetworkServiceType());
                if (abstractNetworkService2.isStarted()) {
                    System.out.println("P2PLayer discoveryList: "+ fermatEvent.getQueryID());
                    if (fermatEvent.getStatus() == NetworkClientActorListReceivedEvent.STATUS.SUCCESS)
                        abstractNetworkService2.handleNetworkClientActorListReceivedEvent(fermatEvent.getQueryID(), fermatEvent.getActorList());
                    else
                        System.out.println("ERROR IN THE QUERY WITH ID: "+ fermatEvent.getQueryID());
                }
            }
        });
        eventManager.addListener(actorListReceivedListener);
        listenersAdded.add(actorListReceivedListener);

        /*
         * 11. Listen and handle Network Client Sent Message Failed Event
         */
        FermatEventListener sentMessageFailedListener = eventManager.getNewListener(P2pEventType.NETWORK_CLIENT_SENT_MESSAGE_FAILED);
        sentMessageFailedListener.setEventHandler(new FermatEventHandler<NetworkClientNewMessageFailedEvent>() {
            @Override
            public void handleEvent(NetworkClientNewMessageFailedEvent fermatEvent) throws FermatException {
                System.out.println("P2P Layer: FAILED MESSAGE EVENT");
                AbstractNetworkService2 abstractNetworkService2 = networkServices.get(fermatEvent.getNetworkServiceTypeSource());
                if (abstractNetworkService2.isStarted()) {
                    //todo: ver esto: tengo que ver si voy a buscarlo a la db de la layer o si lo mando directo al ns con el id del mensaje que falló
//                    abstractNetworkService2.onNetworkServiceFailedMessage(fermatEvent.getId());

//                    if(networkService.getNetworkServiceConnectionManager().getOutgoingMessagesDao().exists(fermatEvent.getId()))
//                        networkService.onNetworkServiceFailedMessage(networkService.getNetworkServiceConnectionManager().getOutgoingMessagesDao().findById(fermatEvent.getId()));

                }

            }
        });
        eventManager.addListener(sentMessageFailedListener);
        listenersAdded.add(sentMessageFailedListener);

        /**
         * ACK listener
         */
        FermatEventListener ackEventListener = eventManager.getNewListener(P2pEventType.NETWORK_CLIENT_ACK);
        ackEventListener.setEventHandler(new FermatEventHandler<NetworkClientACKEvent>() {
            @Override
            public void handleEvent(NetworkClientACKEvent fermatEvent) throws FermatException {
                System.out.println("##### ACK MENSAJE LLEGÓ BIEN A LA LAYER!!!##### ID:"+fermatEvent.getContent().getPackageId());
            }
        });
        eventManager.addListener(ackEventListener);
        listenersAdded.add(ackEventListener);


    }




    private void distributeMessage(NetworkServiceType networkType,NetworkClientNewMessageTransmitEvent fermatEvent){
        if(networkServices.containsKey(networkType)){
            networkServices.get(networkType).onMessageReceived(fermatEvent.getContent());
        }
    }

    @Override
    public synchronized void register(AbstractNetworkService2 abstractNetworkService) {
        if (client.isConnected()) {
            try {
                messageSender.registerNetworkServiceProfile(abstractNetworkService.getProfile());
            } catch (FermatException e) {
                e.printStackTrace();
            }
        }
        networkServices.putIfAbsent(abstractNetworkService.getNetworkServiceType(), abstractNetworkService);
    }

    @Override
    public void register(NetworkChannel NetworkChannel) {
        if(client!=null) throw new IllegalArgumentException("Client already registered");
        client = NetworkChannel;
        client.connect();
    }

    @Override
    public void register(ActorProfile profile, NetworkServiceType type) {

        try {
            messageSender.registerActorProfile(profile, type);
        } catch (FermatException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void update(ActorProfile profile, UpdateTypes type) {

        if (client.isConnected()) {
            try {
                client.updateProfile(profile, type);
            } catch (FermatException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handle the event ClientConnectionCloseNotificationEvent
     * @param event
     */
    public void handleClientConnectionCloseNotificationEvent(ClientConnectionCloseNotificationEvent event) {

        try {
            System.out.println("***handleClientConnectionCloseNotificationEvent");

//            communicationSupervisorPendingMessagesAgent.removeAllConnectionWaitingForResponse();


        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setNetworkServicesRegisteredFalse() {
        for (final AbstractNetworkService2 abstractNetworkService : networkServices.values()) {
            try {
                abstractNetworkService.handleNetworkClientConnectionClosedEvent(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public UUID sendMessage(NetworkServiceMessage packageContent, NetworkServiceType networkServiceType,String nodeDestinationPublicKey) throws CantSendMessageException {
        System.out.println("***P2PLayer Method sendMessage..");
        //todo: me faltan cosas
        return messageSender.sendMessage(packageContent,networkServiceType,nodeDestinationPublicKey);
    }

    @Override
    public UUID sendDiscoveryMessage(ActorListMsgRequest packageContent, NetworkServiceType networkServiceType,String nodeDestinationPublicKey) throws CantSendMessageException {
        System.out.println("***P2PLayer Method sendMessage..");
        //todo: me faltan cosas
        return messageSender.sendDiscoveryMessage(packageContent,networkServiceType,nodeDestinationPublicKey);
    }

    /**
     * Handle the event CompleteComponentConnectionRequestNotificationEvent
     * @param event
     */
    public void handleCompleteComponentConnectionRequestNotificationEvent(CompleteComponentConnectionRequestNotificationEvent event) {

        try {

            System.out.println("***handleCompleteComponentConnectionRequestNotificationEvent");
            /*
             * Tell the manager to handler the new connection established
             */
//            communicationNetworkServiceConnectionManager.handleEstablishedRequestedNetworkServiceConnection(event.getRemoteComponent());
//            communicationSupervisorPendingMessagesAgent.removeConnectionWaitingForResponse(event.getRemoteComponent().getIdentityPublicKey());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Handle the event FailureComponentConnectionRequestNotificationEvent
     * @param event
     */
    public void handleFailureComponentConnectionRequest(FailureComponentConnectionRequestNotificationEvent event) {

        try {

            System.out.println("Executing handleFailureComponentConnectionRequest ");
//            communicationSupervisorPendingMessagesAgent.removeConnectionWaitingForResponse(event.getRemoteParticipant().getIdentityPublicKey());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Handle the event VPNConnectionCloseNotificationEvent
     * @param event
     */
    public void handleVpnConnectionCloseNotificationEvent(VPNConnectionCloseNotificationEvent event) {

        try {

            System.out.println("***handleVpnConnectionCloseNotificationEvent");
                String remotePublicKey = event.getRemoteParticipant().getIdentityPublicKey();

//                communicationSupervisorPendingMessagesAgent.removeConnectionWaitingForResponse(remotePublicKey);



        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Handle the event VPNConnectionLooseNotificationEvent
     * @param event
     */
    public void handleVPNConnectionLooseNotificationEvent(VPNConnectionLooseNotificationEvent event) {

        try {

            System.out.println("***handleVPNConnectionLooseNotificationEvent");

                String remotePublicKey = event.getRemoteParticipant().getIdentityPublicKey();


//                communicationSupervisorPendingMessagesAgent.removeConnectionWaitingForResponse(remotePublicKey);




        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Collection<AbstractNetworkService2> getNetworkServices() {
        return networkServices.values();
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public NetworkChannel getNetworkClient() {
        return client;
    }
}
