package com.app.stority.homeSpace.owner.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.app.stority.R
import com.app.stority.databinding.AdapterHomeSpaceBinding
import com.app.stority.helper.AppExecutors
import com.app.stority.helper.DataBoundListAdapter
import com.app.stority.homeSpace.data.HomeSpaceTable
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment.Companion.ACTION_DELETE
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment.Companion.ACTION_FAB_HIDE
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment.Companion.ACTION_FAB_SHOW
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment.Companion.ACTION_ROOT
import com.google.android.material.circularreveal.cardview.CircularRevealCardView


class HomeSpaceAdapter(
    private val context: Context,
    private val dataBindingComponent: DataBindingComponent,
    val appExecutors: AppExecutors,
    private val callback: ((List<HomeSpaceTable?>, action: String) -> Unit)?


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

    private var allListData = ArrayList<HomeSpaceTable?>()
    private var showAddAllMenuIcon = false
    private var showRemoveAllMenuIcon = false
    private var cardViewList = ArrayList<CircularRevealCardView?>()
    private var actionMode: ActionMode? = null
    private var multiSelect = false
    private val selectedItems = ArrayList<HomeSpaceTable?>()
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
        allListData.add(item)
        cardViewList.add(binding.cv)
        update(binding.data, binding)

    }

    inner class ActionModeCallback : ActionMode.Callback {

        override fun onActionItemClicked(
            mode: ActionMode?,
            item: MenuItem?
        ): Boolean {
            when (item?.itemId) {
                R.id.menuDelete -> {
                    callback?.invoke(selectedItems, ACTION_DELETE)
                    mode?.finish()
                    callback?.invoke(selectedItems, ACTION_FAB_SHOW)
                }

                R.id.menuAddAll -> {
                    mode?.finish()
                }

                R.id.menuRemoveAll -> {
                    showAddAllMenuIcon = true
                    mode?.invalidate()
                    if (!allListData.isNullOrEmpty()) {
                        selectedItems.clear()
                        allListData.zip(cardViewList) { data, cv ->
                            selectItem(data, cv, "all")
                        }
                        notifyDataSetChanged()
                    }
                }
            }

            return true
        }

        override fun onCreateActionMode(
            mode: ActionMode?,
            menu: Menu?
        ): Boolean {
            multiSelect = true
            val inflater = mode?.menuInflater
            inflater?.inflate(R.menu.multi_select_menu, menu)
            menu?.findItem(R.id.menuAddAll)?.isVisible = false
            return true
        }

        override fun onPrepareActionMode(
            mode: ActionMode?,
            menu: Menu?
        ): Boolean {
            if (showAddAllMenuIcon) {
                menu?.findItem(R.id.menuAddAll)?.isVisible = true
                menu?.findItem(R.id.menuRemoveAll)?.isVisible = false
            } else if (showRemoveAllMenuIcon) {
                menu?.findItem(R.id.menuAddAll)?.isVisible = false
                menu?.findItem(R.id.menuRemoveAll)?.isVisible = true
            }
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            multiSelect = false
            selectedItems.clear()
            callback?.invoke(listOf(HomeSpaceTable()), ACTION_FAB_SHOW)
            showAddAllMenuIcon = false
            allListData.clear()
            notifyDataSetChanged()
        }
    }

    fun update(data: HomeSpaceTable?, binding: AdapterHomeSpaceBinding?) {
        if (selectedItems.contains(data)) {
            binding?.cv?.strokeColor =
                ContextCompat.getColor(context, R.color.app_theme_color_accent)
            binding?.cv?.strokeWidth = 3
            binding?.cv?.setCardBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.app_theme_color
                )
            )
        } else {
            binding?.cv?.strokeColor = Color.TRANSPARENT
            binding?.cv?.strokeWidth = 0
            binding?.cv?.setCardBackgroundColor(ContextCompat.getColor(context,R.color.white))
        }

        binding?.root?.setOnClickListener {
            binding.data?.let { data ->
                selectItem(data, binding.cv, ACTION_ROOT)
            }
        }
        binding?.root?.setOnLongClickListener {
            val context = it.context as AppCompatActivity
            actionMode = context.startSupportActionMode(ActionModeCallback())
            binding.data?.let { data ->
                callback?.invoke(listOf(HomeSpaceTable()), ACTION_FAB_HIDE)
                selectItem(data, binding.cv, null)
            }
            return@setOnLongClickListener true
        }

    }

    fun selectItem(data: HomeSpaceTable?, cv: CircularRevealCardView?, action: String?) {
        if (multiSelect) {
            if (selectedItems.contains(data)) {
                selectedItems.remove(data)
                cv?.strokeColor = Color.TRANSPARENT
                cv?.strokeWidth = 0
                cv?.setCardBackgroundColor(ContextCompat.getColor(context,R.color.white))
                if (selectedItems.size == 0) {
                    callback?.invoke(listOf(data), ACTION_FAB_SHOW)
                    actionMode?.finish()
                }
            } else {
                selectedItems.add(data)
                cv?.strokeColor =
                    ContextCompat.getColor(context, R.color.app_theme_color_accent)
                cv?.strokeWidth = 3
                cv?.setCardBackgroundColor(ContextCompat.getColor(context, R.color.app_theme_color))
            }

            if (selectedItems.size > 0) {
                actionMode?.title = selectedItems.size.toString()
            } else {
                actionMode?.title = ""
            }

            if (allListData.size.toString() == actionMode?.title) {
                showAddAllMenuIcon = true
                showRemoveAllMenuIcon = false
                actionMode?.invalidate()
            } else {
                showRemoveAllMenuIcon = true
                showAddAllMenuIcon = false
                actionMode?.invalidate()
            }

        } else {
            when (action) {
                ACTION_ROOT -> {
                    callback?.invoke(listOf(data), ACTION_ROOT)
                }
                null -> {
                }
            }
        }
    }

}