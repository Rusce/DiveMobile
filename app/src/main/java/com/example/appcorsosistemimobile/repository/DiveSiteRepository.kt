package com.example.appcorsosistemimobile.repository

import com.example.appcorsosistemimobile.data.model.DiveSite
import com.example.appcorsosistemimobile.data.model.DiveSiteComment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import android.content.Context
import android.net.Uri
import com.google.firebase.storage.ktx.storage
import android.util.Log
import kotlin.math.round

object DiveSiteRepository {

    private val diveSiteCollection = Firebase.firestore.collection("dive_sites")

    suspend fun getAllDiveSites(): List<DiveSite> {
        val snapshot = diveSiteCollection.get().await()
        return snapshot.documents.mapNotNull {
            it.toObject(DiveSite::class.java)
        }
    }

    suspend fun getDiveSiteById(diveSiteId: String): DiveSite? {
        val snapshot = diveSiteCollection.document(diveSiteId).get().await()
        return snapshot.toObject(DiveSite::class.java)
    }

    suspend fun addDiveSiteWithImages(
        context: Context,
        diveSite: DiveSite,
        imageUris: List<Uri>
    ): Result<Unit> {
        return try {
            val storage = Firebase.storage
            val urls = mutableListOf<String>()

            for ((index, uri) in imageUris.withIndex()) {
                val ref = storage.reference
                    .child("divesites/${diveSite.id}/image_$index.jpg")

                val stream = context.contentResolver.openInputStream(uri)
                    ?: return Result.failure(Exception("Impossibile aprire immagine"))

                ref.putStream(stream).await()
                val downloadUrl = ref.downloadUrl.await().toString()
                urls.add(downloadUrl)
            }

            val diveSiteWithUrls = diveSite.copy(imageUrls = urls)

            diveSiteCollection
                .document(diveSite.id)
                .set(diveSiteWithUrls)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addCommentToDiveSite(diveSiteId: String, comment: DiveSiteComment): Result<Unit> {
        return try {
            diveSiteCollection
                .document(diveSiteId)
                .collection("comments")
                .document(comment.id)
                .set(comment)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCommentsForDiveSite(diveSiteId: String): List<DiveSiteComment> {
        Log.d("DiveSiteComments", "Fetching comments for diveSiteId: $diveSiteId")//debug
        return try {
            val snapshot = diveSiteCollection
                .document(diveSiteId)
                .collection("comments")
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(DiveSiteComment::class.java) }
        } catch (e: Exception) {
            emptyList() // oppure log dell'errore volendo TODO
        }
    }

    suspend fun toggleFavoriteDiveSite(userEmail: String, diveSiteId: String, isFavorite: Boolean): Result<Unit> {
        return try {
            val userRef = Firebase.firestore.collection("users").document(userEmail)
            val updateOp = if (isFavorite) {
                mapOf("favouriteDiveSite" to com.google.firebase.firestore.FieldValue.arrayRemove(diveSiteId))
            } else {
                mapOf("favouriteDiveSite" to com.google.firebase.firestore.FieldValue.arrayUnion(diveSiteId))
            }
            userRef.update(updateOp).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReveiwsAverageForDiveSite(diveSiteId: String): Double {
        Log.d("DiveSiteComments", "Fetching comments for diveSiteId: $diveSiteId")//debug
        val comments = getCommentsForDiveSite(diveSiteId)
        var sum = 0
        comments.forEach {sum += it.stars}
        return round((sum.toDouble() / if(comments.isEmpty()) 1 else comments.size) * 10.0) / 10.0
    }

    suspend fun getUserReviewForDiveSite(diveSiteId: String, authorName: String): DiveSiteComment? {
        getCommentsForDiveSite(diveSiteId).forEach { if(it.authorName == authorName) return it }
        return null
    }
}

