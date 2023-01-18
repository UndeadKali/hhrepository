package com.example.mysql.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mysql.databinding.FragmentBlank3Binding

class BlankFragment3 : Fragment() {
    private lateinit var binding: FragmentBlank3Binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBlank3Binding.inflate(layoutInflater)
        return binding.root
    }

}