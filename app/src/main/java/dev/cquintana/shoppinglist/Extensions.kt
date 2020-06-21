package dev.cquintana.shoppinglist

import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import timber.log.Timber

fun View.setMarginExtensionFunction(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
    val params = layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(left, top, right, bottom)
    layoutParams = params
}

fun EditText.onSubmit(func: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        Timber.d("OnEditorAction")
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            func()
        }

        true

    }
}

fun postDelayed(delayMillis: Long, runnable: () -> Unit) {
    Handler().postDelayed(runnable, delayMillis)
}
