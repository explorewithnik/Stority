package com.app.stority.homeSpace.owner.fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
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
import com.app.stority.helper.autoCleared
import com.app.stority.homeSpace.data.HomeSpaceTable
import com.app.stority.homeSpace.observer.HomeSpaceViewModel
import com.app.stority.homeSpace.owner.adapter.HomeSpaceAdapter
import com.app.stority.remoteUtils.Status
import com.app.stority.widget.AddDataDailog
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
        setUpToolbar()
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(HomeSpaceViewModel::class.java)

        viewModel.init()

        adapter = HomeSpaceAdapter(
            dataBindingComponent = dataBindingComponent,
            appExecutors = executors
        ) { data, action ->
            when (action) {
                "item" -> {

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

    private fun setUpToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            setDisplayShowTitleEnabled(true)
            //setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.home_space)
        }

        //binding.toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }

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

            }
        }
        return true
    }

    companion object {
        const val ACTION_EDIT = 1
        const val ACTION_NEW = 0
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
        }
    }

    private fun onSaveCallback(data: HomeSpaceTable, action: Int) {
        viewModel.insertCategory(data)
    }
}