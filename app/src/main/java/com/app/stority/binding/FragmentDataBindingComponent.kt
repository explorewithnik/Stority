package com.app.stority.binding

import androidx.databinding.DataBindingComponent
import androidx.fragment.app.Fragment


/**
 * A Data Binding Component implementation for fragments.
 */
class FragmentDataBindingComponent(var fragment: Fragment) : DataBindingComponent {
    override fun getFragmentBindingAdapters(): FragmentBindingAdapters {
        return FragmentBindingAdapters(fragment)
    }
}
