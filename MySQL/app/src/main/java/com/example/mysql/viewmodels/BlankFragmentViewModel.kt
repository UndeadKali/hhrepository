package com.example.mysql.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mysql.UserPreferencesRepository
import com.example.mysql.data.Users
import com.example.mysql.retrofit.GitHubService
import com.example.mysql.retrofit.RetrofitObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class BlankFragmentViewModel(application: Application) : AndroidViewModel(application) {

    private val since: Int = Random.nextInt(Short.MAX_VALUE.toInt())
    private val _usersList: MutableLiveData<MutableList<Users>> =
        MutableLiveData(mutableListOf())
    val usersList: MutableLiveData<MutableList<Users>> = _usersList
    private val dataStore = UserPreferencesRepository(application)
    val intValues = dataStore.getValues().asLiveData(Dispatchers.IO)

    fun setValue(value: Int) {
        viewModelScope.launch {
            dataStore.setValue(value)
        }
    }

    suspend fun getProducts(value: Int) {
        _usersList.value?.clear()
        val retrofit = RetrofitObject.getInstance().create(GitHubService::class.java)
        val listOfUsers = retrofit.getUsersList(since, value)

        for (iterable in listOfUsers.body().orEmpty().withIndex()) {
            val login = iterable.value.login
            val id = iterable.value.id
            val avatar_url = iterable.value.avatar_url
            val user = Users(login, id, avatar_url)
            usersList.value?.add(user)
        }
    }

}