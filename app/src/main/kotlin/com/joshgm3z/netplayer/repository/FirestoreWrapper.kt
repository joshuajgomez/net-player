package com.joshgm3z.netplayer.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.joshgm3z.netplayer.util.Logger
import javax.inject.Inject

class FirestoreWrapper
@Inject constructor(
    private val db: FirebaseFirestore
) {
    fun listenToDataMap(
        collection: String,
        documentId: String,
        onData: (Map<String, Any>) -> Unit
    ) {
        db.collection(collection)
            .document(documentId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val data = snapshot.data
                    if (data != null) {
                        Logger.debug("data = [${data}]")
                        onData(data)
                    }
                }
            }
    }
}