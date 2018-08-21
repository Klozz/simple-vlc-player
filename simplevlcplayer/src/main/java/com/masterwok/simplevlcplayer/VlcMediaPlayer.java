package com.masterwok.simplevlcplayer;

import android.net.Uri;
import android.view.SurfaceView;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.RendererItem;

import java.io.FileDescriptor;

import static org.videolan.libvlc.Media.Slave.Type.Subtitle;
import static org.videolan.libvlc.MediaPlayer.Event.Buffering;
import static org.videolan.libvlc.MediaPlayer.Event.EncounteredError;
import static org.videolan.libvlc.MediaPlayer.Event.EndReached;
import static org.videolan.libvlc.MediaPlayer.Event.Opening;
import static org.videolan.libvlc.MediaPlayer.Event.Paused;
import static org.videolan.libvlc.MediaPlayer.Event.Playing;
import static org.videolan.libvlc.MediaPlayer.Event.PositionChanged;
import static org.videolan.libvlc.MediaPlayer.Event.SeekableChanged;
import static org.videolan.libvlc.MediaPlayer.Event.Stopped;
import static org.videolan.libvlc.MediaPlayer.Event.TimeChanged;


/**
 * This class is an implementation of the media player contract and wraps
 * the VLC media player.
 */
public class VlcMediaPlayer
        implements
        com.masterwok.simplevlcplayer.contracts.VlcMediaPlayer
        , org.videolan.libvlc.MediaPlayer.EventListener
        , IVLCVout.Callback {


    private final org.videolan.libvlc.MediaPlayer player;
    private final LibVLC libVlc;

    private RendererItem selectedRendererItem;

    private Callback callback;

    public VlcMediaPlayer(
            LibVLC libVlc
    ) {
        this.libVlc = libVlc;

        player = new org.videolan.libvlc.MediaPlayer(libVlc);
        player.setEventListener(this);
    }

    @Override
    public void release() {
        player.release();
    }

    @Override
    public void play() {
        player.play();
    }

    @Override
    public void pause() {
        player.pause();
    }

    @Override
    public IVLCVout getVout() {
        return player.getVLCVout();
    }

    @Override
    public void stop() {
        player.stop();
    }

    @Override
    public void setMedia(Uri uri) {
        final Media media = new Media(
                libVlc,
                uri
        );

        player.setMedia(media);
        media.release();
    }

    @Override
    public void setMedia(FileDescriptor fileDescriptor) {
        final Media media = new Media(
                libVlc,
                fileDescriptor
        );

        player.setMedia(media);
        media.release();
    }

    @Override
    public void setSubtitle(Uri uri) {
        if (uri == null) {
            Media.Slave[] slaves = player.getMedia().getSlaves();

            if (slaves != null && slaves.length > 0) {
                callback.onSubtitlesCleared();
            }

            return;
        }

        player.addSlave(
                Subtitle,
                uri,
                true
        );
    }

    @Override
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public long getTime() {
        return player.getTime();
    }

    @Override
    public void setTime(long time) {
        if (!player.isSeekable()) {
            return;
        }

        player.setTime(time);
    }

    @Override
    public long getLength() {
        return player.getLength();
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public void onEvent(org.videolan.libvlc.MediaPlayer.Event event) {
        if (callback == null) {
            return;
        }

        switch (event.type) {
            case Opening:
                callback.onPlayerOpening();
                break;
            case SeekableChanged:
                callback.onPlayerSeekStateChange(event.getSeekable());
                break;
            case Playing:
                callback.onPlayerPlaying();
                break;
            case Paused:
                callback.onPlayerPaused();
                break;
            case Stopped:
                callback.onPlayerStopped();
                break;
            case EndReached:
                callback.onPlayerEndReached();
                break;
            case EncounteredError:
                callback.onPlayerError();
                break;
            case TimeChanged:
                callback.onPlayerTimeChange(event.getTimeChanged());
                break;
            case PositionChanged:
                callback.onPlayerPositionChanged(event.getPositionChanged());
                break;
            case Buffering:
                callback.onBuffering(event.getBuffering());
            default:
                break;
        }
    }

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {
        // Nothing to do..
    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {
        // Nothing to do..
    }

    @Override
    public void attachSurfaces(
            SurfaceView surfaceMedia,
            SurfaceView surfaceSubtitles,
            IVLCVout.OnNewVideoLayoutListener layoutListener
    ) {
        selectedRendererItem = null;

        final IVLCVout vlcOut = getVout();
        vlcOut.setVideoView(surfaceMedia);
        vlcOut.setSubtitlesView(surfaceSubtitles);
        vlcOut.attachViews(layoutListener);
    }

    @Override
    public void detachSurfaces() {
        getVout().detachViews();
    }

    @Override
    public void setRendererItem(RendererItem rendererItem) {
        selectedRendererItem = rendererItem;
        player.setRenderer(rendererItem);
    }

    @Override
    public void setVolume(int volume) {
        player.setVolume(volume);
    }

    @Override
    public RendererItem getSelectedRendererItem() {
        return selectedRendererItem;
    }

    @Override
    public void setAspectRatio(String aspectRatio) {
        player.setAspectRatio(aspectRatio);
    }

    @Override
    public void setScale(float scale) {
        player.setScale(scale);
    }

    @Override
    public Media.VideoTrack getCurrentVideoTrack() {
        return player.getCurrentVideoTrack();
    }

    @Override
    public Media getMedia() {
        return player.getMedia();
    }

}
