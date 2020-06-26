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
import com.app.stority.homeSpace.owner.fragment.SubCategoryFragment.Companion.ACTION_ALL
import com.app.stority.homeSpace.owner.fragment.SubCategoryFragment.Companion.ACTION_COPY
import com.app.stority.homeSpace.owner.fragment.SubCategoryFragment.Companion.ACTION_DELETE
import com.app.stority.homeSpace.owner.fragment.SubCategoryFragment.Companion.ACTION_EDIT
import com.app.stority.homeSpace.owner.fragment.SubCategoryFragment.Companion.ACTION_FAB_HIDE
import com.app.stority.homeSpace.owner.fragment.SubCategoryFragment.Companion.ACTION_FAB_SHOW
import com.app.stority.homeSpace.owner.fragment.SubCategoryFragment.Companion.ACTION_MARK_AS_DONE
import com.app.stority.homeSpace.owner.fragment.SubCategoryFragment.Companion.ACTION_MARK_AS_PENDING
import com.app.stority.homeSpace.owner.fragment.SubCategoryFragment.Companion.ACTION_MARK_AS_PROGRESS
import com.app.stority.homeSpace.owner.fragment.SubCategoryFragment.Companion.ACTION_ROOT
import com.app.stority.homeSpace.owner.fragment.SubCategoryFragment.Companion.ACTION_SHARE
import com.app.stority.widget.ConfirmationDailog
import com.app.stority.widget.ConfirmationDailog.Companion.SUB_CATEGORY_DATA
import com.app.stority.widget.ConfirmationDailog.Companion.SUB_CATEGORY_LIST_DATA
import com.app.tooltip.ClosePolicy
import com.app.tooltip.Tooltip
import com.app.tooltip.Typefaces
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
import com.google.gson.Gson


class SubCategoryAdapter(
    private var backGroundColor: String,
    private var isFirstRun: Boolean?,
    private val context: Context,
    private val dataBindingComponent: DataBindingComponent,
    val appExecutors: AppExecutors,
    private val callback: ((List<SubCategoryTable?>, action: String, color: String) -> Unit)?


) : DataBoundListAdapter<SubCategoryTable, AdapterSubCategoryBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<SubCategoryTable>() {
        override fun areItemsTheSame(
            oldItem: SubCategoryTable,
            newItem: SubCategoryTable
        ): Boolean {
            return oldItem.id == newItem.id && oldItem.text == newItem.text && oldItem.backGroundColor == newItem.backGroundColor
        }

        override fun areContentsTheSame(
            oldItem: SubCategoryTable,
            newItem: SubCategoryTable
        ): Boolean {
            return oldItem.id == newItem.id && oldItem.text == newItem.text && oldItem.backGroundColor == newItem.backGroundColor
        }
    }
) {
    var searchMenuClosed: Boolean = true
    var tooltip: Tooltip? = null
    var allListData = ArrayList<SubCategoryTable?>()
    private var showAddAllMenuIcon = false
    private var showMoreOption = true
    private var showRemoveAllMenuIcon = false
    var cardViewList = ArrayList<CircularRevealCardView?>()
    var actionMode: ActionMode? = null
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
        Logger.e(Thread.currentThread(), "color code background  ${item.backGroundColor}")

        if (backGroundColor != "-1" && backGroundColor != "default") {
            when (backGroundColor) {
                "green" -> {
                    Logger.e(Thread.currentThread(), "color code green is ${item.backGroundColor}")
                    binding.cv.strokeColor =
                        ContextCompat.getColor(context, android.R.color.transparent)
                    binding.cv.strokeWidth = 0
                    binding.cv.setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            android.R.color.holo_green_dark
                        )
                    )

                    binding.text.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )

                    binding.dateText.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )

                }

                "gray" -> {
                    Logger.e(Thread.currentThread(), "color code gray is ${item.backGroundColor}")
                    binding.cv.strokeColor =
                        ContextCompat.getColor(context, R.color.transparent)
                    binding.cv.strokeWidth = 0
                    binding.cv.setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.red
                        )
                    )
                    binding.text.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )

                    binding.dateText.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )
                }

                else -> {
                    Logger.e(
                        Thread.currentThread(),
                        "color code default is ${item.backGroundColor}"
                    )
                    binding.cv.strokeColor =
                        ContextCompat.getColor(context, android.R.color.transparent)
                    binding.cv.strokeWidth = 0
                    binding.cv.setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )

                    binding.text.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.black
                        )
                    )

                    binding.dateText.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.black
                        )
                    )
                }
            }
        } else {
            when (item.backGroundColor) {
                "green" -> {
                    Logger.e(Thread.currentThread(), "color code green is ${item.backGroundColor}")
                    binding.cv.strokeColor =
                        ContextCompat.getColor(context, android.R.color.transparent)
                    binding.cv.strokeWidth = 0
                    binding.cv.setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            android.R.color.holo_green_dark
                        )
                    )

                    binding.text.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )

                    binding.dateText.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )

                }

                "gray" -> {
                    Logger.e(Thread.currentThread(), "color code gray is ${item.backGroundColor}")
                    binding.cv.strokeColor =
                        ContextCompat.getColor(context, R.color.transparent)
                    binding.cv.strokeWidth = 0
                    binding.cv.setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.red
                        )
                    )
                    binding.text.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )

                    binding.dateText.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )
                }

                else -> {
                    Logger.e(
                        Thread.currentThread(),
                        "color code default is ${item.backGroundColor}"
                    )
                    binding.cv.strokeColor =
                        ContextCompat.getColor(context, android.R.color.transparent)
                    binding.cv.strokeWidth = 0
                    binding.cv.setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )

                    binding.text.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.black
                        )
                    )

                    binding.dateText.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.black
                        )
                    )
                }
            }
        }


        if (cardViewList.size > 0) cardViewList.clear()
        repeat(allListData.size) {
            cardViewList.add(binding.cv)
        }
        update(
            binding.data,
            binding,
            allListData
        )

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
                        subCategoryData = selectedItems,
                        action = ACTION_DELETE,
                        typeOfDataToDelete = SUB_CATEGORY_LIST_DATA,
                        dataBindingComponent = dataBindingComponent,
                        onDeleteCallback = this@SubCategoryAdapter::onDeleteCallback,
                        onHomeSpaceDeleteCallback = this@SubCategoryAdapter::onHomeSpaceDeleteCallback,
                        onCancelCallback = this@SubCategoryAdapter::onCancelCallback,
                        onSubCategoryListDeleteCallback = this@SubCategoryAdapter::onSubCategoryListDeleteCallback
                    ).show()
                }

                R.id.markAsPending -> {
                    callback?.invoke(selectedItems, ACTION_MARK_AS_PENDING, "gray")
                    mode?.finish()
                }


                R.id.markAsInProgress -> {
                    callback?.invoke(selectedItems, ACTION_MARK_AS_PROGRESS, "default")
                    mode?.finish()
                }


                R.id.markAsFinish -> {
                    callback?.invoke(selectedItems, ACTION_MARK_AS_DONE, "green")
                    mode?.finish()
                }

                R.id.copy -> {
                    callback?.invoke(selectedItems, ACTION_COPY, "default")
                    mode?.finish()
                }

                R.id.edit -> {
                    callback?.invoke(selectedItems, ACTION_EDIT, "default")
                    mode?.finish()
                }

                R.id.menuShare -> {
                    callback?.invoke(selectedItems, ACTION_SHARE, "default")
                    mode?.finish()
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
                            selectItem(data, cv, null, ACTION_ALL)
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
            Logger.e(
                Thread.currentThread(),
                "onCreateActionMode searchMenuClosed $searchMenuClosed"
            )
            if (!searchMenuClosed) menu?.findItem(R.id.menuRemoveAll)?.isVisible = false
            return true
        }

        override fun onPrepareActionMode(
            mode: ActionMode?,
            menu: Menu?
        ): Boolean {
            Logger.e(
                Thread.currentThread(),
                "onPrepareActionMode searchMenuClosed $searchMenuClosed"
            )

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
//                menu?.findItem(R.id.action_more)?.isVisible = true
            } else {
                menu?.findItem(R.id.copy)?.isVisible = false
                menu?.findItem(R.id.edit)?.isVisible = false
                menu?.findItem(R.id.menuShare)?.isVisible = false
//                menu?.findItem(R.id.action_more)?.isVisible = false
            }


            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            callback?.invoke(listOf(SubCategoryTable()), ACTION_FAB_SHOW, "default")
            multiSelect = false
            actionMode = null
            selectedItems.clear()
            showAddAllMenuIcon = false
            showRemoveAllMenuIcon = false
            cardViewList.clear()
            notifyDataSetChanged()
        }
    }


    private fun onDeleteCallback(data: SubCategoryTable?, action: String) {

    }

    private fun update(
        data: SubCategoryTable?,
        binding: AdapterSubCategoryBinding?,
        allListData: ArrayList<SubCategoryTable?>
    ) {

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

            binding?.text?.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.black
                )
            )

            binding?.dateText?.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.black
                )
            )

        }

        Logger.e(Thread.currentThread(), "isFirstRun $isFirstRun")

        if (cardViewList.isNotEmpty() && cardViewList.size == 1 && isFirstRun == true) {
            binding?.cv?.post {
                val metrics = context.resources.displayMetrics
                tooltip = Tooltip.Builder(context)
                    .anchor(binding.cv, 0, 0, true)
                    .text("tap on card to add points or long press to copy, share, edit or delete it")
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
                selectItem(data, binding.cv, binding, ACTION_ROOT)
            }
        }
        binding?.root?.setOnLongClickListener {
            val context = it.context as AppCompatActivity
            actionMode = context.startSupportActionMode(ActionModeCallback())
            binding.data?.let { data ->
                callback?.invoke(listOf(SubCategoryTable()), ACTION_FAB_HIDE, "default")
                selectItem(data, binding.cv, binding, null)
            }
            return@setOnLongClickListener true
        }
    }

    fun selectItem(
        data: SubCategoryTable?,
        cv: CircularRevealCardView?,
        binding: AdapterSubCategoryBinding?,
        action: String?
    ) {
        Logger.e(Thread.currentThread(), "all list data update 2 ${Gson().toJson(allListData)}")
        if (multiSelect) {
            if (selectedItems.contains(data)) {
                Logger.e(Thread.currentThread(), "selectedItems")
                selectedItems.remove(data)
                cv?.strokeColor = Color.TRANSPARENT
                cv?.strokeWidth = 0
                when (data?.backGroundColor) {
                    "green" -> {
                        cv?.setCardBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                android.R.color.holo_green_dark
                            )
                        )

                        binding?.text?.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )

                        binding?.dateText?.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )

                    }

                    "gray" -> {
                        cv?.setCardBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.red
                            )
                        )
                        binding?.text?.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )

                        binding?.dateText?.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )
                    }

                    else -> {
                        cv?.setCardBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )

                        binding?.text?.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )

                        binding?.dateText?.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                    }
                }



                if (selectedItems.size == 0) {
                    callback?.invoke(listOf(data), ACTION_FAB_SHOW, "default")
                    actionMode?.finish()
                }
            } else {
                Logger.e(Thread.currentThread(), "selectedItems not")
                selectedItems.add(data)
                cv?.strokeColor =
                    ContextCompat.getColor(context, R.color.app_theme_color_accent)
                cv?.strokeWidth = 3
                cv?.setCardBackgroundColor(ContextCompat.getColor(context, R.color.app_theme_color))

                Logger.e(Thread.currentThread(), "selectedItems not binding $binding")
                Logger.e(
                    Thread.currentThread(),
                    "selectedItems not binding text ${binding?.text?.text}"
                )
                binding?.text?.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.black
                    )
                )

                binding?.dateText?.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.black
                    )
                )
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
//            cv?.strokeColor = Color.TRANSPARENT
//            cv?.strokeWidth = 0
            when (action) {
                ACTION_ROOT -> {
                    callback?.invoke(listOf(data), ACTION_ROOT, "default")
                }
                null -> {
                }
            }
        }
    }

    private fun onHomeSpaceDeleteCallback(list: List<HomeSpaceTable?>, action: String) {
        Logger.e(Thread.currentThread(), "onHomeSpaceDeleteCallback")
    }

    private fun onCancelCallback(action: String) {
        callback?.invoke(listOf(SubCategoryTable()), ACTION_FAB_SHOW, "default")
        actionMode?.finish()
    }


    private fun onSubCategoryListDeleteCallback(list: List<SubCategoryTable?>, action: String) {
        Logger.e(Thread.currentThread(), "onSubCategoryListDeleteCallback")
        callback?.invoke(selectedItems, ACTION_DELETE, "default")
        actionMode?.finish()
        callback?.invoke(selectedItems, ACTION_FAB_SHOW, "default")
    }

}