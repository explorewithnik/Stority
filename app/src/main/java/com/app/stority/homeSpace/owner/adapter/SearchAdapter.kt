package com.app.stority.homeSpace.owner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.app.stority.R
import com.app.stority.databinding.AdapterSearchBinding
import com.app.stority.helper.AppExecutors
import com.app.stority.helper.DataBoundListAdapter
import com.app.stority.homeSpace.data.HomeSpaceTable
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment.Companion.ACTION_ROOT


class SearchAdapter(
    private val context: Context,
    private val dataBindingComponent: DataBindingComponent,
    val appExecutors: AppExecutors,
    private val callback: ((List<HomeSpaceTable?>, action: String) -> Unit)?

) : DataBoundListAdapter<HomeSpaceTable, AdapterSearchBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<HomeSpaceTable>() {
        override fun areItemsTheSame(oldItem: HomeSpaceTable, newItem: HomeSpaceTable): Boolean {
            return oldItem.id == newItem.id && oldItem.text == newItem.text
        }

        override fun areContentsTheSame(oldItem: HomeSpaceTable, newItem: HomeSpaceTable): Boolean {
            return oldItem.id == newItem.id && oldItem.text == newItem.text
        }
    }
) {
    override fun createBinding(parent: ViewGroup): AdapterSearchBinding {

        return DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context),
                R.layout.adapter_search,
                parent,
                false,
                dataBindingComponent
            )
    }

    override fun bind(binding: AdapterSearchBinding, item: HomeSpaceTable, position: Int) {
        binding.data = item
        binding.root.setOnClickListener {
            binding.data?.let { data ->
                callback?.invoke(listOf(data), ACTION_ROOT)
            }
        }
    }
}