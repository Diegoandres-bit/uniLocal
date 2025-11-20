package com.example.myapplication.data

import android.net.Uri
import com.example.myapplication.model.City
import com.example.myapplication.model.Location
import com.example.myapplication.model.Place
import com.example.myapplication.model.PlaceType
import com.example.myapplication.model.ReviewStatus
import com.example.myapplication.model.Role
import com.example.myapplication.model.Schedule
import com.example.myapplication.model.User
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Única fuente de verdad "quemada" que simula toda la infraestructura.
 * Mantiene los lugares, usuarios y respuestas típicas de servicios en la nube
 * pero todo corre en memoria para que el flujo parezca real en la entrega final.
 */
object FakeCloudDataSource {

    private val initialPlaces = listOf(
        Place(
            id = "1",
            title = "Restaurante El paisa",
            description = "El mejor restaurante paisa",
            address = "Cra 12 # 12 - 12",
            city = City.ARMENIA,
            location = Location(4.535000, -75.676879),
            images = listOf(
                "https://picsum.photos/seed/paisa1/600/400",
                "https://picsum.photos/seed/paisa2/400/300"
            ),
            phoneNumber = "+57 322 555 1212",
            type = PlaceType.RESTAURANT,
            schedules = sampleSchedule(),
            status = ReviewStatus.APPROVED
        ),
        Place(
            id = "2",
            title = "Bar test 1",
            description = "Un bar test",
            address = "Calle 12 # 12 - 12",
            city = City.ARMENIA,
            location = Location(4.51400, -75.676568),
            images = listOf("https://picsum.photos/seed/bar1/600/400"),
            phoneNumber = "+57 311 987 2020",
            type = PlaceType.BAR,
            schedules = sampleSchedule(),
            status = ReviewStatus.PENDING,
            createdByUserId = "2"
        ),
        Place(
            id = "3",
            title = "Hotel de prueba",
            description = "Elige este hotel para escapadas rápidas",
            address = "Calle 52 # 10 - 44",
            city = City.PEREIRA,
            location = Location(4.8124, -75.6985),
            images = listOf("https://picsum.photos/seed/hotel1/600/400"),
            phoneNumber = "+57 606 744 3322",
            type = PlaceType.HOTEL,
            schedules = emptyList(),
            status = ReviewStatus.APPROVED,
            createdByUserId = "2"
        )
    )

    private val initialUsers = listOf(
        User(
            id = "1",
            name = "Admin",
            username = "admin",
            role = Role.ADMIN,
            city = City.ARMENIA,
            email = "admin@email.com",
            password = "12345678"
        ),
        User(
            id = "2",
            name = "Carlos",
            username = "carlosf",
            role = Role.USER,
            city = City.ARMENIA,
            email = "carlos@email.com",
            password = "12345678"
        )
    )

    private val _places = MutableStateFlow(initialPlaces)
    val places: StateFlow<List<Place>> = _places.asStateFlow()

    private val _users = MutableStateFlow(initialUsers)
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    suspend fun login(identifier: String, password: String): Result<User> {
        delay(600)
        val user = _users.value.firstOrNull {
            (it.email.equals(identifier, true) || it.username.equals(identifier, true)) &&
                it.password == password
        }
        return if (user != null) {
            _currentUser.value = user
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("Credenciales incorrectas"))
        }
    }

    suspend fun register(user: User): Result<User> {
        delay(500)
        if (_users.value.any { it.email.equals(user.email, true) || it.username == user.username }) {
            return Result.failure(IllegalStateException("Usuario ya existe"))
        }
        _users.update { it + user }
        return Result.success(user)
    }

    suspend fun requestPasswordReset(identifier: String): Result<String> {
        delay(400)
        val user = _users.value.firstOrNull {
            it.email.equals(identifier, true) || it.username.equals(identifier, true)
        } ?: return Result.failure(IllegalArgumentException("No encontramos esa cuenta"))

        val code = (100000..999999).random().toString()
        return Result.success(code)
    }

    suspend fun uploadPhoto(local: Uri): Result<String> {
        delay(800)
        val remoteUrl = "https://picsum.photos/seed/${local.hashCode()}/${(400..600).random()}/${(300..500).random()}"
        return Result.success(remoteUrl)
    }

    suspend fun createPlace(draft: CreatePlaceDraft): Result<Place> {
        delay(700)
        val place = Place(
            id = UUID.randomUUID().toString(),
            title = draft.name,
            description = draft.description,
            address = draft.address,
            city = draft.city,
            location = draft.location,
            images = draft.imageUrls,
            phoneNumber = draft.phones,
            type = draft.category,
            schedules = draft.schedule,
            createdByUserId = _currentUser.value?.id,
            createdAt = LocalDateTime.now(),
            status = ReviewStatus.PENDING
        )
        _places.update { it + place }
        return Result.success(place)
    }

    fun logout() {
        _currentUser.value = null
    }

    fun overrideCurrentUser(user: User?) {
        _currentUser.value = user
    }

    fun findPlace(id: String): Place? = _places.value.firstOrNull { it.id == id }

    fun deletePlace(id: String) {
        _places.update { list -> list.filterNot { it.id == id } }
    }

    fun seedReviewApproval(placeId: String, approve: Boolean) {
        _places.update { list ->
            list.map {
                if (it.id == placeId) it.copy(status = if (approve) ReviewStatus.APPROVED else ReviewStatus.REJECTED)
                else it
            }
        }
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
        val schedule: List<Schedule>
    )

    private fun sampleSchedule(): List<Schedule> = listOf(
        Schedule(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(18, 0)),
        Schedule(DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(18, 0)),
        Schedule(DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(23, 0))
    )
}

fun fakePlaceForMapbox(index: Int, day: DayOfWeek = DayOfWeek.MONDAY): Place = Place(
    id = "map-$index",
    title = "Lugar destacado $index",
    description = "Sugerido automáticamente para mostrar en el mapa.",
    address = "Cra $index # 00-00",
    city = City.ARMENIA,
    location = Location(4.52 + index * 0.004, -75.68 + index * 0.005),
    images = listOf("https://picsum.photos/seed/map$index/400/300"),
    phoneNumber = "+57 31$index 000 000",
    type = when (index % 3) {
        0 -> PlaceType.OTHER
        1 -> PlaceType.BAR
        else -> PlaceType.RESTAURANT
    },
    schedules = listOf(Schedule(day, LocalTime.of(8, 0), LocalTime.of(18, 0))),
    status = ReviewStatus.APPROVED
)
