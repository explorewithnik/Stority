package com.app.stority.homeSpace.owner.fragment

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.app.stority.R
import com.app.stority.binding.FragmentDataBindingComponent
import com.app.stority.databinding.FragmentSearchBinding
import com.app.stority.di.Injectable
import com.app.stority.helper.AppExecutors
import com.app.stority.helper.Logger
import com.app.stority.helper.autoCleared
import com.app.stority.homeSpace.observer.SearchViewModel
import com.app.stority.homeSpace.owner.adapter.SearchAdapter
import com.app.stority.remoteUtils.Status
import javax.inject.Inject

class SearchFragment : Fragment(), Injectable {
    var binding by autoCleared<FragmentSearchBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private var adapter by autoCleared<SearchAdapter>()
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var executors: AppExecutors

    private lateinit var viewModel: SearchViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(SearchViewModel::class.java)

        adapter = SearchAdapter(
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
            }
        }

        binding.let {
            it.lifecycleOwner = this
            it.recycler.adapter = adapter
        }

        initEntryList(viewModel)
    }

    private fun initEntryList(viewModel: SearchViewModel) {
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false, dataBindingComponent)
        return binding.root
    }

    private fun navController() = findNavController()

    companion object {
        const val ACTION_ROOT = "root"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

}
