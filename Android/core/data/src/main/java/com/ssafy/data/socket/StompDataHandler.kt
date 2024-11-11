package com.ssafy.data.socket

import com.ssafy.network.socket.StompResponse
import java.util.PriorityQueue
import java.util.Timer
import java.util.TimerTask

class StompDataHandler<R>(
    startOffset: Long = 0L,
    private val onDataReleased: suspend (StompResponse<R>) -> Unit
) {
    private val priorityQueue = PriorityQueue<StompResponse<R>> { a, b ->
        a.offset.compareTo(b.offset)
    }

    private var lastOffset = startOffset
    private var timer: Timer? = null

    private val onTimeout
        get() = object : TimerTask() {
            override fun run() {
                throw RuntimeException("offset이 일치하지 않습니다.")
            }
        }

    suspend fun handleSocketData(data: StompResponse<R>) {
        if (data.offset <= lastOffset) return
        priorityQueue.offer(data)
        checkAndReleaseData()
        if (priorityQueue.isNotEmpty()) startTimer()
    }

    private suspend fun checkAndReleaseData() {
        while (priorityQueue.isNotEmpty()) {
            val currentOffset = priorityQueue.peek()?.offset
            if (currentOffset != lastOffset + 1) break

            val released = priorityQueue.poll() ?: break
            lastOffset = released.offset

            stopTimer()
            onDataReleased(released)
        }
    }

    private fun startTimer() {
        stopTimer()
        timer = Timer().apply { schedule(onTimeout, 3000) }
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
    }
}