package com.example.myapplication.data.remote

import android.util.Log
import com.example.myapplication.model.City
import com.example.myapplication.model.Location
import com.example.myapplication.model.Place
import com.example.myapplication.model.PlaceType
import com.example.myapplication.model.ReviewStatus
import com.example.myapplication.model.Schedule
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.tasks.await

class FirestorePlacesRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    private val placesCollection get() = firestore.collection("places")

    val places: Flow<List<Place>> = callbackFlow {
        val listenerRegistration = placesCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirestorePlacesRepo", "Snapshot error", error)
                    trySend(emptyList()).isSuccess
                    return@addSnapshotListener
                }
                if (snapshot == null) {
                    trySend(emptyList()).isSuccess
                    return@addSnapshotListener
                }
                val placesList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(FirestorePlaceDto::class.java)
                        ?.copy(id = doc.id)
                        ?.toDomain()
                }
                trySend(placesList).isSuccess
            }
        awaitClose { listenerRegistration.remove() }
    }.catch { e ->
        Log.e("FirestorePlacesRepo", "places flow cancelled", e)
        emit(emptyList())
    }

    suspend fun createPlace(draft: CreatePlaceDraft): Result<Place> = runCatching {
        val document = placesCollection.document()
        val dto = FirestorePlaceDto.fromDraft(
            id = document.id,
            draft = draft,
            userId = draft.createdByUserId ?: auth.currentUser?.uid
        )
        document.set(dto).await()
        dto.toDomain() ?: error("No se pudo mapear el lugar creado")
    }

    suspend fun deletePlace(placeId: String): Result<Unit> = runCatching {
        placesCollection.document(placeId).delete().await()
    }

    suspend fun updateStatus(placeId: String, newStatus: ReviewStatus): Result<Unit> = runCatching {
        placesCollection.document(placeId).update("status", newStatus.name).await()
    }

    suspend fun findPlace(placeId: String): Result<Place?> = runCatching {
        val snapshot = placesCollection.document(placeId).get().await()
        snapshot.toObject(FirestorePlaceDto::class.java)
            ?.copy(id = snapshot.id)
            ?.toDomain()
    }

    data class CreatePlaceDraft(
        val name: String,
        val description: String,
        val category: PlaceType,
        val phones: String,
        val imageUrls: List<String>,
        val address: String,
        val city: City,
        val location: Location,
        val schedule: List<Schedule>,
        val createdByUserId: String? = null
    )
}

private data class FirestorePlaceDto(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val address: String = "",
    val city: String = City.ARMENIA.name,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val images: List<String> = emptyList(),
    val phoneNumber: String = "",
    val type: String = PlaceType.RESTAURANT.name,
    val schedules: List<FirestoreScheduleDto> = emptyList(),
    val createdByUserId: String? = null,
    val createdAt: Timestamp? = null,
    val status: String = ReviewStatus.PENDING.name,
    val distanceKm: Double? = null,
    val puntuation: Double? = null
) {

    fun toDomain(): Place? = runCatching {
        Place(
            id = id.ifBlank { UUID.randomUUID().toString() },
            title = title,
            description = description,
            address = address,
            city = city.safeCity(),
            location = Location(latitude, longitude),
            images = images,
            phoneNumber = phoneNumber,
            type = type.safePlaceType(),
            schedules = schedules.mapNotNull { it.toDomain() },
            createdByUserId = createdByUserId,
            createdAt = createdAt?.toLocalDateTime() ?: LocalDateTime.now(),
            status = status.safeStatus(),
            distanceKm = distanceKm,
            puntuation = puntuation
        )
    }.getOrNull()

    companion object {
        private val formatter = DateTimeFormatter.ofPattern("HH:mm")

        fun fromDraft(
            id: String,
            draft: FirestorePlacesRepository.CreatePlaceDraft,
            userId: String?
        ): FirestorePlaceDto = FirestorePlaceDto(
            id = id,
            title = draft.name,
            description = draft.description,
            address = draft.address,
            city = draft.city.name,
            latitude = draft.location.latitude,
            longitude = draft.location.longitude,
            images = draft.imageUrls,
            phoneNumber = draft.phones,
            type = draft.category.name,
            schedules = draft.schedule.map { FirestoreScheduleDto.fromDomain(it, formatter) },
            createdByUserId = userId,
            createdAt = Timestamp.now(),
            status = ReviewStatus.PENDING.name
        )
    }
}

private data class FirestoreScheduleDto(
    val day: String = DayOfWeek.MONDAY.name,
    val open: String = "08:00",
    val close: String = "18:00"
) {
    fun toDomain(): Schedule? = runCatching {
        Schedule(
            day = DayOfWeek.valueOf(day.uppercase(Locale.ROOT)),
            open = LocalTime.parse(open),
            close = LocalTime.parse(close)
        )
    }.getOrNull()

    companion object {
        fun fromDomain(schedule: Schedule, formatter: DateTimeFormatter): FirestoreScheduleDto =
            FirestoreScheduleDto(
                day = schedule.day.name,
                open = formatter.format(schedule.open),
                close = formatter.format(schedule.close)
            )
    }
}

private fun String?.safeStatus(): ReviewStatus =
    this?.let { runCatching { ReviewStatus.valueOf(it.uppercase(Locale.ROOT)) }.getOrNull() }
        ?: ReviewStatus.PENDING

private fun String?.safePlaceType(): PlaceType =
    this?.let { runCatching { PlaceType.valueOf(it.uppercase(Locale.ROOT)) }.getOrNull() }
        ?: PlaceType.RESTAURANT

private fun String?.safeCity(): City =
    this?.let { runCatching { City.valueOf(it.uppercase(Locale.ROOT)) }.getOrNull() }
        ?: City.ARMENIA

private fun Timestamp.toLocalDateTime(): LocalDateTime =
    this.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
