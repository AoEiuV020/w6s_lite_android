package com.foreveross.atwork.infrastructure.model.deling

import com.google.gson.annotations.SerializedName

class KeyInfo(
        @SerializedName("pid")
        var pid: String,

        @SerializedName("lock_id")
        var lockId: String)