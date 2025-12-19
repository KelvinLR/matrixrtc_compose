package com.example.matrixrtc_compose_app.rtc

import android.content.Context
import android.util.Log
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.RtpReceiver
import org.webrtc.SessionDescription
import org.webrtc.SdpObserver
import org.webrtc.AudioSource
import org.webrtc.AudioTrack
import org.webrtc.DataChannel
import org.webrtc.SurfaceTextureHelper
import org.webrtc.EglBase
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.MediaStream



class WebRtcManager(private val context: Context) {

    // ---------------------------------------------------------------------------------------------
    // STATE
    // ---------------------------------------------------------------------------------------------
    private var peerConnection: PeerConnection? = null
    private var remoteDescriptionSet: Boolean = false
    private val pendingRemoteCandidates = mutableListOf<IceCandidate>()

    var onLocalSdp: ((type: String, sdp: String) -> Unit)? = null
    var onLocalIceCandidate: ((IceCandidate) -> Unit)? = null
    var onError: ((String) -> Unit)? = null

    // ---------------------------------------------------------------------------------------------
    // FACTORY
    // ---------------------------------------------------------------------------------------------
    private val eglBase = EglBase.create()

    private val factory: PeerConnectionFactory by lazy {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context)
                .setEnableInternalTracer(true)
                .createInitializationOptions()
        )

        PeerConnectionFactory.builder()
            .setVideoEncoderFactory(DefaultVideoEncoderFactory(eglBase.eglBaseContext, true, true))
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase.eglBaseContext))
            .createPeerConnectionFactory()
    }

    // ---------------------------------------------------------------------------------------------
    // CONFIG
    // ---------------------------------------------------------------------------------------------
    private val iceServers = listOf(
        PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
    )

    private val rtcConfig = PeerConnection.RTCConfiguration(iceServers).apply {
        sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
    }

    // ---------------------------------------------------------------------------------------------
    // PEER CONNECTION
    // ---------------------------------------------------------------------------------------------
    fun createPeerConnection() {
        Log.i("RTC", "Observer class = " + org.webrtc.PeerConnection.Observer::class.java.protectionDomain.codeSource.location)

        peerConnection = factory.createPeerConnection(rtcConfig, object : org.webrtc.PeerConnection.Observer {
            override fun onSignalingChange(newState: PeerConnection.SignalingState) {
                Log.i("RTC", "Signaling: $newState")
            }

            override fun onIceConnectionChange(state: PeerConnection.IceConnectionState) {
                Log.i("RTC", "IceConnectionState: $state")
            }

            override fun onConnectionChange(newState: PeerConnection.PeerConnectionState) {
                Log.i("RTC", "ConnectionState: $newState")
            }

            override fun onIceConnectionReceivingChange(p0: Boolean) {
                TODO("Not yet implemented")
            }

            override fun onIceCandidate(candidate: IceCandidate) {
                Log.i("RTC", "ICE: $candidate")
                onLocalIceCandidate?.invoke(candidate)
            }

            override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
                TODO("Not yet implemented")
            }

            override fun onAddStream(p0: MediaStream?) {
                TODO("Not yet implemented")
            }

            override fun onRemoveStream(p0: MediaStream?) {
                TODO("Not yet implemented")
            }

            override fun onDataChannel(p0: DataChannel?) {
                TODO("Not yet implemented")
            }

            override fun onIceGatheringChange(state: PeerConnection.IceGatheringState) {
                Log.i("RTC", "IceGathering: $state")
            }

            override fun onRenegotiationNeeded() {
                Log.i("RTC", "Renegotiation needed")
            }

            override fun onAddTrack(receiver: RtpReceiver?, streams: Array<out MediaStream>?) {}
        })

        Log.i("RTC", "PeerConnection criada: $peerConnection")
    }

    fun addDummyAudioTrack() {
        val audioSource = factory.createAudioSource(MediaConstraints())
        val audioTrack = factory.createAudioTrack("AUDIO", audioSource)

        peerConnection?.addTrack(audioTrack)
        Log.i("RTC", "Dummy audio track adicionada")
    }

    fun createOffer() {
        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        }

        peerConnection?.createOffer(object : SdpObserver {
            override fun onCreateSuccess(desc: SessionDescription) {
                Log.i("RTC", "Offer criada")

                peerConnection?.setLocalDescription(object : SdpObserver {
                    override fun onSetSuccess() {
                        Log.i("RTC", "LocalDescription (offer) setada")
                        onLocalSdp?.invoke("offer", desc.description)
                    }

                    override fun onSetFailure(error: String?) {
                        onError?.invoke("Erro setLocalDescription(offer): $error")
                    }

                    override fun onCreateSuccess(p0: SessionDescription?) {}
                    override fun onCreateFailure(p0: String?) {}
                }, desc)
            }

            override fun onCreateFailure(error: String?) {
                onError?.invoke("Erro createOffer: $error")
            }

            override fun onSetSuccess() {}
            override fun onSetFailure(p0: String?) {}
        }, constraints)
    }

    fun createAnswer() {
        val constraints = MediaConstraints()

        peerConnection?.createAnswer(object : SdpObserver {
            override fun onCreateSuccess(desc: SessionDescription) {
                Log.i("RTC", "Answer criada")

                peerConnection?.setLocalDescription(object : SdpObserver {
                    override fun onSetSuccess() {
                        Log.i("RTC", "LocalDescription (answer) setada")
                        onLocalSdp?.invoke("answer", desc.description)
                    }

                    override fun onSetFailure(error: String?) {
                        onError?.invoke("Erro setLocalDescription(answer): $error")
                    }

                    override fun onCreateSuccess(p0: SessionDescription?) {}
                    override fun onCreateFailure(p0: String?) {}
                }, desc)
            }

            override fun onCreateFailure(error: String?) {
                onError?.invoke("Erro createAnswer: $error")
            }

            override fun onSetSuccess() {}
            override fun onSetFailure(p0: String?) {}
        }, constraints)
    }

    fun setRemoteDescription(
        type: String,
        sdp: String,
        onSetSuccess: (() -> Unit)? = null
    ) {
        val description = SessionDescription(
            if (type == "offer") SessionDescription.Type.OFFER else SessionDescription.Type.ANSWER,
            sdp
        )

        peerConnection?.setRemoteDescription(object : SdpObserver {
            override fun onSetSuccess() {
                Log.i("RTC", "RemoteDescription aplicada ($type)")
                remoteDescriptionSet = true

                // aplicar ICE pendentes
                if (pendingRemoteCandidates.isNotEmpty()) {
                    Log.i("RTC", "Aplicando ${pendingRemoteCandidates.size} ICEs pendentes...")
                    pendingRemoteCandidates.forEach { cand ->
                        val ok = peerConnection?.addIceCandidate(cand)
                        Log.i("RTC", "ICE pendente aplicado (ok=$ok): $cand")
                    }
                    pendingRemoteCandidates.clear()
                }

                onSetSuccess?.invoke()
            }

            override fun onSetFailure(error: String?) {
                onError?.invoke("Erro setRemoteDescription: $error")
            }

            override fun onCreateSuccess(p0: SessionDescription?) {}
            override fun onCreateFailure(p0: String?) {}
        }, description)
    }

    fun addIceCandidate(sdpMid: String?, sdpMLineIndex: Int, candidate: String) {
        val ice = IceCandidate(sdpMid, sdpMLineIndex, candidate)

        if (!remoteDescriptionSet) {
            pendingRemoteCandidates.add(ice)
            Log.i("RTC", "ICE bufferizado (aguardando remoteDescription): $ice")
            return
        }

        val ok = peerConnection?.addIceCandidate(ice)
        Log.i("RTC", "ICE aplicado (ok=$ok): $ice")
    }
}
