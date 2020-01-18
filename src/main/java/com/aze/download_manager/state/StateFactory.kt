package com.aze.download_manager.state

import com.aze.download_manager.task.TaskBean

class StateFactory {
    companion object {
        fun create(status: TaskBean.Status): State? {
            return when (status) {
                TaskBean.Status.CREATE -> CreateState()
                TaskBean.Status.START -> StartState()
                TaskBean.Status.PAUSE -> PauseState()
                TaskBean.Status.STOP -> StopState()
                TaskBean.Status.DESTROY -> DestroyState()
                else -> null
            }

        }

    }
}