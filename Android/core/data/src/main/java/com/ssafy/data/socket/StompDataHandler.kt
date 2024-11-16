package com.ssafy.data.socket

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
        if (startOffset < 0) callback.onTimeout(startOffset)
    }

    private val onTimeout
        get() = object : TimerTask() {
            override fun run() {
                priorityQueue.clear()
                callback.onTimeout(lastOffset)
            }
        }

    suspend fun handleSocketData(response: StompResponse) {
        if (response.offset <= lastOffset) return callback.ack(response)
        priorityQueue.offer(response)
        checkAndReleaseData()
        if (priorityQueue.isNotEmpty()) startTimer()
    }

    private suspend fun checkAndReleaseData() {
        while (priorityQueue.isNotEmpty()) {
            val response = priorityQueue.peek() ?: break
            val currentOffset = response.offset
            if (currentOffset != lastOffset + 1 && !callback.superPass(response)) break

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
        fun superPass(response: StompResponse): Boolean
    }
}