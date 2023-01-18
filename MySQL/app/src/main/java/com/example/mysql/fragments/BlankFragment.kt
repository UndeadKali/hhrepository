package com.example.mysql.fragments

import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mysql.R
import com.example.mysql.RecyclerViewAdapter
import com.example.mysql.UserPreferencesRepository
import com.example.mysql.databinding.FragmentBlankBinding
import com.example.mysql.viewmodels.BlankFragmentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BlankFragment : Fragment() {
    private val viewModel: BlankFragmentViewModel by viewModels()
    private lateinit var binding: FragmentBlankBinding
    private lateinit var adapter: RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>
    private lateinit var dataStore: UserPreferencesRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentBlankBinding.inflate(inflater)

        binding.let {
            it.addButton.setOnClickListener { _ ->
                it.addButton.isEnabled = false
                CoroutineScope(Dispatchers.IO).launch {
                    dataStore.setValue(dataStore.getValue()?.plus(1) ?: 1)
                    getProducts()
                    delay(2600)
                    lifecycleScope.launch { it.addButton.isEnabled = true }
                }
            }

            it.removeButton.setOnClickListener { _ ->
                it.removeButton.isEnabled = false
                CoroutineScope(Dispatchers.IO).launch {
                    dataStore.setValue(dataStore.getValue()?.minus(1) ?: 1)
                    getProducts()
                    delay(2600)
                    lifecycleScope.launch { it.removeButton.isEnabled = true }
                }
            }
        }

        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(context, 1)
        binding.productsRecyclerView.layoutManager = layoutManager
        dataStore = UserPreferencesRepository(requireContext())

        getProducts()

        return binding.root
    }

    private fun getProducts() {
        CoroutineScope(Dispatchers.IO).launch {
            if (dataStore.getValue() == null) {
                dataStore.setValue(dataStore.getValue() ?: 1)
            }
            viewModel.getProducts(dataStore.getValue() ?: 1)
            lifecycleScope.launch {
                checkInt()
                val dividerItemDecoration = DividerItemDecoration(
                    binding.productsRecyclerView.context, DividerItemDecoration.VERTICAL
                )
                val insetDivider = InsetDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.divider
                    ), 20, 0, 20, 0
                )
                dividerItemDecoration.setDrawable(insetDivider)

                binding.productsRecyclerView.addItemDecoration(dividerItemDecoration)
                adapter =
                    RecyclerViewAdapter(viewModel.usersList.value?.toList() ?: mutableListOf())
                binding.productsRecyclerView.adapter = adapter
                binding.productsRecyclerView.isNestedScrollingEnabled = false
                binding.productsRecyclerView.setHasFixedSize(true)
            }

        }

    }

    private fun checkInt() {
        var ending: String
        binding.apply {
            viewModel.intValues.observe(viewLifecycleOwner) {
                viewModel.usersList.value?.size?.let { size -> viewModel.setValue(size) }
                ending =
                    if (viewModel.usersList.value?.size.toString()[viewModel.usersList.value?.size.toString().length - 1] == '1') "element"
                    else "elements"
                if (viewModel.usersList.value?.size != 0) {
                    when (it) {
                        viewModel.usersList.value?.size -> {
                            Toast.makeText(
                                context,
                                "Displayed ${viewModel.usersList.value?.size} $ending",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

}