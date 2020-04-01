package com.app.stority.widget

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import java.text.SimpleDateFormat
import java.util.*

class CommonMethods {

    companion object {

        fun setUpToolbar(
            requireActivity: FragmentActivity,
            title: String,
            fragmentName: String
        ) {

            when (fragmentName) {

                "HomeSpaceFragment" -> {
                    val actionBar = (requireActivity as AppCompatActivity).supportActionBar
                    actionBar?.setDisplayShowTitleEnabled(true)
                    actionBar?.setDisplayHomeAsUpEnabled(false)
                    actionBar?.title = title
                }

                "SubCategoryFragment" -> {
                    val actionBar = (requireActivity as AppCompatActivity).supportActionBar
                    actionBar?.setDisplayShowTitleEnabled(true)
                    actionBar?.setDisplayHomeAsUpEnabled(true)
                    actionBar?.title = title
                }

                "SubCategoryViewFragment" -> {
                    val actionBar = (requireActivity as AppCompatActivity).supportActionBar
                    actionBar?.setDisplayShowTitleEnabled(true)
                    actionBar?.setDisplayHomeAsUpEnabled(true)

                    title.let { timeStamp ->
                        if (timeStamp.isNotBlank() && timeStamp.isNotEmpty()) {
                            actionBar?.title = bindDateTime(
                                Date(timeStamp.toLong()),
                                "MMM dd, hh:mm aa",
                                ""
                            )
                        }
                    }
                }

            }
        }

        fun bindDateTime(date: Date?, format: String?, emptyTxt: String): String {
            return try {
                if (date != null) {
                    val dateFormat = SimpleDateFormat(format, Locale.ENGLISH)
                    dateFormat.format(date)
                } else {
                    emptyTxt
                }
            } catch (e: Exception) {
                emptyTxt
                //if (AppConfig.DEBUG_MODE) e.printStackTrace()
            }

        }

    }

}