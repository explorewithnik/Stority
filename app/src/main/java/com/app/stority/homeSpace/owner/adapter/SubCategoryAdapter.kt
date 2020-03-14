package com.app.stority.homeSpace.owner.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
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
import com.app.stority.databinding.AdapterSubCategoryBinding
import com.app.stority.helper.AppExecutors
import com.app.stority.helper.DataBoundListAdapter
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment.Companion.ACTION_ALL
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment.Companion.ACTION_DELETE
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment.Companion.ACTION_FAB_HIDE
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment.Companion.ACTION_FAB_SHOW
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment.Companion.ACTION_ROOT
import com.app.stority.homeSpace.data.SubCategoryTable
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment
import com.google.android.material.circularreveal.cardview.CircularRevealCardView


class SubCategoryAdapter(
    private val context: Context,
    private val dataBindingComponent: DataBindingComponent,
    val appExecutors: AppExecutors,
    private val callback: ((List<SubCategoryTable?>, action: String) -> Unit)?


) : DataBoundListAdapter<SubCategoryTable, AdapterSubCategoryBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<SubCategoryTable>() {
        override fun areItemsTheSame(
            oldItem: SubCategoryTable,
            newItem: SubCategoryTable
        ): Boolean {
            return oldItem.subCategoryId == newItem.subCategoryId && oldItem.text == newItem.text
        }

        override fun areContentsTheSame(
            oldItem: SubCategoryTable,
            newItem: SubCategoryTable
        ): Boolean {
            return oldItem.subCategoryId == newItem.subCategoryId && oldItem.text == newItem.text
        }
    }
) {

    var allListData = ArrayList<SubCategoryTable?>()
    private var showAddAllMenuIcon = false
    private var showRemoveAllMenuIcon = false
    var cardViewList = ArrayList<CircularRevealCardView?>()
    private var actionMode: ActionMode? = null
    private var multiSelect = false
    private val selectedItems = ArrayList<SubCategoryTable?>()
    override fun createBinding(parent: ViewGroup): AdapterSubCategoryBinding {

        return DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context),
                R.layout.adapter_sub_category,
                parent,
                false,
                dataBindingComponent
            )
    }

    override fun bind(binding: AdapterSubCategoryBinding, item: SubCategoryTable, position: Int) {
        binding.data = item

        if (cardViewList.size > 0) cardViewList.clear()
        repeat(allListData.size) {
            cardViewList.add(binding.cv)
        }

//        binding.more.setOnClickListener {
//            binding.data?.let { data ->
//                callback?.invoke(listOf(data), HomeSpaceFragment.ACTION_MORE)
//            }
//        }

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
                            selectItem(data, cv, ACTION_ALL)
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
            callback?.invoke(listOf(SubCategoryTable()), ACTION_FAB_SHOW)
            multiSelect = false
            actionMode = null
            selectedItems.clear()
            showAddAllMenuIcon = false
            showRemoveAllMenuIcon = false
            cardViewList.clear()
            notifyDataSetChanged()
        }
    }

    private fun update(data: SubCategoryTable?, binding: AdapterSubCategoryBinding?) {
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
            binding?.cv?.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
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
                callback?.invoke(listOf(SubCategoryTable()), ACTION_FAB_HIDE)
                selectItem(data, binding.cv, null)
            }
            return@setOnLongClickListener true
        }

    }

    fun selectItem(data: SubCategoryTable?, cv: CircularRevealCardView?, action: String?) {

        if (multiSelect) {
            if (selectedItems.contains(data)) {
                selectedItems.remove(data)
                cv?.strokeColor = Color.TRANSPARENT
                cv?.strokeWidth = 0
                cv?.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))


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