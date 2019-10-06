package com.app.stority.homeSpace.owner.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.app.stority.R
import com.app.stority.binding.FragmentDataBindingComponent
import com.app.stority.databinding.FragmentHomeSpaceBinding
import com.app.stority.di.Injectable
import com.app.stority.helper.AppExecutors
import com.app.stority.helper.Logger
import com.app.stority.helper.autoCleared
import com.app.stority.homeSpace.data.HomeSpaceTable
import com.app.stority.homeSpace.observer.HomeSpaceViewModel
import com.app.stority.homeSpace.owner.adapter.HomeSpaceAdapter
import com.app.stority.remoteUtils.Status
import com.app.stority.widget.AddDataDailog
import com.app.stority.widget.MultipleOptionDailog
import com.google.gson.Gson
import javax.inject.Inject


class HomeSpaceFragment : Fragment(), Injectable {
    var binding by autoCleared<FragmentHomeSpaceBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var adapter by autoCleared<HomeSpaceAdapter>()
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var executors: AppExecutors
    lateinit var viewModel: HomeSpaceViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(HomeSpaceViewModel::class.java)

        viewModel.init()

        adapter = HomeSpaceAdapter(
            dataBindingComponent = dataBindingComponent,
            appExecutors = executors
        ) { listData, action ->
            when (action) {

                "item" -> {
                    Logger.e(Thread.currentThread(), "item ${Gson().toJson(listData)}")
                }

                "delete" -> {
                    Logger.e(Thread.currentThread(), "list ite ${Gson().toJson(listData)}")
                    viewModel.deleteHomeSpaceListData(list = listData)
                }
            }
        }

        binding.let {
            it.lifecycleOwner = this
            it.recycler.adapter = adapter
        }
        initEntryList(viewModel)
    }

    private fun initEntryList(viewModel: HomeSpaceViewModel) {
        viewModel.result.observe(this, Observer { listResource ->
            binding.count = listResource?.data?.size
            binding.status = listResource?.status
            endProgress()
            when (listResource?.status) {
                Status.SUCCESS -> {
                    endProgress()
                    if (listResource.data != null) {
                        adapter.submitList(listResource.data)
                    } else {
                        adapter.submitList(emptyList())
                    }
                }
                Status.ERROR -> {

                    endProgress()
                    if (listResource.code == 502) {
                        // ServerErrorDialog(context).show()
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
            R.layout.fragment_home_space,
            container,
            false,
            dataBindingComponent
        )

        binding.fab.setOnClickListener {
            onActionCallback(HomeSpaceTable(), ACTION_NEW)
        }

        return binding.root
    }

    fun navController() = findNavController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_space_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.menuSearch)?.isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSearch -> {
                Logger.e(Thread.currentThread(), "search")
            }
        }
        return true
    }

    companion object {
        const val ACTION_EDIT = 1
        const val ACTION_NEW = 0
        const val ACTION_MORE = 2
        const val ACTION_COPY = "copy"
        const val ACTION_DELETE = "delete"
        const val ACTION_SHARE = "share"
    }

    private fun onActionCallback(data: HomeSpaceTable, action: Int) {
        when (action) {
            ACTION_NEW -> {
                AddDataDailog(
                    context = requireContext(),
                    data = data,
                    action = action,
                    dataBindingComponent = dataBindingComponent,
                    onSaveCallback = this::onSaveCallback
                ).show()
            }

            ACTION_MORE -> {
                MultipleOptionDailog(
                    context = requireContext(),
                    data = data,
                    onMoreActionCalback = this::onMoreActionCallback
                ).show()
            }
        }
    }

    private fun onSaveCallback(data: HomeSpaceTable, action: Int) {
        viewModel.insertCategory(data)
    }

    private fun onMoreActionCallback(data: HomeSpaceTable, action: String) {
        Logger.e(Thread.currentThread(), "data 2 ${Gson().toJson(data)} action $action")
        when (action) {
            ACTION_COPY -> {
                val clipboard =
                    requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
                val clip = ClipData.newPlainText("label", data.text)
                clipboard?.setPrimaryClip(clip)

                Toast.makeText(requireContext(), "copied", Toast.LENGTH_SHORT).show()
            }

            ACTION_DELETE -> {
                viewModel.deleteHomeSpaceData(data = data)
            }

            ACTION_SHARE -> {
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Note")
                sharingIntent.putExtra(Intent.EXTRA_TEXT, data.text)
                startActivity(Intent.createChooser(sharingIntent, "Share via"))
            }

        }
    }
}
