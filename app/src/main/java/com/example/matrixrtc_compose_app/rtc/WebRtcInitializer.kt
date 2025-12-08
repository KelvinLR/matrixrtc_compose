package com.example.matrixrtc_compose_app.rtc

import android.content.Context
import org.webrtc.PeerConnectionFactory

object WebRtcInitializer {

    private var initialized = false

    fun initialize(context: Context) {
        if (initialized) return

        val options = PeerConnectionFactory.InitializationOptions.builder(context)
            .setEnableInternalTracer(true)
            .createInitializationOptions()

        PeerConnectionFactory.initialize(options)

        initialized = true
    }
}