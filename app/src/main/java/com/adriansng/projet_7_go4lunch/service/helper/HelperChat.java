package com.adriansng.projet_7_go4lunch.service.helper;

import com.adriansng.projet_7_go4lunch.model.Message;
import com.adriansng.projet_7_go4lunch.model.Workmate;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

public class HelperChat {

    public static final String COLLECTION_NAME_MESSAGE = "messages";
    public static final String COLLECTION_NAME_CHAT = "chat";

    // ------------------
    // MESSAGE
    // ------------------

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getMessageCollection(String idWorkmateSender, String idWorkmateReceiver) {
        return HelperWorkmate.getWorkmateCollection()
                .document(idWorkmateSender)
                .collection(COLLECTION_NAME_CHAT)
                .document(idWorkmateReceiver)
                .collection(COLLECTION_NAME_MESSAGE);
    }

    // --- GET ---

    public static Query getAllMessageForChat(String idWorkmateSender, String idWorkmateReceiver) {
        return HelperWorkmate.getWorkmateCollection()
                .document(idWorkmateSender)
                .collection(COLLECTION_NAME_CHAT)
                .document(idWorkmateReceiver)
                .collection(COLLECTION_NAME_MESSAGE)
                .orderBy("dateCreated")
                .limit(50);
    }

    // --- CREATE ---

    public static Task<DocumentReference> createMessageForChat(String textMessage, Workmate idWorkmateSender, String idWorkmateReceiver) {
        Message message = new Message(textMessage, idWorkmateSender);
        getMessageCollection(idWorkmateReceiver, idWorkmateSender.getUid()).add(message);
        return getMessageCollection(idWorkmateSender.getUid(), idWorkmateReceiver).add(message);
    }

    public static Task<DocumentReference> createMessageWithImageForChat(String urlImage, String textMessage, Workmate idWorkmateSender, String idWorkmateReceiver) {
        Message message = new Message(textMessage, urlImage, idWorkmateSender);
        getMessageCollection(idWorkmateReceiver, idWorkmateSender.getUid()).add(message);
        return getMessageCollection(idWorkmateSender.getUid(), idWorkmateReceiver).add(message);
    }
}
