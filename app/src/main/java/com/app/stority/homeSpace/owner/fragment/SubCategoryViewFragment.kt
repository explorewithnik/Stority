package com.app.stority.homeSpace.owner.fragment

import android.content.*
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.app.stority.R
import com.app.stority.binding.FragmentDataBindingComponent
import com.app.stority.databinding.FragmentSubCategoryViewBinding
import com.app.stority.di.Injectable
import com.app.stority.helper.*
import com.app.stority.homeSpace.data.HomeSpaceTable
import com.app.stority.homeSpace.data.SubCategoryTable
import com.app.stority.homeSpace.observer.SubCategoryViewModel
import com.app.stority.homeSpace.owner.activity.HomeSpaceActivity
import com.app.stority.widget.CommonMethods
import com.app.stority.widget.ConfirmationDailog
import com.app.stority.widget.ConfirmationDailog.Companion.SUB_CATEGORY_DATA
import com.app.stority.widget.OnBackPressed
import javax.inject.Inject

class SubCategoryViewFragment : Fragment(), Injectable, OnBackPressed {
    var binding by autoCleared<FragmentSubCategoryViewBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private val viewModel: SubCategoryViewModel by viewModels { viewModelFactory }

    private var editedText = ""

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var executors: AppExecutors
    private var isFirstRun = false
    private var sharedPref: SharedPreferences? = null
    private var text = ""
    private var entryId = "-1"
    private var title = ""
    private var parentId = "-1"

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(FIRST_RUN_ID, isFirstRun)
        outState.putString(TEXT_ID, text)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        requireActivity().title = ""

        title = savedInstanceState?.getString(title)
            ?: SubCategoryViewFragmentArgs.fromBundle(arguments!!).timeStamp

        CommonMethods.setUpToolbar(requireActivity(), title, "SubCategoryViewFragment")
//        Logger.e(Thread.currentThread(), "onActivityCreated")

        text = savedInstanceState?.getString(text)
            ?: SubCategoryViewFragmentArgs.fromBundle(arguments!!).text


        entryId = savedInstanceState?.getString(entryId)
            ?: SubCategoryViewFragmentArgs.fromBundle(arguments!!).entryId

        parentId = savedInstanceState?.getString(parentId)
            ?: SubCategoryViewFragmentArgs.fromBundle(arguments!!).parentId

//        Logger.e(Thread.currentThread(), "parentId $parentId")


        isFirstRun = (activity as HomeSpaceActivity).isFirstRun ?: false
//        Logger.e(Thread.currentThread(), "firstTimeLaunch SubCategoryViewFragment $isFirstRun")
//        Logger.e(Thread.currentThread(), "text SubCategoryViewFragment $text")
//        Logger.e(Thread.currentThread(), "entryId SubCategoryViewFragment $entryId")

        binding.let {
            it.lifecycleOwner = this
            it.isFirstRun = isFirstRun
            it.text = text
        }

        binding.editView.afterTextChanged {
            editedText = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_sub_category_view,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        sharedPref = (activity as HomeSpaceActivity).sharedPref
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sub_category_view_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.sub_menu_copy)?.isVisible = true
        menu.findItem(R.id.sub_menu_delete)?.isVisible = true
        menu.findItem(R.id.sub_menu_share)?.isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when (item.itemId) {

            android.R.id.home -> {
                backPress()
            }

            R.id.sub_menu_copy -> {
                val clipboard =
                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                val clip = ClipData.newPlainText("label", text)
                clipboard?.setPrimaryClip(clip)
//                Logger.e(Thread.currentThread(), "clip $clip")

                Toast.makeText(requireContext(), "copied", Toast.LENGTH_SHORT).show()
            }

            R.id.sub_menu_delete -> {
                if (!entryId.isBlank() || entryId != "-1" || entryId.isNotEmpty()) {
                    val data = SubCategoryTable()
                    data.subCategoryId = entryId.toInt()

                    ConfirmationDailog(
                        context = requireContext(),
                        data = data,
                        title = "This note will be deleted",
                        homeSpaceData = listOf(HomeSpaceTable()),
                        action = ACTION_DELETE,
                        typeOfDataToDelete = SUB_CATEGORY_DATA,
                        dataBindingComponent = dataBindingComponent,
                        onDeleteCallback = this::onDeleteCallback,
                        onHomeSpaceDeleteCallback = this::onHomeSpaceDeleteCallback,
                        onCancelCallback = this::onCancelCallback,
                        onSubCategoryListDeleteCallback = this::onSubCategoryListDeleteCallback
                    ).show()
                }
            }

            R.id.sub_menu_share -> {
                Logger.e(Thread.currentThread(), "ACTION_share $text")
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Note")
                sharingIntent.putExtra(Intent.EXTRA_TEXT, text)
                startActivity(Intent.createChooser(sharingIntent, "Share via"))
            }
        }
        return true
    }

    companion object {
        const val ACTION_EDIT = "edit"
        const val ACTION_COPY = "copy"
        const val ACTION_DELETE = "delete"
        const val ACTION_SHARE = "share"
        const val TEXT_ID = "text_id"
        const val FIRST_RUN_ID = "first_run_id"
    }

    private fun onDeleteCallback(data: SubCategoryTable?, action: String) {
        viewModel.deleteSubCategoryData(data = data)
        findNavController().popBackStack()
    }

    private fun onHomeSpaceDeleteCallback(data: List<HomeSpaceTable?>, action: String) {

    }

    private fun onCancelCallback(action: String) {

    }

    private fun onSubCategoryListDeleteCallback(list: List<SubCategoryTable?>, action: String) {

    }


    override fun onBackPress() {
        backPress()
    }

    private fun backPress() {
        closeKeyboard()
        if (editedText.trim() == text) {
            Logger.e(Thread.currentThread(), "text not edited  $editedText")
            findNavController().popBackStack()
        } else {
            if (!editedText.trim().isNullOrBlankOrEmpty()) {
                Logger.e(Thread.currentThread(), "text edited  $editedText")
                val subCategoryTable = SubCategoryTable()
                subCategoryTable.text = editedText.trim()
                subCategoryTable.subCategoryId = entryId.toInt()
                subCategoryTable.timeStamp = System.currentTimeMillis().toString()
                subCategoryTable.id = parentId.toInt()
                viewModel.updateSubCategory(subCategoryTable)
                Toast.makeText(requireContext(), "updated", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                val subCategoryTable = SubCategoryTable()
                subCategoryTable.subCategoryId = entryId.toInt()
                viewModel.deleteSubCategoryData(subCategoryTable)
                Toast.makeText(requireContext(), "deleted", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
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


}


