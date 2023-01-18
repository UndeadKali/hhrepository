package com.example.mysql.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mysql.data.User
import com.example.mysql.retrofit.GitHubService
import com.example.mysql.retrofit.RetrofitObject

class BlankFragment2ViewModel : ViewModel() {

    private lateinit var _user: MutableLiveData<User>
    lateinit var user: MutableLiveData<User>

    suspend fun getInformationAboutUser(username: String) {
        val retrofit = RetrofitObject.getInstance().create(GitHubService::class.java)
        val userInstance = retrofit.getUser(username).body()
        _user = MutableLiveData(
            User(
                userInstance?.url,
                userInstance?.id,
                userInstance?.avatar_url,
                userInstance?.name,
                userInstance?.created_at,
            )
        )
        user = _user
    }
}