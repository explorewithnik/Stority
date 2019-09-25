package com.app.stority.displayImage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.app.stority.R
import com.app.stority.binding.FragmentDataBindingComponent
import com.app.stority.databinding.FragmentDisplayBinding
import com.app.stority.di.Injectable
import com.app.stority.helper.AppExecutors
import com.app.stority.helper.Logger
import com.app.stority.helper.autoCleared

import javax.inject.Inject

class DisplayFragment : Fragment(), Injectable {

    var binding by autoCleared<FragmentDisplayBinding>()

    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private var imageList by autoCleared<List<String>>()

    private var currentImage = 1
    private var startPosition = 0

    @Inject
    lateinit var executors: AppExecutors

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (requireActivity().intent.hasExtra(IMAGE_LIST)) {
            imageList = requireActivity().intent.getStringArrayListExtra(IMAGE_LIST) as List<String>
            startPosition = requireActivity().intent.getIntExtra(IMAGE_POSITION, 0)
            currentImage = startPosition + 1
        }

        Logger.e(Thread.currentThread(), "ImageList: $imageList")


        binding.let {
            it.lifecycleOwner = this
            it.shouldVisible = imageList.size > 1
            it.currentImage = currentImage
            it.totalImage = imageList.size
            it.imageUrl = imageList[startPosition]
        }

        binding.next.setOnClickListener {
            currentImage += 1
            binding.currentImage = currentImage
            binding.imageUrl = imageList[currentImage - 1]
        }

        binding.prev.setOnClickListener {
            currentImage -= 1
            binding.currentImage = currentImage
            binding.imageUrl = imageList[currentImage - 1]
        }

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().finish()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_display,
                container,
                false,
                dataBindingComponent)

        return binding.root
    }

    companion object {
        const val IMAGE_LIST = "imageList"
        const val IMAGE_POSITION = "imagePosition"
    }

}