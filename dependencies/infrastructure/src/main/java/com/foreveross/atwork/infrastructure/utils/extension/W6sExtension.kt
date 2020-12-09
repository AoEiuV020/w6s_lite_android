package com.foreveross.atwork.infrastructure.utils.extension

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.text.method.DigitsKeyListener
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.app.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.foreveross.atwork.infrastructure.BaseApplicationLike
import com.foreveross.atwork.infrastructure.model.discussion.template.DiscussionFeature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.json.JSONArray
import org.json.JSONObject


fun JSONArray.mapJsonObjectList(): ArrayList<JSONObject> {
    val jsonObjectList = ArrayList<JSONObject>()
    (0 until this.length()).forEach { i ->
        jsonObjectList.add(this.optJSONObject(i))
    }

    return jsonObjectList
}


/**
 * 将一种类型转换为另一种类型,如果类型转换不允许，返回null
 * */
inline fun <reified T> Any.asType(): T? {
    return if (this is T) {
        this
    } else {
        null
    }
}

/**
 * 安全的获取值的信息，其过程中发生异常会自动处理，返回null
 * getValueAction 取值操作，可能发生异常
 * */
inline fun <T> getValueSafely(getValueAction: () -> T?): T? {
    return try {
        getValueAction()
    } catch (t: Throwable) {
        t.printStackTrace()
        null
    }
}

fun View.setOnClickEventListener(onClick: (event: MotionEvent) -> Unit) {
    val gestureDetector = GestureDetector(this.context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
            onClick(event)
            return true
        }
    })

    this.setOnTouchListener { v, event ->
        gestureDetector.onTouchEvent(event)
        return@setOnTouchListener true

    }
}


inline fun <reified T : Parcelable> Intent.putParcelableDirectly(value: T) {
    putExtra(T::class.java.toString(), value)
}

inline fun <reified T : Parcelable> Intent.getParcelableDirectly(): T? {
    return getParcelableExtra(T::class.java.toString())
}

inline fun <reified T : Parcelable> Bundle.getParcelableDirectly(): T? {
    return getParcelable(T::class.java.toString())
}


val Any.ctxApp: Context
    get() = BaseApplicationLike.baseContext


fun Context.getColorCompat(@ColorRes id: Int) = ContextCompat.getColor(this, id)

val Context.coroutineScope: CoroutineScope
    get() {
        if (this is LifecycleOwner) {
            return this.lifecycleScope
        }

        return CoroutineScope(Dispatchers.Main)
    }


fun @receiver:ColorRes Int.toColor(context: Context) =  ContextCompat.getColor(context, this)

@ColorInt
inline fun String.toColorIntCompat(): Int {
    return try {
        Color.parseColor(this)
    } catch (e: Exception) {
        0
    }
}


fun TextView.clearText() {
    this.text = ""
}

fun TextView.hasText() = this.text.isNotEmpty()


fun EditText.lastSelection() {
    setSelection(length())
}

fun EditText.setJustLetterAndNumber() {
    this.keyListener = DigitsKeyListener.getInstance("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
}

