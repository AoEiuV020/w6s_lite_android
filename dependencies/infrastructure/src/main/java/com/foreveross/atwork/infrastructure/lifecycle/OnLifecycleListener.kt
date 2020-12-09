package com.foreveross.atwork.infrastructure.lifecycle

import android.content.Intent

interface OnLifecycleListener {
    fun onActivityResult(intent: Intent)
}