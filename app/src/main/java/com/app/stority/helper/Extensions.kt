package com.app.stority.helper

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

infix fun <T> Boolean.then(param: T): T? = if (this) param else null

infix fun <T> Boolean.then(param: () -> T): T? = if (this) param() else null

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}

fun String?.isNullOrBlankOrEmpty(): Boolean = this.isNullOrBlank() || this.isNullOrEmpty()

fun List<Any>?.isNullOrBlankOrEmpty(): Boolean = this==null || this.isNullOrEmpty() || this.isEmpty() || this.size == 0