package su.com.suvideoselector;

import android.media.MediaPlayer;
import android.view.SurfaceHolder;

public interface VideoPlayerListener {
    void onCompletion(MediaPlayer mp);
    void onError(MediaPlayer mp,String msg);
    void onInfo(MediaPlayer mp,String msg);
    void onPrepared(MediaPlayer mp);
    void onSeekComplete(MediaPlayer mp);
    void surfaceCreated(SurfaceHolder holder);
    void surfaceChanged(SurfaceHolder holder, int format, int width, int height,boolean landscape);
    void surfaceDestroyed(SurfaceHolder holder);
    void onUpdate(MediaPlayer mp, int percent);
}
