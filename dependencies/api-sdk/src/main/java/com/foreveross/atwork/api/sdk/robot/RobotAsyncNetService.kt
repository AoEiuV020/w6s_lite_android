package com.foreveross.atwork.api.sdk.robot

import com.foreveross.atwork.api.sdk.NetWorkFailListener
import com.foreveross.atwork.api.sdk.robot.response.GetInstructResponse

class RobotAsyncNetService{
    /**
     * 查询语音指令
     */
    interface OnQueryListener : NetWorkFailListener {
        fun onSuccess(discussion: GetInstructResponse.Data)
        fun onFail(massage: String)
    }

    /**
     * 数据库读写callback
     */
    interface OnSqlListener {
        fun onCallback(isSuccess: Boolean)
    }
}