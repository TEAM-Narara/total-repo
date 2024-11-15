package com.ssafy.data.socket

import android.util.Log
import com.ssafy.network.socket.StompResponse
import java.util.PriorityQueue
import java.util.Timer
import java.util.TimerTask


class StompDataHandler(
    startOffset: Long = 0L,
    private val callback: Callback,
) {
    private val priorityQueue = PriorityQueue<StompResponse> { a, b ->
        a.offset.compareTo(b.offset)
    }

    private var lastOffset = startOffset
    private var timer: Timer? = null

    init {
        Log.d("TAG", "startOffset: $startOffset")
        if (lastOffset < 0) callback.onTimeout(lastOffset)
    }

    private val onTimeout
        get() = object : TimerTask() {
            override fun run() {
                priorityQueue.clear()
                callback.onTimeout(lastOffset)
            }
        }

    suspend fun handleSocketData(data: StompResponse) {
        if (data.offset <= lastOffset) return callback.ack(data)
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
            callback.onDataReleased(released)
            callback.ack(released)
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

    interface Callback {
        suspend fun ack(response: StompResponse)
        suspend fun onDataReleased(response: StompResponse)
        fun onTimeout(lastOffset: Long)
    }
}