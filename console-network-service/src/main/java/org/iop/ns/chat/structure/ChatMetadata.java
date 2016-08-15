package org.iop.ns.chat.structure;

import com.bitdubai.fermat_api.layer.all_definition.components.enums.PlatformComponentType;
import org.iop.ns.chat.enums.ChatMessageStatus;
import org.iop.ns.chat.enums.DistributionStatus;
import org.iop.ns.chat.enums.MessageStatus;
import org.iop.ns.chat.enums.TypeChat;


import java.util.List;
import java.util.UUID;

/**
 * Created by Gabriel Araujo on 05/01/16.
 */
public interface ChatMetadata {

    UUID getChatId();

    UUID getObjectId();

    PlatformComponentType getLocalActorType();

    String getLocalActorPublicKey();

    PlatformComponentType getRemoteActorType();

    String getRemoteActorPublicKey();

    String getChatName();

    ChatMessageStatus getChatMessageStatus();

    MessageStatus getMessageStatus();

    String getDate();

    UUID getMessageId();

    String getMessage();

    DistributionStatus getDistributionStatus();

    TypeChat getTypeChat();

//    List<GroupMember> getGroupMembers();

    String toJson();

}
