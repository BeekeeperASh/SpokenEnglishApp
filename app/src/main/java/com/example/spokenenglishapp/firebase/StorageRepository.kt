package com.example.spokenenglishapp.firebase

import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.time.Duration

const val USERS_COLLECTION_REF = "users"

class StorageRepository {

    val user = Firebase.auth.currentUser
    fun hasUser(): Boolean = Firebase.auth.currentUser != null

    fun getUserId(): String = Firebase.auth.currentUser?.uid.orEmpty()

    private val usersRef: CollectionReference = Firebase.firestore.collection(USERS_COLLECTION_REF)

    fun getUserData(
        userId: String
    ): Flow<Resources<List<UserData>>> = callbackFlow {
        var snapshotStateListener: ListenerRegistration? = null
        try {
            //delay(800L)
            snapshotStateListener = usersRef.orderBy("timestamp").whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val users = snapshot.toObjects(UserData::class.java)
                        Resources.Success(data = users)
                    } else {
                        Resources.Error(throwable = e?.cause)
                    }
                    trySend(response)
                }
        } catch (e: Exception) {
            trySend(Resources.Error(e?.cause))
            e.printStackTrace()
        }
        awaitClose {
            snapshotStateListener?.remove()
        }
        delay(800L)
    }

    fun getData(
        dataId: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (UserData?) -> Unit
    ) {
        usersRef.document(dataId).get()
            .addOnSuccessListener {
                onSuccess.invoke(it.toObject(UserData::class.java))
            }
            .addOnFailureListener { res ->
                onError.invoke(res.cause)

            }
    }

    fun addUser(
        userId: String,
        username: String?,
        profilePictureUrl: String?,
        timestamp: Timestamp = Timestamp.now(),
        //testVal: Int,
        onComplete: (Boolean) -> Unit
    ) {

        val documentId = usersRef.document().id
        val user = UserData(userId, username, profilePictureUrl, timestamp, documentId)
        usersRef.document(documentId).set(user)
            .addOnCompleteListener { res ->
                onComplete.invoke(res.isSuccessful)
            }
    }

    fun deleteUser(
        userId: String,
        onComplete: (Boolean) -> Unit
    ) {
        usersRef.document(userId)
            .delete()
            .addOnCompleteListener {
                onComplete.invoke(it.isSuccessful)
            }
    }

    fun updateData(
        userId: String,
        element: String,
        onResult: (Boolean) -> Unit
    ) {
        val updateData = hashMapOf<String, Any>(
            "test" to element
        )
        usersRef.document(userId)
            .update(updateData)
            .addOnCompleteListener {
                onResult(it.isSuccessful)
            }
    }

//    fun addElementToMap(documentId: String, element: Any, onComplete: (Boolean) -> Unit) {
//        val timestamp = Timestamp.now()
//        val updateData = hashMapOf<String, Any>(
//            "accuracyMap" to FieldValue.arrayUnion(Pair(timestamp,element))
//        )
//
//        usersRef.document(documentId)
//            .update(updateData)
//            .addOnCompleteListener { task ->
//                onComplete(task.isSuccessful)
//            }
//    }

    fun addElementToMap(documentId: String, value: Any, onComplete: (Boolean) -> Unit) {
        val key = Timestamp.now().toDate().toString()

        usersRef.document(documentId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val existingMap = documentSnapshot.get("accuracyMap") as? Map<String, Any> ?: emptyMap()
                val updatedMap = existingMap.toMutableMap()

                updatedMap[key] = value

                val updateData = hashMapOf<String, Any>(
                    "accuracyMap" to updatedMap
                )

                usersRef.document(documentId)
                    .update("accuracyMap", updatedMap)
                    .addOnCompleteListener { task ->
                        onComplete(task.isSuccessful)
                    }
            }
            .addOnFailureListener { exception ->
                onComplete(false)
                // Обработка ошибки чтения документа
            }

    }


}

sealed class Resources<T>(
    val data: T? = null,
    val throwable: Throwable? = null
) {
    class Loading<T> : Resources<T>()
    class Success<T>(data: T?) : Resources<T>(data = data)
    class Error<T>(throwable: Throwable?) : Resources<T>(throwable = throwable)


}