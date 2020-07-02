package com.app.stority.homeSpace.owner.fragment

import android.content.*
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.app.stority.R
import com.app.stority.binding.FragmentDataBindingComponent
import com.app.stority.databinding.FragmentHomeSpaceBinding
import com.app.stority.di.Injectable
import com.app.stority.helper.AppExecutors
import com.app.stority.helper.Logger
import com.app.stority.helper.autoCleared
import com.app.stority.helper.isNullOrBlankOrEmpty
import com.app.stority.homeSpace.data.HomeSpaceTable
import com.app.stority.homeSpace.observer.HomeSpaceViewModel
import com.app.stority.homeSpace.owner.activity.HomeSpaceActivity
import com.app.stority.homeSpace.owner.adapter.HomeSpaceAdapter
import com.app.stority.remoteUtils.Status
import com.app.stority.widget.AddDataDailog
import com.app.stority.widget.CommonMethods
import com.app.stority.widget.MultipleOptionDailog
import com.app.tooltip.ClosePolicy
import com.app.tooltip.Tooltip
import com.app.tooltip.Typefaces
import com.google.gson.Gson
import javax.inject.Inject

class HomeSpaceFragment : Fragment(), Injectable {
    var tooltip: Tooltip? = null
    var binding by autoCleared<FragmentHomeSpaceBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private var adapter by autoCleared<HomeSpaceAdapter>()

    private var searchMenuClosed = true

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var searchView: SearchView? = null
    private var shouldHideSearchList: Boolean = true

    @Inject
    lateinit var executors: AppExecutors
    private val viewModel: HomeSpaceViewModel by viewModels { viewModelFactory }
    private var isFirstRun: Boolean = false
    private var sharedPref: SharedPreferences? = null
    private var searchList: MutableList<HomeSpaceTable?> = ArrayList()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        CommonMethods.setUpToolbar(
            requireActivity(),
            "My Notes",
            "HomeSpaceFragment"
        )
        shouldHideSearchList = true
        Logger.e(Thread.currentThread(), "virus onActivityCreated")
        changeListType(sharedPref?.getString(LIST_TYPE, GRID_TYPE))

        if (!searchList.isNullOrEmpty() && searchList.size > 0) {
            viewModel.apiCall.value = "5"
            searchMenuClosed = true
            Logger.e(Thread.currentThread(), "searchList ${searchList.size}")
        } else {
            Logger.e(Thread.currentThread(), "elseee")
        }

        val stopAnim = viewModel.init(value = "1")
        if (stopAnim) isFirstRun =
            false //stopping tooltip anim when coming back from sub category frag

        adapter = HomeSpaceAdapter(
            isFirstRun = isFirstRun,
            context = requireContext(),
            dataBindingComponent = dataBindingComponent,
            appExecutors = executors
        ) { listData, action, color ->
            when (action) {

                ACTION_ROOT -> {
                    Logger.e(Thread.currentThread(), "id ${listData[0]?.id}")
                    Logger.e(Thread.currentThread(), "text ${listData[0]?.text}")

                    val fragAction = HomeSpaceFragmentDirections.SubCategoryFragment()

                    if (!searchList.isNullOrEmpty() && searchList.size > 0) {
                        Logger.e(Thread.currentThread(), "searchList id ${searchList[0]?.id}")
                        Logger.e(Thread.currentThread(), "searchList text ${searchList[0]?.text}")
                        fragAction.fromSearch = true
//                        searchList.clear()
                    } else {
                        fragAction.fromSearch = false
                    }

                    fragAction.entryId = listData[0]?.id.toString()
                    fragAction.text = listData[0]?.text.toString()
                    fragAction.backGroundColor = listData[0]?.backGroundColor.toString()
                    navController().navigate(fragAction)
                }

                ACTION_DELETE -> {
                    viewModel.deleteHomeSpaceListData(list = listData)
                    Logger.e(Thread.currentThread(), "searchList size ${searchList.size}")
                    Logger.e(Thread.currentThread(), "search menu closed $searchMenuClosed")
                    if (!searchList.isNullOrEmpty() && searchList.size > 0) {
                        searchList.removeAll(listData)
                        searchMenuClosed = true
                    }

                    requireActivity().invalidateOptionsMenu()
                }


                ACTION_FAB_SHOW -> {
                    binding.fab.show()
                }

                ACTION_FAB_HIDE -> {
                    binding.fab.hide()
                }

                ACTION_COPY -> {
                    val clipboard =
                        requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
                    val clip = ClipData.newPlainText("label", listData[0]?.text)
                    clipboard?.setPrimaryClip(clip)
                    Logger.e(Thread.currentThread(), "clip $clip")

                    Toast.makeText(requireContext(), "copied", Toast.LENGTH_SHORT).show()
                }

                ACTION_EDIT -> {
                    AddDataDailog(
                        context = requireContext(),
                        data = listData[0],
                        action = ACTION_RENAME,
                        dataBindingComponent = dataBindingComponent,
                        onSaveCallback = this::onSaveCallback,
                        onCancelCallback = this::onCancelCallback
                    ).show()

                }

                ACTION_MARK_AS_DONE -> {
                    viewModel.updateColor(list = listData, color = color)
                }

                ACTION_MARK_AS_PROGRESS -> {
                    viewModel.updateColor(list = listData, color = color)
                }

                ACTION_MARK_AS_PENDING -> {
                    viewModel.updateColor(list = listData, color = color)
                }

                ACTION_SHARE -> {
                    Logger.e(Thread.currentThread(), "ACTION_share ${Gson().toJson(listData[0])}")
                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    sharingIntent.type = "text/plain"
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Note")
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, listData[0]?.text)
                    startActivity(Intent.createChooser(sharingIntent, "Share via"))
//                    viewModel.subApiCall.value = listData[0]?.id.toString()
                    Logger.e(Thread.currentThread(), "ACTION_share id ${listData[0]?.id}")
                }

                else -> {
                    Logger.e(Thread.currentThread(), "else called")
                }
            }
        }

        binding.let {
            it.lifecycleOwner = this
            it.recycler.adapter = adapter
            it.isFirstRun = isFirstRun
        }

        initEntryList(viewModel)
    }

    private fun initEntryList(viewModel: HomeSpaceViewModel) {
        viewModel.result.observe(viewLifecycleOwner, Observer { listResource ->
            binding.count = listResource?.data?.size
            binding.status = listResource?.status
            endProgress()
            when (listResource?.status) {
                Status.SUCCESS -> {
                    endProgress()
                    if (listResource.data != null) {
                        Logger.e(
                            Thread.currentThread(),
                            "data list ${Gson().toJson(listResource.data)}"
                        )
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
            requireActivity().invalidateOptionsMenu()
        })

        viewModel.subResult.observe(viewLifecycleOwner, Observer { subList ->
            endProgress()
            when (subList?.status) {
                Status.SUCCESS -> {
                    endProgress()
                    if (subList.data != null) {
                        Logger.e(
                            Thread.currentThread(),
                            "sub data list ${Gson().toJson(subList.data)}"
                        )

                    } else {
                        Logger.e(
                            Thread.currentThread(),
                            "sub data empty list ${Gson().toJson(subList.data)}"
                        )
                    }
                }
                Status.ERROR -> {

                    endProgress()
                    if (subList.code == 502) {
                    } else {
                        //Toast.makeText(context,"error",Toast.LENGTH_SHORT).show()
                    }
                }
                Status.LOADING -> {
                    startProgress()
                }
            }
            requireActivity().invalidateOptionsMenu()
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
            R.layout.fragment_home_space,
            container,
            false,
            dataBindingComponent
        )

        binding.fab.setOnClickListener {
            binding.fab.hide()
            onActionCallback(HomeSpaceTable(), ACTION_NEW)
        }
        return binding.root
    }

    private fun navController() = findNavController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        sharedPref = (activity as HomeSpaceActivity).sharedPref
        isFirstRun = (activity as HomeSpaceActivity).isFirstRun ?: false
        Logger.e(Thread.currentThread(), "firstTimeLaunch HomeSpaceFragment $isFirstRun")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_space_menu, menu)

        // Define the listener
        val expandListener = object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                shouldHideSearchList = true
                searchMenuClosed = true
                adapter.searchMenuClosed = true
                Logger.e(Thread.currentThread(), "onMenuItemActionCollapse")
                if (!searchList.isNullOrEmpty()) searchList = ArrayList()
                viewModel.apiCall.value = "1"
//                binding.count = adapter.allListData.size
//                binding.status = Status.SUCCESS
                if (sharedPref?.getString(LIST_TYPE, GRID_TYPE) == GRID_TYPE) {
                    Handler().postDelayed({
                        if (isVisible) menu.findItem(R.id.menuList).isVisible = true
                    }, 1)
                } else if (sharedPref?.getString(LIST_TYPE, GRID_TYPE) == LINEAR_TYPE) {
                    Handler().postDelayed({
                        if (isVisible) menu.findItem(R.id.menuGridList).isVisible = true
                    }, 1)
                }
                Handler().postDelayed({
                    if (isVisible) binding.fab.show()
                }, 200)

                return true // Return true to collapse action view
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                // Do something when expanded
                searchMenuClosed = false
                adapter.searchMenuClosed = false

                Logger.e(Thread.currentThread(), "onMenuItemActionExpand ${searchList.size}")
                Logger.e(
                    Thread.currentThread(),
                    "onMenuItemActionExpand list  ${Gson().toJson(searchList)}"
                )

                adapter.submitList(emptyList())
                binding.count = 0
                binding.status = Status.SUCCESS

//                if (!searchList.isNullOrEmpty() && searchList.size > 0){
////                    searchList.clear()
//                    adapter.submitList(emptyList())
//                    binding.count = 0
//                    binding.status = Status.SUCCESS
//                }

//                if (searchList.isNullOrEmpty()) {
//                    adapter.submitList(emptyList())
//                    binding.count = 0
//                    binding.status = Status.SUCCESS
//                } else {
//                    adapter.submitList(searchList)
//                    binding.count = searchList.size
//                    binding.status = Status.SUCCESS
//                }
                if (sharedPref?.getString(LIST_TYPE, GRID_TYPE) == GRID_TYPE) {
                    menu.findItem(R.id.menuList).isVisible = false
                } else if (sharedPref?.getString(LIST_TYPE, GRID_TYPE) == LINEAR_TYPE) {
                    menu.findItem(R.id.menuGridList).isVisible = false
                }
                Handler().postDelayed({
                    if (isVisible) binding.fab.hide()
                }, 200)

                return true // Return true to expand action view
            }
        }

        // Get the MenuItem for the action item
        val actionMenuItem = menu.findItem(R.id.menuSearch)

        searchView = actionMenuItem.actionView as SearchView
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                closeKeyboard()
                Logger.e(Thread.currentThread(), "onQueryTextSubmit $query")
//                query?.let { queryData ->
//                    searchList = adapter.allListData.filter {
//                        it?.text?.startsWith(queryData, true) ?: false
//                    }.toMutableList()
//
//                    Logger.e(Thread.currentThread(), "list ${Gson().toJson(searchList)}")
//
//                    if (searchList.isNullOrEmpty()) {
//                        Logger.e(Thread.currentThread(), "if called")
////                        adapter.submitList(emptyList())
//                        binding.count = 0
//                        binding.status = Status.SUCCESS
//                    } else {
//                        Logger.e(Thread.currentThread(), "else called")
//                        adapter.submitList(searchList)
//                        binding.count = searchList.size
//                        binding.status = Status.SUCCESS
//                    }
//                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Logger.e(Thread.currentThread(), "onQueryTextChange $newText")
                Logger.e(
                    Thread.currentThread(),
                    "onQueryTextChange searchMenuClosed $searchMenuClosed"
                )
                if (searchMenuClosed) return true
                if (newText.isNullOrBlankOrEmpty()) {
                    if (shouldHideSearchList) {
                        adapter.submitList(emptyList())
                        binding.count = 0
                        binding.status = Status.SUCCESS
                    } else {
                        if (!searchList.isNullOrEmpty() && searchList.size > 0) {
                            adapter.submitList(searchList)
                            binding.count = searchList.size
                            binding.status = Status.SUCCESS
                        } else {
                            adapter.submitList(emptyList())
                            binding.count = 0
                            binding.status = Status.SUCCESS
                        }
                    }
                } else {
                    newText?.let { queryData ->
                        searchList = adapter.allListData.filter {
                            it?.text?.contains(queryData, true) ?: false
                        }.toMutableList()

                        Logger.e(Thread.currentThread(), "list ${Gson().toJson(searchList)}")

                        if (searchList.isNullOrEmpty()) {
                            binding.count = 0
                            binding.status = Status.SUCCESS
                        } else {
                            adapter.submitList(searchList)
                            binding.count = searchList.size
                            binding.status = Status.SUCCESS
                        }
                    }

                }
                return true
            }
        })
        // Assign the listener to that action item
        actionMenuItem?.setOnActionExpandListener(expandListener)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.menuSearch)?.isVisible = true
        Logger.e(Thread.currentThread(), "itemCount  ${adapter.allListData.size}")
        menu.findItem(R.id.menuList)?.isVisible =
            sharedPref?.getString(
                LIST_TYPE,
                GRID_TYPE
            ) == GRID_TYPE && adapter.allListData.size > 0 && searchList.isNullOrEmpty() && searchMenuClosed

        menu.findItem(R.id.menuGridList)?.isVisible =
            sharedPref?.getString(
                LIST_TYPE,
                GRID_TYPE
            ) == LINEAR_TYPE && adapter.allListData.size > 0 && searchList.isNullOrEmpty() && searchMenuClosed

        menu.findItem(R.id.menuSearch)?.isVisible = adapter.allListData.size > 0
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when (item.itemId) {


            R.id.menuGridList -> {
                changeListType(GRID_TYPE)
                sharedPref?.edit()?.putString(LIST_TYPE, GRID_TYPE)?.apply()
                requireActivity().invalidateOptionsMenu()
            }

            R.id.menuList -> {
                changeListType(LINEAR_TYPE)
                sharedPref?.edit()?.putString(LIST_TYPE, LINEAR_TYPE)?.apply()
                requireActivity().invalidateOptionsMenu()
            }
        }
        return true
    }

    companion object {
        const val LIST_TYPE = "listType"
        const val GRID_TYPE = "grid"
        const val LINEAR_TYPE = "linear"
        const val ACTION_CANCEL = -1
        const val ACTION_EDIT = "edit"
        const val ACTION_RENAME = 1
        const val ACTION_NEW = 0
        const val ACTION_MORE = "more"
        const val ACTION_MORE_INT = 2
        const val ACTION_COPY = "copy"
        const val ACTION_MARK_AS_DONE = "done"
        const val ACTION_MARK_AS_PROGRESS = "progress"
        const val ACTION_MARK_AS_PENDING = "pending"
        const val ACTION_DELETE = "delete"
        const val ACTION_SHARE = "share"
        const val ACTION_FAB_SHOW = "fabShow"
        const val ACTION_FAB_HIDE = "fabHide"
        const val ACTION_ROOT = "root"
        const val ACTION_ALL = "all"
    }

    private fun onActionCallback(data: HomeSpaceTable?, action: Int) {
        when (action) {
            ACTION_NEW -> {
                AddDataDailog(
                    context = requireContext(),
                    data = data,
                    action = action,
                    dataBindingComponent = dataBindingComponent,
                    onSaveCallback = this::onSaveCallback,
                    onCancelCallback = this::onCancelCallback
                ).show()
            }

            ACTION_RENAME -> {
                AddDataDailog(
                    context = requireContext(),
                    data = data,
                    action = action,
                    dataBindingComponent = dataBindingComponent,
                    onSaveCallback = this::onSaveCallback,
                    onCancelCallback = this::onCancelCallback
                ).show()
            }

            ACTION_MORE_INT -> {
                MultipleOptionDailog(
                    context = requireContext(),
                    data = data,
                    onMoreActionCalback = this::onMoreActionCallback,
                    onCancelCallback = this::onCancelCallback
                ).show()
            }
        }
    }

    private fun onSaveCallback(data: HomeSpaceTable?, action: Int) {

        when (action) {
            ACTION_NEW -> {
                if (!data?.text.isNullOrBlank()) {
                    adapter.allListData.clear()
                    viewModel.insertCategory(data)
                    smoothScroll()
                }
            }

            ACTION_RENAME -> {
                shouldHideSearchList = false
                if (data != null && data.text?.trim().isNullOrBlankOrEmpty())
                    viewModel.deleteHomeSpaceData(data = data)
                else viewModel.updateCategory(data)

                adapter.notifyDataSetChanged()
//                if (searchView?.isIconified == false) {
//                    searchView?.isIconified = true
//                }
            }

        }

        binding.fab.show()
    }

    private fun smoothScroll() {
        Handler().postDelayed({
            if (isVisible)
                binding.recycler.smoothScrollToPosition(0)
        }, 200)
    }

    private fun onCancelCallback(action: Int) {
        when (action) {
            ACTION_CANCEL -> {
                binding.fab.show()
            }
        }

    }

    private fun onMoreActionCallback(data: HomeSpaceTable?, action: String) {
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
                Logger.e(Thread.currentThread(), "ACTION_EDIT ${Gson().toJson(data)}")
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

    private fun changeListType(type: String?) {

        when (type) {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //it will let tooltip run else tooltip won't run
        binding.fab.post {

            if (isFirstRun && adapter.allListData.size == 0) {

                binding.fab.startAnimation(
                    AnimationUtils.loadAnimation(
                        requireContext(),
                        R.anim.rotate
                    )
                )

                val metrics = resources.displayMetrics
                tooltip = Tooltip.Builder(requireContext())
                    .anchor(binding.fab, 0, 0, true)
                    .text("add your first note")
                    .styleId(R.style.ToolTipAltStyle)
                    .typeface(Typefaces[requireContext(), "font/roboto.ttf"])
                    .maxWidth(metrics.widthPixels / 2)
                    .arrow(true)
                    .floatingAnimation(Tooltip.Animation.DEFAULT)
                    .closePolicy(ClosePolicy.TOUCH_ANYWHERE_NO_CONSUME)
                    .showDuration(Animation.INFINITE.toLong())
                    .overlay(false)
                    .create()

                tooltip?.doOnHidden {
                    tooltip = null
                }
                    ?.doOnFailure {

                    }
                    ?.doOnShown {

                    }

                    ?.show(binding.fab, Tooltip.Gravity.LEFT, true)

            }
        }
    }

    private fun closeKeyboard() {
        // Check if no view has focus:
        val view = requireActivity().currentFocus
        view?.let { v ->
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    override fun onResume() {
        super.onResume()
        Logger.e(Thread.currentThread(), "virus onResume")
    }

    override fun onPause() {
        super.onPause()
        Logger.e(Thread.currentThread(), "virus onPause")
    }

    override fun onStop() {
        super.onStop()
        Logger.e(Thread.currentThread(), "virus onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.e(Thread.currentThread(), "virus onDestroy")

    }
}
