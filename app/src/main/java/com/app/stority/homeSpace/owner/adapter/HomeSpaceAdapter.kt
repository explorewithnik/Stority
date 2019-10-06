package com.app.stority.homeSpace.owner.adapter

import android.graphics.Color
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
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
    private val callback: ((List<HomeSpaceTable>, action: String) -> Unit)?

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

    private var actionMode: ActionMode? = null
    private var multiSelect = false
    private val selectedItems = ArrayList<HomeSpaceTable>()
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

        update(binding.data, binding)
    }

    inner class ActionModeCallback : ActionMode.Callback {


        override fun onActionItemClicked(
            mode: ActionMode?,
            item: MenuItem?
        ): Boolean {
            callback?.invoke(selectedItems, "delete")
            mode?.finish()
            return true
        }

        override fun onCreateActionMode(
            mode: ActionMode?,
            menu: Menu?
        ): Boolean {
            multiSelect = true
            val inflater = mode?.menuInflater
            inflater?.inflate(R.menu.multi_select_menu, menu)
            return true
        }

        override fun onPrepareActionMode(
            mode: ActionMode?,
            menu: Menu?
        ): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            multiSelect = false
            selectedItems.clear()
            notifyDataSetChanged()
        }
    }

    fun update(data: HomeSpaceTable?, binding: AdapterHomeSpaceBinding) {
        if (selectedItems.contains(data)) {

            binding.cv.strokeColor = Color.BLACK
            binding.cv.strokeWidth = 2
        } else {
            binding.cv.strokeColor = Color.TRANSPARENT
            binding.cv.strokeWidth = 0
        }

        binding.root.setOnClickListener {
            binding.data?.let { data ->
                selectItem(data, binding, "item")
            }
        }

        binding.root.setOnLongClickListener {
            val context = it.context as AppCompatActivity
            actionMode = context.startSupportActionMode(ActionModeCallback())
            binding.data?.let { data ->
                selectItem(data, binding, null)
            }
            return@setOnLongClickListener true
        }

    }

    fun selectItem(
        data: HomeSpaceTable, binding: AdapterHomeSpaceBinding, action: String?
    ) {
        if (multiSelect) {
            if (selectedItems.contains(data)) {
                selectedItems.remove(data)
                binding.cv.strokeColor = Color.TRANSPARENT
                binding.cv.strokeWidth = 0
                if (selectedItems.size == 0) actionMode?.finish()

            } else {
                selectedItems.add(data)
                binding.cv.strokeColor = Color.BLACK
                binding.cv.strokeWidth = 2
            }

            if (selectedItems.size > 0) {
                actionMode?.title = selectedItems.size.toString()
            } else {
                actionMode?.title = ""
            }


        } else {
            when (action) {
                "item" -> {
                    callback?.invoke(listOf(data), "item")
                }
                null -> {
                }
            }
        }
    }

}