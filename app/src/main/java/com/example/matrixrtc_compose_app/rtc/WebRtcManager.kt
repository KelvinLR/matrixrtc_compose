import android.content.Context
import android.util.Log
import com.example.matrixrtc_compose_app.rtc.WebRtcInitializer
import org.webrtc.PeerConnectionFactory

class WebRtcManager (private val context: Context) {
    private val factory: PeerConnectionFactory by lazy {
        PeerConnectionFactory.builder().createPeerConnectionFactory()
    }

    init {
        WebRtcInitializer.initialize(context)
    }

    fun testWebRtc () {
        Log.i("RTC", "iniciando WebRTC...")

        WebRtcInitializer.initialize(context)

        val factory = PeerConnectionFactory.builder().createPeerConnectionFactory()

        Log.i("RTC", "Factory criado: $factory")
    }
}




