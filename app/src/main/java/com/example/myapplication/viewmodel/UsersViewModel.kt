package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myapplication.model.City
import com.example.myapplication.model.Role
import com.example.myapplication.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UsersViewModel: ViewModel(){

    private val _users = MutableStateFlow(emptyList<User>())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers(){

        _users.value = listOf(
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
                password = "123456"
            )
        )

    }

    fun create(user: User){
        _users.value = _users.value + user
    }

    fun findById(id: String): User?{
        return _users.value.find { it.id == id }
    }

    fun findByEmail(email: String): User?{
        return _users.value.find { it.email == email }
    }

    fun login(identifier: String, password: String): User? {
        return _users.value.find {
            (it.email == identifier || it.username == identifier) && it.password == password
        }
    }

    fun update(user: User) {
        _users.value = _users.value.map { if (it.id == user.id) user else it }
    }



}