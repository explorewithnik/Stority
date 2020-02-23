package com.app.stority.homeSpace.owner.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.app.stority.R
import com.app.stority.binding.FragmentDataBindingComponent
import com.app.stority.databinding.FragmentSubCategoryBinding
import com.app.stority.di.Injectable
import com.app.stority.helper.AppExecutors
import com.app.stority.helper.Logger
import com.app.stority.helper.autoCleared
import com.app.stority.remoteUtils.Status
import com.app.stority.homeSpace.data.SubCategoryTable
import com.app.stority.homeSpace.observer.SubCategoryViewModel
import com.app.stority.homeSpace.owner.adapter.SubCategoryAdapter
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment.Companion.GRID_TYPE
import com.app.stority.homeSpace.owner.fragment.HomeSpaceFragment.Companion.LINEAR_TYPE
import com.app.stority.widget.AddSubCategoryDailog
import com.app.stority.widget.MultipleOptionDailogSubCategory
import com.google.gson.Gson
import javax.inject.Inject


class SubCategoryFragment : Fragment(), Injectable {
    var binding by autoCleared<FragmentSubCategoryBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private var adapter by autoCleared<SubCategoryAdapter>()
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var executors: AppExecutors
    private lateinit var viewModel: SubCategoryViewModel

    private var PRIVATE_MODE = 0
    private val PREF_NAME = "HomeSpacePref"
    private val SUB_LIST_TYPE = "subListType"
    private var sharedPref: SharedPreferences? = null

    private var entryId = ""
    private var text = ""

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        entryId = savedInstanceState?.getString(entryId)
            ?: SubCategoryFragmentArgs.fromBundle(arguments!!).entryId

        text = savedInstanceState?.getString(text)
            ?: SubCategoryFragmentArgs.fromBundle(arguments!!).text

        Logger.e(Thread.currentThread(), "text $text")

        requireActivity().title = text

        sharedPref = requireActivity().getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        changeSubListType(sharedPref?.getString(SUB_LIST_TYPE, GRID_TYPE))

        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(SubCategoryViewModel::class.java)


        viewModel.init(entryId)

        adapter = SubCategoryAdapter(
            context = requireContext(),
            dataBindingComponent = dataBindingComponent,
            appExecutors = executors

        ) { listData, action ->
            when (action) {

                ACTION_ROOT -> {
                    Logger.e(Thread.currentThread(), "item ${Gson().toJson(listData)}")
                }

                ACTION_DELETE -> {
                    viewModel.deleteSubCategoryListData(list = listData)
                }

                ACTION_FAB_SHOW -> {
                    binding.fab.show()
                }

                ACTION_FAB_HIDE -> {
                    binding.fab.hide()
                }

                ACTION_MORE -> {
                    binding.fab.hide()
                    onActionCallback(listData[0], ACTION_MORE_INT)
                }

            }
        }

        binding.let {
            it.lifecycleOwner = this
            it.recycler.adapter = adapter
        }
        initEntryList(viewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun initEntryList(viewModel: SubCategoryViewModel) {
        viewModel.result.observe(this, Observer { listResource ->
            binding.count = listResource?.data?.size
            binding.status = listResource?.status
            endProgress()
            when (listResource?.status) {
                Status.SUCCESS -> {
                    endProgress()
                    if (listResource.data != null) {
                        adapter.submitList(listResource.data)
                        adapter.allListData.clear()
                        adapter.allListData.addAll(listResource.data)
                    } else {
                        adapter.submitList(emptyList())
                    }
                }
                Status.ERROR -> {

                    endProgress()
                    if (listResource.code == 502) {
                    } else {
                        //Toast.makeText(context,"error",Toast.LENGTH_SHORT).show()
                    }
                }
                Status.LOADING -> {
                    startProgress()
                }
            }
        })
    }

    private fun startProgress() {
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun endProgress() {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_sub_category,
            container,
            false,
            dataBindingComponent
        )

        binding.fab.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.rotate
            )
        )

        binding.fab.setOnClickListener {
            binding.fab.hide()
            onActionCallback(
                SubCategoryTable(),
                ACTION_NEW
            )
        }

        return binding.root
    }

    fun navController() = findNavController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sub_category_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.subCategoryMenuSearch)?.isVisible = true

        menu.findItem(R.id.menuList)?.isVisible =
            sharedPref?.getString(SUB_LIST_TYPE, GRID_TYPE) == GRID_TYPE

        menu.findItem(R.id.menuGridList)?.isVisible =
            sharedPref?.getString(SUB_LIST_TYPE, GRID_TYPE) == LINEAR_TYPE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.subCategoryMenuSearch -> {
                Logger.e(Thread.currentThread(), "search")
            }

            R.id.menuGridList -> {
                changeSubListType(GRID_TYPE)
                sharedPref?.edit()?.putString(SUB_LIST_TYPE, GRID_TYPE)?.apply()
                requireActivity().invalidateOptionsMenu()
            }

            R.id.menuList -> {
                changeSubListType(LINEAR_TYPE)
                sharedPref?.edit()?.putString(SUB_LIST_TYPE, LINEAR_TYPE)?.apply()
                requireActivity().invalidateOptionsMenu()
            }
        }
        return true
    }

    companion object {
        const val ENTRY_ID = "entry_id"
        const val TEXT_ID = "text_id"
        const val ACTION_CANCEL = -1
        const val ACTION_EDIT = "edit"
        const val ACTION_NEW = 0
        const val ACTION_MORE = "more"
        const val ACTION_MORE_INT = 2
        const val ACTION_RENAME = 1

        const val ACTION_COPY = "copy"
        const val ACTION_DELETE = "delete"
        const val ACTION_SHARE = "share"
        const val ACTION_FAB_SHOW = "fabShow"
        const val ACTION_FAB_HIDE = "fabHide"
        const val ACTION_ROOT = "root"
        const val ACTION_ALL = "all"
    }

    private fun onActionCallback(data: SubCategoryTable?, action: Int) {
        when (action) {
            ACTION_NEW -> {
                AddSubCategoryDailog(
                    context = requireContext(),
                    data = data,
                    action = action,
                    dataBindingComponent = dataBindingComponent,
                    onSaveCallback = this::onSaveCallback,
                    onCancelCallback = this::onCancelCallback
                ).show()
            }
            ACTION_RENAME -> {
                AddSubCategoryDailog(
                    context = requireContext(),
                    data = data,
                    action = action,
                    dataBindingComponent = dataBindingComponent,
                    onSaveCallback = this::onSaveCallback,
                    onCancelCallback = this::onCancelCallback
                ).show()

            }

            ACTION_MORE_INT -> {
                MultipleOptionDailogSubCategory(
                    context = requireContext(),
                    data = data,
                    onMoreActionCalback = this::onMoreActionCallback,
                    onCancelCallback = this::onCancelCallback
                ).show()
            }

        }
    }

    private fun onSaveCallback(data: SubCategoryTable?, action: Int) {

        when (action) {
            ACTION_RENAME -> {
                viewModel.updateSubCategory(data)
                adapter.notifyDataSetChanged()
            }

            ACTION_NEW -> {
                if (!data?.text.isNullOrBlank()) {
                    adapter.allListData.clear()
                    binding.recycler.smoothScrollToPosition(binding.count!!)
                    viewModel.insertSubCategory(entryId, data)
                }
            }
        }
        binding.fab.show()
    }


    private fun onCancelCallback(action: Int) {
        when (action) {
            ACTION_CANCEL -> {
                binding.fab.show()
            }
        }

    }

    private fun onMoreActionCallback(data: SubCategoryTable?, action: String) {
        when (action) {
            ACTION_COPY -> {
                val clipboard =
                    requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
                val clip = ClipData.newPlainText("label", data?.text)
                clipboard?.setPrimaryClip(clip)

                Toast.makeText(requireContext(), "copied", Toast.LENGTH_SHORT).show()
                binding.fab.show()
            }
            ACTION_EDIT -> {
                onActionCallback(data, ACTION_RENAME)
            }

            ACTION_SHARE -> {
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Note")
                sharingIntent.putExtra(Intent.EXTRA_TEXT, data?.text)
                startActivity(Intent.createChooser(sharingIntent, "Share via"))
                binding.fab.show()
            }

        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ENTRY_ID, entryId)
        outState.putString(TEXT_ID, text)
    }

    private fun changeSubListType(subType: String?) {

        when (subType) {
            GRID_TYPE -> {
                binding.recycler.layoutManager =
                    StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            }

            LINEAR_TYPE -> {
                binding.recycler.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }

            else -> {
                binding.recycler.layoutManager =
                    StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            }
        }
    }
}
