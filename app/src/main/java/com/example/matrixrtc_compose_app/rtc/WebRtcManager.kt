import android.content.Context
import com.example.matrixrtc_compose_app.rtc.WebRtcInitializer
import org.webrtc.PeerConnectionFactory

class WebRtcManager (private val context: Context) {
    init {
        WebRtcInitializer.initialize(context)
    }

    private val factory: PeerConnectionFactory by lazy {
        PeerConnectionFactory.builder().createPeerConnectionFactory()
    }
}