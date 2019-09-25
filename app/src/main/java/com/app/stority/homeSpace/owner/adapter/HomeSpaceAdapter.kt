package com.app.stority.homeSpace.owner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.app.stority.R
import com.app.stority.databinding.AdapterHomeSpaceBinding
import com.app.stority.helper.AppExecutors
import com.app.stority.helper.DataBoundListAdapter
import com.app.stority.homeSpace.data.HomeSpaceTable



class HomeSpaceAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val callback: ((HomeSpaceTable, action: String) -> Unit)?
) : DataBoundListAdapter<HomeSpaceTable, AdapterHomeSpaceBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<HomeSpaceTable>() {
        override fun areItemsTheSame(oldItem: HomeSpaceTable, newItem: HomeSpaceTable): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HomeSpaceTable, newItem: HomeSpaceTable): Boolean {
            return oldItem.id == newItem.id
        }
    }
) {

    override fun createBinding(parent: ViewGroup): AdapterHomeSpaceBinding {
        val binding = DataBindingUtil
            .inflate<AdapterHomeSpaceBinding>(
                LayoutInflater.from(parent.context),
                R.layout.adapter_home_space,
                parent,
                false,
                dataBindingComponent
            )

        return binding
    }

    override fun bind(binding: AdapterHomeSpaceBinding, item: HomeSpaceTable, position: Int) {
        binding.data = item

        binding.root.setOnClickListener {
            binding.data?.let {
                callback?.invoke(it, "item")
            }
        }
    }
}