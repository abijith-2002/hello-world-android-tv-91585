package com.example.tv.ui.player

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tv.R
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.ui.PlayerView

/**
 * PUBLIC_INTERFACE
 * PlayerActivity
 *
 * Simple full-screen ExoPlayer-based activity for Android TV playback.
 * - Accepts a media URL via intent extra EXTRA_MEDIA_URL.
 * - Prepares and starts playback.
 * - Keeps the screen on while playing.
 * - Handles lifecycle following Android's recommendations:
 *   - Initialize in onStart()/onResume depending on SDK version
 *   - Release in onStop()/onPause respectively
 * - Supports TV remote Play/Pause via default PlayerView controls and DPAD_CENTER toggling.
 */
class PlayerActivity : AppCompatActivity() {

    private var player: SimpleExoPlayer? = null
    private var playerView: PlayerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Fullscreen and keep screen on for TV playback
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_player)
        playerView = findViewById(R.id.exo_player_view)

        // Make sure the player view can receive DPAD focus
        playerView?.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()
        }
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initializePlayerIfNeeded()
        }
    }

    override fun onResume() {
        super.onResume()
        if ((Util.SDK_INT < 24) || player == null) {
            initializePlayerIfNeeded()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    private fun initializePlayerIfNeeded() {
        if (player != null) return
        val url = intent.getStringExtra(EXTRA_MEDIA_URL)
        if (url.isNullOrBlank()) {
            Toast.makeText(this, getString(R.string.error_no_media_url), Toast.LENGTH_LONG).show()
            finish()
            return
        }
        player = SimpleExoPlayer.Builder(this).build().also { exo ->
            playerView?.player = exo
            exo.addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    Toast.makeText(this@PlayerActivity, "Playback error: ${error.message}", Toast.LENGTH_LONG).show()
                }
            })
            val mediaItem = MediaItem.fromUri(Uri.parse(url))
            exo.setMediaItem(mediaItem)
            exo.prepare()
            exo.playWhenReady = true
        }
    }

    private fun releasePlayer() {
        playerView?.player = null
        player?.release()
        player = null
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Toggle play/pause with DPAD_CENTER if desired in addition to default PlayerView controls
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
            player?.let {
                it.playWhenReady = !it.playWhenReady
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    companion object {
        const val EXTRA_MEDIA_URL = "extra_media_url"

        // PUBLIC_INTERFACE
        fun createIntent(context: Context, url: String): Intent {
            /** This is a public function. Create an Intent carrying EXTRA_MEDIA_URL for PlayerActivity playback. */
            return Intent(context, PlayerActivity::class.java).apply {
                putExtra(EXTRA_MEDIA_URL, url)
            }
        }
    }
}
