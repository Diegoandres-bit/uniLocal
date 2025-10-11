package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.City
import com.example.myapplication.model.Location
import com.example.myapplication.model.Place
import com.example.myapplication.model.PlaceType
import com.example.myapplication.model.ReviewStatus
import com.example.myapplication.model.Schedule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalTime

class PlacesViewModel: ViewModel() {

    private val _places = MutableStateFlow(emptyList<Place>())
    val places: StateFlow<List<Place>> = _places.asStateFlow()


    val pendingPlaces: StateFlow<List<Place>> =
        _places
            .map { list -> list.filter { it.status == ReviewStatus.PENDING } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val approvedPlaces: StateFlow<List<Place>> =
        _places
            .map { list -> list.filter { it.status == ReviewStatus.APPROVED } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        loadPlaces()
    }

    fun loadPlaces() {
        _places.value = listOf(
            Place(
                id = "1",
                title = "Restaurante El paisa",
                description = "El mejor restaurante paisa",
                address = "Cra 12 # 12 - 12",
                location = Location(1.23, 2.34),
                images = listOf(
                    "https://picsum.photos/600/400",
                    "https://picsum.photos/300/200",
                    "https://picsum.photos/301/200",
                    "https://picsum.photos/302/200",
                    "https://picsum.photos/303/200"
                ),
                phoneNumber = "3123123123",
                type = PlaceType.RESTAURANT,
                city = City.ARMENIA,
                schedules = listOf(
                    Schedule(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(20, 0)),
                    Schedule(DayOfWeek.THURSDAY, LocalTime.of(10, 0), LocalTime.of(20, 0)),
                    Schedule(DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(20, 0)),
                ),
                distanceKm = 15.5,
                puntuation = 4.7,
            ),
            Place(
                id = "2",
                title = "Bar test 1",
                description = "Un bar test",
                address = "Calle 12 # 12 - 12",
                location = Location(1.23, 2.34),
                images = listOf("https://cdn0.uncomo.com/es/posts/6/8/4/como_gestionar_un_bar_22486_orig.jpg"),
                phoneNumber = "3123123123",
                type = PlaceType.BAR,
                city = City.ARMENIA,
                schedules = listOf(),
                distanceKm = 10.5,
                puntuation = 2.8,
                createdByUserId = "2"
            ),
            Place(
                id = "3",
                title = "Hotel de prueba",
                description = "Un bar test",
                address = "Calle 12 # 12 - 12",
                location = Location(1.23, 2.34),
                images = listOf("https://cdn0.uncomo.com/es/posts/6/8/4/como_gestionar_un_bar_22486_orig.jpg"),
                phoneNumber = "3123123123",
                type = PlaceType.HOTEL,
                city = City.PEREIRA,
                schedules = listOf(),
                distanceKm = 35.0,
                puntuation = 2.0,
                createdByUserId = "2"
            ),
            Place(
                id = "4",
                title = "Shopping test 1",
                description = "Un bar test",
                address = "Calle 12 # 12 - 12",
                location = Location(1.23, 2.34),
                images = listOf("https://cdn0.uncomo.com/es/posts/6/8/4/como_gestionar_un_bar_22486_orig.jpg"),
                phoneNumber = "3123123123",
                type = PlaceType.SHOPPING,
                city = City.MEDELLIN,
                schedules = listOf(),
                distanceKm = 5.5,
                puntuation = 4.5,
                createdByUserId = "2"
            ),
            Place(
                id = "5",
                title = "Shopping test 2",
                description = "Un bar test",
                address = "Calle 12 # 12 - 12",
                location = Location(1.23, 2.34),
                images = listOf("https://cdn0.uncomo.com/es/posts/6/8/4/como_gestionar_un_bar_22486_orig.jpg"),
                phoneNumber = "3123123123",
                type = PlaceType.SHOPPING,
                city = City.BOGOTA,
                schedules = listOf(),
                distanceKm = 25.0,
                puntuation = 4.0,
                createdByUserId = "2"
            ),
            Place(
                id = "6",
                title = "Parque de prueba",
                description = "Un bar test",
                address = "Calle 12 # 12 - 12",
                location = Location(1.23, 2.34),
                images = listOf("https://cdn0.uncomo.com/es/posts/6/8/4/como_gestionar_un_bar_22486_orig.jpg"),
                phoneNumber = "3123123123",
                type = PlaceType.PARK,
                city = City.BOGOTA,
                schedules = listOf(),
                distanceKm = 45.1,
                puntuation = 3.8,
                createdByUserId = "2"
            )
        )
    }

    fun create(place: Place) {
        _places.value = _places.value + place
    }

    fun findById(id: String): Place? =
        _places.value.find { it.id == id }

    fun findByType(type: PlaceType): List<Place> =
        _places.value.filter { it.type == type }

    fun findByName(name: String): List<Place> =
        _places.value.filter { it.title.contains(other = name, ignoreCase = true) }
    fun findByKM(km: Double): List<Place> =
        _places.value.filter { it.distanceKm!! <= km}

    fun approvePlace(id: String) {
        _places.value = _places.value.map { p ->
            if (p.id == id) {
                Log.d("PlacesViewModel", "Lugar ${p.title} marcado como APROBADO")
                p.copy(status = ReviewStatus.APPROVED)
            } else p
        }
    }


    fun rejectPlace(id: String) {
        _places.value = _places.value.map { p ->
            if (p.id == id) p.copy(status = ReviewStatus.REJECTED) else p
        }
    }
}
