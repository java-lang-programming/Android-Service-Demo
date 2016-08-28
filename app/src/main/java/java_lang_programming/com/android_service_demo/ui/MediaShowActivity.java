/**
 * Copyright (C) 2016 Programming Java Android Development Project
 * Programming Java is
 *
 *      http://java-lang-programming.com/
 *
 * UI Media Generator version : 0.2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java_lang_programming.com.android_service_demo.ui;

import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaCodec;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.SampleSource;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioTrack;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.Util;

import java.io.IOException;

import java_lang_programming.com.android_service_demo.R;

public class MediaShowActivity extends AppCompatActivity implements SurfaceHolder.Callback,
        ExoPlayer.Listener, ExtractorSampleSource.EventListener, MediaCodecVideoTrackRenderer.EventListener,
        MediaCodecAudioTrackRenderer.EventListener {

    private static final String TAG = "MediaShowActivity";

    private RelativeLayout buttonLayout;
    private Button playStart;
    private Button playStop;
    private SurfaceView surfaceView;
    private ExoPlayer exoPlayer;
    private Handler mainHandler;

    private long playerPosition;

    private int rendererBuildingState;
    private int lastReportedPlaybackState;
    private boolean lastReportedPlayWhenReady;

    private static final int RENDERER_BUILDING_STATE_IDLE = 1;
    private static final int RENDERER_BUILDING_STATE_BUILDING = 2;
    private static final int RENDERER_BUILDING_STATE_BUILT = 3;

    private static final int RENDERER_COUNT = 2;

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 160;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_show);

        buttonLayout = (RelativeLayout) findViewById(R.id.button_layout);
        playStart  = (Button) findViewById(R.id.play_start);
        playStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exoPlayer.setPlayWhenReady(true);
            }
        });

        playStop  = (Button) findViewById(R.id.play_stop);
        playStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exoPlayer.setPlayWhenReady(false);
            }
        });

        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        surfaceView.getHolder().addCallback(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Factory.newInstance(RENDERER_COUNT, 1000, 5000);
            // add Listener for a callback to be notified of changes
            exoPlayer.addListener(this);
            mainHandler = new Handler();

            // Bandwidth(帯域)
            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter(mainHandler, null);
            // DataSource(メディアがどこにあるかを指定する)
            DataSource dataSource = new DefaultUriDataSource(this, bandwidthMeter, Util.getUserAgent(this, "SampleExoPlayer"));
            // create Allocator
            DefaultAllocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);

            SampleSource sampleSource = new ExtractorSampleSource(
                    getVideoUri(),
                    dataSource,
                    allocator,
                    BUFFER_SEGMENT_SIZE * BUFFER_SEGMENT_COUNT,
                    mainHandler,
                    this,
                    0);

            // データを再生するレンダラーを生成します。今回は動画なので、 "映像" と "音声" の 2 つ
            MediaCodecVideoTrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(
                    getApplicationContext(),
                    sampleSource,
                    MediaCodecSelector.DEFAULT,
                    MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT,
                    5000,
                    mainHandler,
                    this,
                    50);

            MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(
                    sampleSource,
                    MediaCodecSelector.DEFAULT,
                    null,
                    true,
                    mainHandler,
                    this,
                    AudioCapabilities.getCapabilities(getApplicationContext()),
                    AudioManager.STREAM_MUSIC);

            // IDLE状態
            lastReportedPlaybackState = ExoPlayer.STATE_IDLE;
            // レンダラーの状態
            rendererBuildingState = RENDERER_BUILDING_STATE_IDLE;

            exoPlayer.seekTo(playerPosition);

            // 作成したレンダラーを設定する
            exoPlayer.prepare(videoRenderer, audioRenderer);

            // Pass the surface to the video renderer.
            exoPlayer.sendMessage(videoRenderer,
                    MediaCodecVideoTrackRenderer.MSG_SET_SURFACE,
                    surfaceView.getHolder().getSurface());
        } else {
            Log.d(TAG, "onPlayerStateChanged, lastReportedPlayWhenReady: " + lastReportedPlayWhenReady + ", lastReportedPlaybackState: " + lastReportedPlaybackState);
            if (lastReportedPlayWhenReady && lastReportedPlaybackState == ExoPlayer.STATE_READY) {
                exoPlayer.setPlayWhenReady(true);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            buttonLayout.setVisibility(View.GONE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            buttonLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }

    @Override
    public void onLoadError(int sourceId, IOException e) {
        Log.d(TAG, "onLoadError. Cause is sourceId :" + sourceId + ", IOException :" + e.getMessage());
    }

    @Override
    public void onDroppedFrames(int count, long elapsed) {
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
    }

    @Override
    public void onDrawnToSurface(Surface surface) {
    }

    @Override
    public void onDecoderInitializationError(MediaCodecTrackRenderer.DecoderInitializationException e) {
    }

    @Override
    public void onCryptoError(MediaCodec.CryptoException e) {
    }

    @Override
    public void onDecoderInitialized(String decoderName, long elapsedRealtimeMs, long initializationDurationMs) {
    }

    @Override
    public void onAudioTrackInitializationError(AudioTrack.InitializationException e) {
    }

    @Override
    public void onAudioTrackWriteError(AudioTrack.WriteException e) {
    }

    @Override
    public void onAudioTrackUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {
    }

    /**
     * Invoked when the value returned from either {@link ExoPlayer#getPlayWhenReady()} or
     * {@link ExoPlayer#getPlaybackState()} changes.
     *
     * @param playWhenReady Whether playback will proceed when ready. for example, exoPlayer.setPlayWhenReady(false) is false.
     * @param playbackState One of the {@code STATE} constants defined in the {@link ExoPlayer}
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        Log.d(TAG, "onPlayerStateChanged, playWhenReady: " + playWhenReady + ", playbackState: " + playbackState);
        // start
        if (playWhenReady && ExoPlayer.STATE_READY == playbackState) {
            Log.d(TAG, "end : " + System.currentTimeMillis());
        }
        maybeReportPlayerState();
    }

    private void maybeReportPlayerState() {
        boolean playWhenReady = exoPlayer.getPlayWhenReady();
        int playbackState = getPlaybackState();
        if (lastReportedPlayWhenReady != playWhenReady || lastReportedPlaybackState != playbackState) {
            lastReportedPlayWhenReady = playWhenReady;
            lastReportedPlaybackState = playbackState;
        }
    }

    public int getPlaybackState() {
        if (rendererBuildingState == RENDERER_BUILDING_STATE_BUILDING) {
            return ExoPlayer.STATE_PREPARING;
        }
        int playerState = exoPlayer.getPlaybackState();
        if (rendererBuildingState == RENDERER_BUILDING_STATE_BUILT && playerState == ExoPlayer.STATE_IDLE) {
            // This is an edge case where the renderers are built, but are still being passed to the
            // player's playback thread.
            return ExoPlayer.STATE_PREPARING;
        }
        return playerState;
    }


    @Override
    public void onPlayWhenReadyCommitted() {
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.d(TAG, "onPlayerError, rendererBuildingState : " + rendererBuildingState + ", cause : " + error.getMessage());
        rendererBuildingState = RENDERER_BUILDING_STATE_IDLE;
    }

    public Uri getVideoUri(){
        return Uri.parse("/android_asset/sample1.mp4");
    }

}

