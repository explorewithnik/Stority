package com.app.stority.homeSpace.owner.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.animation.Animation
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
import com.app.stority.helper.Logger
import com.app.stority.homeSpace.data.HomeSpaceTable
import com.app.stority.homeSpace.data.SubCategoryTable
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment.Companion.ACTION_ALL
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment.Companion.ACTION_DELETE
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment.Companion.ACTION_FAB_HIDE
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment.Companion.ACTION_FAB_SHOW
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment.Companion.ACTION_ROOT
import com.app.stority.homeSpace.owner.fragment.SubCategoryFragment.Companion.ACTION_COPY
import com.app.stority.homeSpace.owner.fragment.SubCategoryFragment.Companion.ACTION_EDIT
import com.app.stority.homeSpace.owner.fragment.SubCategoryFragment.Companion.ACTION_SHARE
import com.app.stority.widget.ConfirmationDailog
import com.app.stority.widget.ConfirmationDailog.Companion.SUB_CATEGORY_LIST_DATA
import com.app.tooltip.ClosePolicy
import com.app.tooltip.Tooltip
import com.app.tooltip.Typefaces
import com.google.android.material.circularreveal.cardview.CircularRevealCardView


class SubCategoryAdapter(
    private var isFirstRun: Boolean?,
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
    var searchMenuClosed: Boolean = true
    private var tooltip: Tooltip? = null
    var allListData = ArrayList<SubCategoryTable?>()
    private var showAddAllMenuIcon = false
    private var showRemoveAllMenuIcon = false
    var cardViewList = ArrayList<CircularRevealCardView?>()
    private var actionMode: ActionMode? = null
    private var multiSelect = false
    private var showMoreOption = true
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

        update(binding.data, binding)
    }

    inner class ActionModeCallback : ActionMode.Callback {

        override fun onActionItemClicked(
            mode: ActionMode?,
            item: MenuItem?
        ): Boolean {
            when (item?.itemId) {
                R.id.menuDelete -> {
                    ConfirmationDailog(
                        context = context,
                        data = null,
                        title = "Selected notes will be deleted",
                        homeSpaceData = listOf(),
                        action = ACTION_DELETE,
                        typeOfDataToDelete = SUB_CATEGORY_LIST_DATA,
                        dataBindingComponent = dataBindingComponent,
                        onDeleteCallback = this@SubCategoryAdapter::onDeleteCallback,
                        onHomeSpaceDeleteCallback = this@SubCategoryAdapter::onHomeSpaceDeleteCallback,
                        onSubCategoryListDeleteCallback = this@SubCategoryAdapter::onSubCategoryListDeleteCallback,
                        onCancelCallback = this@SubCategoryAdapter::onCancelCallback
                    ).show()
                }

                R.id.menuAddAll -> {
                    mode?.finish()
                }

                R.id.copy -> {
                    callback?.invoke(selectedItems, ACTION_COPY)
                    mode?.finish()
                }

                R.id.edit -> {
                    callback?.invoke(selectedItems, ACTION_EDIT)
                    mode?.finish()
                }

                R.id.menuShare -> {
                    callback?.invoke(selectedItems, ACTION_SHARE)
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
            if (!searchMenuClosed) menu?.findItem(R.id.menuRemoveAll)?.isVisible = false
            return true
        }

        override fun onPrepareActionMode(
            mode: ActionMode?,
            menu: Menu?
        ): Boolean {
//            menu?.findItem(R.id.edit)?.isVisible = false

            if (searchMenuClosed) {
                if (showAddAllMenuIcon) {
                    menu?.findItem(R.id.menuAddAll)?.isVisible = true
                    menu?.findItem(R.id.menuRemoveAll)?.isVisible = false
                } else if (showRemoveAllMenuIcon) {
                    menu?.findItem(R.id.menuAddAll)?.isVisible = false
                    menu?.findItem(R.id.menuRemoveAll)?.isVisible = true
                }
            } else {
                menu?.findItem(R.id.menuRemoveAll)?.isVisible = false
                menu?.findItem(R.id.menuAddAll)?.isVisible = false
            }

            if (showMoreOption) {
                menu?.findItem(R.id.copy)?.isVisible = true
                menu?.findItem(R.id.edit)?.isVisible = true
                menu?.findItem(R.id.menuShare)?.isVisible = true
                menu?.findItem(R.id.action_more)?.isVisible = true
            } else {
                menu?.findItem(R.id.copy)?.isVisible = false
                menu?.findItem(R.id.edit)?.isVisible = false
                menu?.findItem(R.id.menuShare)?.isVisible = false
                menu?.findItem(R.id.action_more)?.isVisible = false
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

        Logger.e(Thread.currentThread(), "isFirstRun $isFirstRun")

        if (cardViewList.isNotEmpty() && cardViewList.size == 1 && isFirstRun == true) {
            binding?.cv?.post {
                val metrics = context.resources.displayMetrics

                tooltip = Tooltip.Builder(context)
                    .anchor(binding.cv, 0, 0, true)
                    .text("tap or long press on card to share copy, edit or delete note")
                    .styleId(R.style.ToolTipAltStyle)
                    .typeface(Typefaces[context, "font/roboto.ttf"])
                    .maxWidth(metrics.widthPixels / 2)
                    .arrow(true)
                    .floatingAnimation(Tooltip.Animation.DEFAULT)
                    .closePolicy(ClosePolicy.TOUCH_ANYWHERE_NO_CONSUME)
                    .showDuration(Animation.INFINITE.toLong())
                    .overlay(true)
                    .create()

                tooltip?.doOnHidden {
                        tooltip = null
                    }
                    ?.doOnFailure {

                    }
                    ?.doOnShown {

                    }

                    ?.show(binding.cv, Tooltip.Gravity.RIGHT, true)
            }
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

            Logger.e(Thread.currentThread(), "title ${actionMode?.title}")

            if (actionMode?.title != "1") {
                showMoreOption = false
                actionMode?.invalidate()
            } else {
                showMoreOption = true
                actionMode?.invalidate()
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

    private fun onCancelCallback(action: String) {
        callback?.invoke(listOf(SubCategoryTable()), ACTION_FAB_SHOW)
        actionMode?.finish()
    }

    private fun onSubCategoryListDeleteCallback(list: List<SubCategoryTable?>, action: String) {
        callback?.invoke(selectedItems, ACTION_DELETE)
        actionMode?.finish()
        callback?.invoke(selectedItems, ACTION_FAB_SHOW)
    }

    private fun onDeleteCallback(data: SubCategoryTable?, action: String) {

    }

    private fun onHomeSpaceDeleteCallback(list: List<HomeSpaceTable?>, action: String) {

    }
}