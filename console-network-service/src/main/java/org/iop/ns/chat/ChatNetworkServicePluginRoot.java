package org.iop.ns.chat;

import com.bitdubai.fermat_api.CantStartPluginException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.error_manager.enums.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginVersionReference;
import com.bitdubai.fermat_api.layer.all_definition.components.enums.PlatformComponentType;
import com.bitdubai.fermat_api.layer.all_definition.enums.Actors;
import com.bitdubai.fermat_api.layer.all_definition.enums.Developers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Layers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Platforms;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.events.EventSource;
import com.bitdubai.fermat_api.layer.all_definition.network_service.enums.NetworkServiceType;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.exceptions.CantConfirmTransactionException;
import com.bitdubai.fermat_api.layer.all_definition.util.Version;
import com.bitdubai.fermat_api.layer.core.PluginInfo;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.exceptions.CantSendMessageException;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.clients.interfaces.NetworkClientManager;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.data.DiscoveryQueryParameters;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.abstract_classes.AbstractActorNetworkService2;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.database.entities.NetworkServiceMessage;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.database.entities.NetworkServiceQuery;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.database.exceptions.RecordNotFoundException;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.profiles.ActorProfile;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.iop.ns.chat.enums.*;
import org.iop.ns.chat.structure.ChatMetadata;
import org.iop.ns.chat.structure.ChatMetadataRecord;
import org.iop.ns.chat.structure.ChatTransmissionJsonAttNames;
import org.iop.ns.chat.structure.EncodeMsjContent;
import org.iop.ns.chat.structure.test.MessageReceiver;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.UUID;

/**
 * Created by Gabriel Araujo 15/02/16.
 */
@PluginInfo(createdBy = "Gabo", developer = Developers.BITDUBAI, difficulty = PluginInfo.Dificulty.HIGH, layer = Layers.NETWORK_SERVICE, maintainerMail = "GaboMail",platform = Platforms.CHAT_PLATFORM, plugin = Plugins.CHAT_NETWORK_SERVICE)
public class ChatNetworkServicePluginRoot extends AbstractActorNetworkService2 {

    /**
     * Represent the intraActorDataBase
     */
//    private Database dataBaseCommunication;

    /**
     * Represent the communicationNetworkServiceDeveloperDatabaseFactory
     */

    private MessageReceiver messageReceiver;

    private List<ActorProfile> myActorProfiles;


    Timer timer = new Timer();

    private long reprocessTimer = 300000; //five minutes
    private List<ActorProfile> result;

    /**
     * Executor
     */
    //ExecutorService executorService;

    /**
     * Constructor with parameters
     */
    public ChatNetworkServicePluginRoot() {
        super(
                new PluginVersionReference(new Version()),
                EventSource.NETWORK_SERVICE_CHAT,
                NetworkServiceType.CHAT
        );

    }


    @Override
    protected void onActorNetworkServiceStart() {

        try {
        /*
         * Initialize the data base
         */
//            initializeDb();
        /*
         * Initialize cache data base
         */
            // initializeCacheDb();

        /*
         * Initialize Developer Database Factory
         */
//            chatNetworkServiceDeveloperDatabaseFactory = new ChatNetworkServiceDeveloperDatabaseFactory(pluginDatabaseSystem, pluginId, getErrorManager());
//            chatNetworkServiceDeveloperDatabaseFactory.initializeDatabase();
//
//            chatMetadataRecordDAO = new ChatMetadataRecordDAO(dataBaseCommunication);

            //declare a schedule to process waiting request message
//            this.startTimer();

            myActorProfiles = new ArrayList<>();

        } catch (Exception e) {
            reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        }


    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void onNewMessageReceived(NetworkServiceMessage newFermatMessageReceive) {
        try {
            System.out.println("----------------------------\n" + "CONVIERTIENDO MENSAJE ENTRANTE A GSON: " + newFermatMessageReceive.toJson() + "\n-------------------------------------------------");

            if (messageReceiver!=null){
                messageReceiver.onMessageReceived(newFermatMessageReceive.getContent());
            }

//            JsonObject messageData = EncodeMsjContent.decodeMsjContent(newFermatMessageReceive);
//            Gson gson = new Gson();
//            ChatMessageTransactionType chatMessageTransactionType = gson.fromJson(messageData.get(ChatTransmissionJsonAttNames.MSJ_CONTENT_TYPE), ChatMessageTransactionType.class);
//            System.out.println("chatMessageTransactionType = " + chatMessageTransactionType);
//            ChatMetadataRecord chatMetadataRecord;
//            switch (chatMessageTransactionType) {
//                case CHAT_METADATA_TRASMIT:
//                    String chatMetadataJson = messageData.get(ChatTransmissionJsonAttNames.CHAT_METADATA).getAsString();
//                    System.out.println("chatMetadataJson = " + chatMetadataJson);
//                    /*
//                     * Convert the xml to object
//                     */
//
//                    chatMetadataRecord = ChatMetadataRecord.fromJson(chatMetadataJson);
////                    messageData = EncodeMsjContent.decodeMsjContent(chatMetadataXml);
////                    chatMetadataRecord = new ChatMetadataRecord(messageData);
//                    System.out.println("----------------------------\n" + "MENSAJE LLEGO EXITOSAMENTE:" + chatMetadataRecord.getLocalActorPublicKey() + "\n-------------------------------------------------");
//
//                    String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm").format(new Timestamp(System.currentTimeMillis()));
//
//                    chatMetadataRecord.changeState(ChatProtocolState.PROCESSING_RECEIVE);
//                    chatMetadataRecord.setTransactionId(UUID.randomUUID());
//                    chatMetadataRecord.setResponseToNotification(messageData.get(ChatTransmissionJsonAttNames.RESPONSE_TO).getAsString());
//                    chatMetadataRecord.setChatMessageStatus(ChatMessageStatus.CREATED_CHAT);
//                    chatMetadataRecord.setMessageStatus(MessageStatus.CREATED);
//                    chatMetadataRecord.setDistributionStatus(DistributionStatus.DELIVERING);
//                    chatMetadataRecord.setProcessed(ChatMetadataRecord.NO_PROCESSED);
//                    chatMetadataRecord.setSentDate(timeStamp);
//                    chatMetadataRecord.setFlagReadead(false);
//                    System.out.println("----------------------------\n" + "CREANDO REGISTRO EN EL INCOMING NOTIFICATION DAO:" + "\n " + chatMetadataRecord.getMessage() + "\n-------------------------------------------------");
//
//                    chatMetadataRecord.setFlagReadead(false);
//
//                    launchIncomingChatNotification(chatMetadataRecord);
//
//                    break;
//                case TRANSACTION_STATUS_UPDATE:
//                    DistributionStatus distributionStatus = (messageData.has(ChatTransmissionJsonAttNames.DISTRIBUTION_STATUS)) ? gson.fromJson(messageData.get(ChatTransmissionJsonAttNames.DISTRIBUTION_STATUS).getAsString(), DistributionStatus.class) : null;
//                    MessageStatus messageStatus = (messageData.has(ChatTransmissionJsonAttNames.MESSAGE_STATUS)) ? gson.fromJson(messageData.get(ChatTransmissionJsonAttNames.MESSAGE_STATUS).getAsString(), MessageStatus.class) : null;
//                    ChatProtocolState chatProtocolState = (messageData.has(ChatTransmissionJsonAttNames.PROTOCOL_STATE)) ? gson.fromJson(messageData.get(ChatTransmissionJsonAttNames.PROTOCOL_STATE).getAsString(), ChatProtocolState.class) : null;
//                    UUID responseTo = (messageData.has(ChatTransmissionJsonAttNames.RESPONSE_TO)) ? gson.fromJson(messageData.get(ChatTransmissionJsonAttNames.RESPONSE_TO).getAsString(), UUID.class) : null;
//
//                    break;
//
//                case TRANSACTION_WRITING_STATUS:
////                    chatMetadataRecord = null;
////                    UUID responsTo = (messageData.has(ChatTransmissionJsonAttNames.RESPONSE_TO)) ? gson.fromJson(messageData.get(ChatTransmissionJsonAttNames.RESPONSE_TO).getAsString(), UUID.class) : null;
////                    if (responsTo != null)
////                        chatMetadataRecord = getChatMetadataRecordDAO().getNotificationByResponseTo(responsTo);
////                    if (chatMetadataRecord != null)
////                        launchIncomingWritingStatusNotification(chatMetadataRecord.getChatId());
//                default:
//                    break;
//
//            }

        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Wrong Json parsing for ChatNS");
            System.out.println("Message received successfully: "+newFermatMessageReceive);
            reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);

        }
        try {
            getNetworkServiceConnectionManager().getIncomingMessagesDao().markAsRead(newFermatMessageReceive);
        } catch (com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.database.exceptions.CantUpdateRecordDataBaseException | RecordNotFoundException e) {
            reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        }
    }

    private void launchIncomingChatNotification(ChatMetadataRecord chatMetadataRecord) {
//        messageReceiver.onMessageReceived(chatMetadataRecord);
    }

    @Override
    public void onSentMessage(NetworkServiceMessage messageSent) {

        try {
            JsonObject messageData = EncodeMsjContent.decodeMsjContent(messageSent);
            Gson gson = new Gson();
            UUID chatId = gson.fromJson(messageData.get(ChatTransmissionJsonAttNames.ID_CHAT), UUID.class);
            ChatMessageTransactionType chatMessageTransactionType = gson.fromJson(messageData.get(ChatTransmissionJsonAttNames.MSJ_CONTENT_TYPE), ChatMessageTransactionType.class);
            if (chatMessageTransactionType == ChatMessageTransactionType.CHAT_METADATA_TRASMIT) {
//                launchOutgoingChatNotification(chatId);
                System.out.println("ChatNetworkServicePluginRoot - SALIENDO DEL HANDLE NEW SENT MESSAGE NOTIFICATION");
            }

        } catch (Exception e) {
            //quiere decir que no estoy reciviendo metadata si no una respuesta
            System.out.println("ChatNetworkServicePluginRoot - EXCEPCION DENTRO DEL PROCCESS EVENT");
            reportUnexpectedError(e);
        }
    }

    public String getNetWorkServicePublicKey() {
        return getIdentity().getPublicKey();
    }

    private Actors getActorByPlatformComponentType(PlatformComponentType platformComponentType) {

        switch (platformComponentType) {
            case ACTOR_CHAT:
                return Actors.CHAT;
            default:
                return Actors.CHAT;
        }
    }

    private void sendMessage(final String jsonMessage,
                             final String identityPublicKey,
                             final Actors identityType,
                             final String actorPublicKey,
                             final Actors actorType) {

        try {
            ActorProfile sender = new ActorProfile();
            sender.setActorType(identityType.getCode());
            sender.setIdentityPublicKey(identityPublicKey);

            ActorProfile receiver = new ActorProfile();
            receiver.setActorType(actorType.getCode());
            receiver.setIdentityPublicKey(actorPublicKey);

            sendNewMessage(
                    sender,
                    receiver,
                    jsonMessage
            );
        } catch (com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.exceptions.CantSendMessageException e) {
            reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        }
    }



    public void sendChatMetadata(final String localActorPubKey, final String remoteActorPubKey, final ChatMetadata chatMetadata) throws Exception {
        ChatMetadataRecord chatMetadataRecord = new ChatMetadataRecord();

        try {

            if (chatMetadata == null) {
                throw new IllegalArgumentException("Argument chatMetadata can not be null");
            }
            if (localActorPubKey == null || localActorPubKey.length() == 0 || localActorPubKey.equals("null")) {
                throw new IllegalArgumentException("Argument localActorPubKey can not be null");
            }
            if (remoteActorPubKey == null || remoteActorPubKey.length() == 0 || remoteActorPubKey.equals("null")) {
                throw new IllegalArgumentException("Argument remoteActorPubKey can not be null");
            }
            System.out.println("ChatNetworkServicePluginRoot - Starting method sendChatMetadata");

            String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm").format(new Timestamp(System.currentTimeMillis()));

            ChatProtocolState protocolState = ChatProtocolState.PROCESSING_SEND;
            chatMetadataRecord.setTransactionId(UUID.randomUUID());
            chatMetadataRecord.setChatId(chatMetadata.getChatId());
            chatMetadataRecord.setObjectId(chatMetadata.getObjectId());
            chatMetadataRecord.setLocalActorType(chatMetadata.getLocalActorType());
            chatMetadataRecord.setLocalActorPublicKey(chatMetadata.getLocalActorPublicKey());
            chatMetadataRecord.setRemoteActorType(chatMetadata.getRemoteActorType());
            chatMetadataRecord.setRemoteActorPublicKey(chatMetadata.getRemoteActorPublicKey());
            chatMetadataRecord.setChatName(chatMetadata.getChatName());
            chatMetadataRecord.setChatMessageStatus(chatMetadata.getChatMessageStatus());
            chatMetadataRecord.setMessageStatus(chatMetadata.getMessageStatus());
            chatMetadataRecord.setDate(chatMetadata.getDate());
            chatMetadataRecord.setMessageId(chatMetadata.getMessageId());
            chatMetadataRecord.setMessage(chatMetadata.getMessage());
            chatMetadataRecord.setDistributionStatus(DistributionStatus.SENT);
            chatMetadataRecord.setResponseToNotification(chatMetadataRecord.getTransactionId().toString());
            chatMetadataRecord.setProcessed(ChatMetadataRecord.NO_PROCESSED);
            chatMetadataRecord.setSentDate(timeStamp);
            chatMetadataRecord.changeState(protocolState);
            chatMetadataRecord.setTypeChat(chatMetadata.getTypeChat());
            final String EncodedMsg = EncodeMsjContent.encodeMSjContentChatMetadataTransmit(chatMetadataRecord, chatMetadata.getLocalActorType(), chatMetadata.getRemoteActorType());


            chatMetadataRecord.setMsgXML(EncodedMsg);
            if (!chatMetadataRecord.isFilled(true)) {
                throw new Exception("Some value of ChatMetadata Is passed NULL");
            }

            // System.out.println("ChatPLuginRoot - Chat transaction: " + chatMetadataRecord);

            /*
             * Save into data base
             */
            final String sender = chatMetadataRecord.getLocalActorPublicKey();
            final PlatformComponentType senderType = chatMetadataRecord.getLocalActorType();
            final String remote = chatMetadataRecord.getRemoteActorPublicKey();
            final PlatformComponentType remoteType = chatMetadataRecord.getRemoteActorType();
//            getChatMetadataRecordDAO().createNotification(chatMetadataRecord);
            System.out.println("*** 12345 case 6:send msg in NS layer" + new Timestamp(System.currentTimeMillis()));
            if (chatMetadata.getTypeChat().equals(TypeChat.INDIVIDUAL)) {
                sendMessage(
                        EncodedMsg,
                        localActorPubKey,
                        getActorByPlatformComponentType(senderType),
                        remoteActorPubKey,
                        getActorByPlatformComponentType(remoteType)
                );
            } else if (chatMetadata.getTypeChat().equals(TypeChat.GROUP)) {

                throw new Exception("not supported");
            }

        } catch (Exception e) {
            StringBuilder contextBuffer = new StringBuilder();
            contextBuffer.append("Plugin ID: ").append(pluginId);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("pluginDatabaseSystem: ").append(pluginDatabaseSystem);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("errorManager: ").append(errorManager);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("eventManager: ").append(eventManager);

            String possibleCause = "Missing Fields.";


            Exception pluginStartException = new RuntimeException( contextBuffer.toString(),e);

            reportUnexpectedError(pluginStartException);

            throw pluginStartException;
        }
    }

    private void reportUnexpectedError(final Exception e) {
        reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
    }

    public void confirmReception(UUID transactionID) throws CantConfirmTransactionException {
        try {

//            ChatMetadataRecord chatMetadataRecord = getChatMetadataRecordDAO().getNotificationById(transactionID);
//            chatMetadataRecord.changeState(ChatProtocolState.DONE);
//            chatMetadataRecord.setDistributionStatus(DistributionStatus.DELIVERED);
//            chatMetadataRecord.setChatMessageStatus(ChatMessageStatus.CREATED_CHAT);
//            chatMetadataRecord.setMessageStatus(MessageStatus.DELIVERED);
//            chatMetadataRecord.setProcessed(ChatMetadataRecord.PROCESSED);
//            chatMetadataRecord.setFlagReadead(true);
//            getChatMetadataRecordDAO().update(chatMetadataRecord);

        } catch (Exception e) {
            StringBuilder contextBuffer = new StringBuilder();
            contextBuffer.append("Plugin ID: ").append(pluginId);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("pluginDatabaseSystem: ").append(pluginDatabaseSystem);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("errorManager: ").append(errorManager);
            contextBuffer.append(CantStartPluginException.CONTEXT_CONTENT_SEPARATOR);
            contextBuffer.append("eventManager: ").append(eventManager);
            CantConfirmTransactionException cantConfirmTransactionException = new CantConfirmTransactionException(CantConfirmTransactionException.DEFAULT_MESSAGE, e, contextBuffer.toString(), "Database error");
            reportUnexpectedError(cantConfirmTransactionException);
            throw cantConfirmTransactionException;
        }
    }


    @Override
    protected void onActorNetworkServiceRegistered() {

        System.out.println("method onNetworkServiceRegistered: chatNS");

    }

    public void setMessageReceiver(MessageReceiver messageReceiver) {
        this.messageReceiver = messageReceiver;
    }

    //I need this method for testing
    public void setNetworkClientManager(NetworkClientManager networkClientManager){
        this.networkClientManager = networkClientManager;
    }

    public void requestActorProfilesList() {
            try {
                discoveryActorProfiles(new DiscoveryQueryParameters(
                        null,
                        NetworkServiceType.ACTOR_CHAT,
                        Actors.CHAT.getCode(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        false,
                        null,
                        20,
                        0,
                        true
                ));
            } catch (CantSendMessageException e) {
                e.printStackTrace();
            }
    }

    UUID testID;

    @Override
    public void onNetworkServiceActorListReceived(NetworkServiceQuery query, List<ActorProfile> actorProfiles) {
//        actorProfiles.forEach(receiver -> {
//            if (receiver.getName().equals("Mati")){
//                ActorProfile sender = myActorProfiles.get(0);
//                try {
//                    testID = sendNewMessage(sender,receiver,"Holas");
//                } catch (com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.network_services.exceptions.CantSendMessageException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        System.out.println("Chat OnNetworkServiceActorListReceived...");
        if (messageReceiver!=null){
            messageReceiver.onActorListReceived(actorProfiles);
        }
    }

    public List<ActorProfile> getResult() {
        return result;
    }

}
