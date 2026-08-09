package com.joshgm3z.netplayer.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.joshgm3z.netplayer.util.Logger
import kotlinx.coroutines.tasks.await
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

    suspend fun newDocumentId(
        collection: String,
        dataMap: Map<String, Any>
    ): String {
        val document = db.collection(collection)
            .add(dataMap)
            .await()
        return document.id
    }

    suspend fun deleteDocumentWithId(collection: String, documentId: String): Boolean {
        return try {
            db.collection(collection)
                .document(documentId)
                .delete()
                .await()
            Logger.debug("Deleted $collection/$documentId")
            true
        } catch (e: Exception) {
            Logger.error("Failed to delete $collection/$documentId: ${e.message}")
            false
        }
    }

    suspend fun activateSession(collection: String, sessionId: String) {
        Logger.debug("collection = [${collection}], sessionId = [${sessionId}]")
        db.collection(collection)
            .document(sessionId)
            .set(mapOf("activated" to System.currentTimeMillis()))
            .await()
    }
}