package com.app.stority.homeSpace.owner.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.widget.SearchView
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
import com.app.stority.databinding.FragmentHomeSpaceBinding
import com.app.stority.di.Injectable
import com.app.stority.helper.AppExecutors
import com.app.stority.helper.Logger
import com.app.stority.helper.autoCleared
import com.app.stority.homeSpace.data.HomeSpaceTable
import com.app.stority.homeSpace.observer.HomeSpaceViewModel
import com.app.stority.homeSpace.owner.activity.HomeSpaceActivity
import com.app.stority.homeSpace.owner.adapter.HomeSpaceAdapter
import com.app.stority.remoteUtils.Status
import com.app.stority.widget.AddDataDailog
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
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var executors: AppExecutors
    private lateinit var viewModel: HomeSpaceViewModel
    private var isFirstRun: Boolean = false
    private var sharedPref: SharedPreferences? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireActivity().title = getString(R.string.app_name)
        Logger.e(Thread.currentThread(), "onActivityCreated")
        changeListType(sharedPref?.getString(LIST_TYPE, GRID_TYPE))

        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(HomeSpaceViewModel::class.java)

        val stopAnim = viewModel.init(value = "1")
        if (stopAnim) isFirstRun =
            false //stopping tooltip anim when coming back from sub category frag

        adapter = HomeSpaceAdapter(
            isFirstRun = isFirstRun,
            context = requireContext(),
            dataBindingComponent = dataBindingComponent,
            appExecutors = executors
        ) { listData, action ->
            when (action) {

                ACTION_ROOT -> {
                    val fragAction = HomeSpaceFragmentDirections.SubCategoryFragment()
                    fragAction.entryId = listData[0]?.id.toString()
                    fragAction.text = listData[0]?.text.toString()
                    navController().navigate(fragAction)
                }

                ACTION_DELETE -> {
                    viewModel.deleteHomeSpaceListData(list = listData)
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

                ACTION_SHARE -> {
                    Logger.e(Thread.currentThread(), "ACTION_share ${Gson().toJson(listData[0])}")
                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    sharingIntent.type = "text/plain"
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Note")
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, listData[0]?.text)
                    startActivity(Intent.createChooser(sharingIntent, "Share via"))
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
                        Logger.e(Thread.currentThread(), "when")
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
                adapter.notifyItemRemoved(0)
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

        val searchView = actionMenuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Logger.e(Thread.currentThread(), "$query")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Logger.e(Thread.currentThread(), "$newText")
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
            sharedPref?.getString(LIST_TYPE, GRID_TYPE) == GRID_TYPE && adapter.allListData.size > 0

        menu.findItem(R.id.menuGridList)?.isVisible =
            sharedPref?.getString(
                LIST_TYPE,
                GRID_TYPE
            ) == LINEAR_TYPE && adapter.allListData.size > 0
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
        private const val LIST_TYPE = "listType"
        const val GRID_TYPE = "grid"
        const val LINEAR_TYPE = "linear"
        const val ACTION_CANCEL = -1
        const val ACTION_EDIT = "edit"
        const val ACTION_RENAME = 1
        const val ACTION_NEW = 0
        const val ACTION_MORE = "more"
        const val ACTION_MORE_INT = 2
        const val ACTION_COPY = "copy"
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
                viewModel.updateCategory(data)
                adapter.notifyDataSetChanged()
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
}
