package org.iop.ns.chat.structure.test;

import org.iop.ns.chat.structure.ChatMetadataRecord;

/**
 * Created by mati on 14/08/16.
 */
public interface MessageReceiver {

    void onMessageReceived(ChatMetadataRecord chatMetadataRecord);

}
