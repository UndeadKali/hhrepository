package com.example.mysql.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import coil.load
import coil.request.CachePolicy
import com.example.mysql.R
import com.example.mysql.databinding.FragmentBlank2Binding
import com.example.mysql.viewmodels.BlankFragment2ViewModel
import kotlinx.coroutines.launch

class BlankFragment2 : Fragment() {
    private val viewModel: BlankFragment2ViewModel by viewModels()
    private lateinit var binding: FragmentBlank2Binding
    private val args: BlankFragment2Args by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentBlank2Binding.inflate(layoutInflater)

        val infoAboutClickedUser =
            "${args.userArg.id}    ${args.userArg.login}     ${args.userArg.avatar_url}"
        Toast.makeText(context, infoAboutClickedUser, Toast.LENGTH_SHORT).show()

        binding.userGetInfoButton.setOnClickListener {
            viewModel.let {
                it.viewModelScope.launch {
                    it.getInformationAboutUser(binding.userEditText.text.toString().trim())
                    observeUser()
                }
            }
        }

        return binding.root
    }

    private fun observeUser() {
        viewModel.user.observe(viewLifecycleOwner) {
            binding.let {
                val userId = "id:   ${viewModel.user.value?.id.toString()}"
                val userUrl = "url: ${viewModel.user.value?.url}"
                val userName = "name:   ${viewModel.user.value?.name}"
                val userCreatedAt = "created_at:    ${viewModel.user.value?.created_at}"
                binding.userAvatarUrl.load(viewModel.user.value?.avatar_url) {
                    memoryCachePolicy(CachePolicy.DISABLED)
                    placeholder(R.drawable.ic_launcher_background)
                    crossfade(2000)
                }

                it.userId.text = userId
                it.userUrl.text = userUrl
                it.userName.text = userName
                it.userCreatedAt.text = userCreatedAt
            }
        }
    }

}