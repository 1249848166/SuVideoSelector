package su.com.suvideoselector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class SuVideoPlayerView extends RelativeLayout implements SurfaceHolder.Callback, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, SeekBar.OnSeekBarChangeListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener, View.OnClickListener {

    SurfaceView surfaceView;
    SurfaceHolder holder;
    MediaPlayer player;
    int vWidth=MATCH_PARENT, vHeight=300;
    ImageView play, zone, logo;
    View mask;
    TextView text;
    SeekBar seekBar;
    int savedProgress = 0;
    boolean isInited = false;
    String path = "";
    boolean landscape=false;

    VideoPlayerListener videoPlayerListener;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            if (player != null && seekBar != null) {
                seekBar.setProgress((int) (((float) player.getCurrentPosition() / player.getDuration()) * 100));
                text.setText(getTimeStr(player.getCurrentPosition()) + "/" + getTimeStr(player.getDuration()));
                if (videoPlayerListener != null)
                    videoPlayerListener.onUpdate(player, player.getCurrentPosition());
            }
            delay(500);
        }
    };

    void delay(int time) {
        handler.sendEmptyMessageDelayed(0, time);
    }

    ExecutorService executorService;

    public void setPath(String path) {
        this.path = path;
    }

    public void setVideoPlayerListener(VideoPlayerListener videoPlayerListener) {
        this.videoPlayerListener = videoPlayerListener;
    }

    public SuVideoPlayerView(Context context) {
        this(context, null);
    }

    public SuVideoPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuVideoPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        try {
            executorService = Executors.newFixedThreadPool(2);
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0);
                }
            });
            View layout = LayoutInflater.from(context).inflate(R.layout.layout_videoview, null);
            surfaceView = layout.findViewById(R.id.surface);
            play = layout.findViewById(R.id.play);
            text = layout.findViewById(R.id.text);
            seekBar = layout.findViewById(R.id.seekbar);
            seekBar.setMax(100);
            zone = layout.findViewById(R.id.zone);
            logo = layout.findViewById(R.id.logo);
            logo.setVisibility(VISIBLE);
            mask = layout.findViewById(R.id.mask);
            mask.setVisibility(INVISIBLE);
            play.setOnClickListener(this);
            zone.setOnClickListener(this);
            surfaceView.setOnClickListener(this);
            seekBar.setOnSeekBarChangeListener(this);
            holder = surfaceView.getHolder();
            holder.addCallback(this);
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            holder.setKeepScreenOn(true);
            player = new MediaPlayer();
            player.setOnCompletionListener(this);
            player.setOnErrorListener(this);
            player.setOnInfoListener(this);
            player.setOnPreparedListener(this);
            player.setOnSeekCompleteListener(this);
            this.addView(layout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playAVideo(String dataPath) {
        try {
            player.reset();
            path = dataPath;
            player.setDataSource(dataPath);
            int h = player.getVideoHeight();
            int w = player.getVideoWidth();
            if (h == 0 || w == 0) {
                Log.v("play", "无法播放该类型视频");
                if (videoPlayerListener != null)
                    videoPlayerListener.onInfo(player, "无法播放该类型视频");
                return;
            }
            player.setDisplay(holder);
            player.prepareAsync();
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    public void pauseOrContinue(boolean pause) {
        if (player != null) {
            if (pause) {
                savedProgress = (int) (((float) player.getCurrentPosition() / player.getDuration()) * 100);
                player.pause();
                play.setImageResource(R.drawable.play_video);
                logo.setImageResource(R.drawable.play_video);
                logo.setVisibility(VISIBLE);
                mask.setVisibility(VISIBLE);
            } else {
                player.seekTo((int) (((float) savedProgress / 100) * player.getDuration()));
                player.start();
                play.setImageResource(R.drawable.pause_video);
                logo.setImageResource(R.drawable.pause_video);
                logo.setVisibility(INVISIBLE);
                mask.setVisibility(INVISIBLE);
            }
        }
    }

    public void stop() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (videoPlayerListener != null)
            videoPlayerListener.onCompletion(mp);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.v("Play Error:::", "MEDIA_ERROR_SERVER_DIED");
                if (videoPlayerListener != null)
                    videoPlayerListener.onError(mp, "MEDIA_ERROR_SERVER_DIED");
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.v("Play Error:::", "MEDIA_ERROR_UNKNOWN");
                if (videoPlayerListener != null)
                    videoPlayerListener.onError(mp, "MEDIA_ERROR_UNKNOWN");
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                Log.v("onInfo", "MEDIA_INFO_BAD_INTERLEAVING");
                if (videoPlayerListener != null)
                    videoPlayerListener.onInfo(mp, "MEDIA_INFO_BAD_INTERLEAVING");
                break;
            case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                Log.v("onInfo", "MEDIA_INFO_METADATA_UPDATE");
                if (videoPlayerListener != null)
                    videoPlayerListener.onInfo(mp, "MEDIA_INFO_METADATA_UPDATE");
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                Log.v("onInfo", "MEDIA_INFO_VIDEO_TRACK_LAGGING");
                if (videoPlayerListener != null)
                    videoPlayerListener.onInfo(mp, "MEDIA_INFO_VIDEO_TRACK_LAGGING");
                break;
            case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                Log.v("onInfo", "MEDIA_INFO_NOT_SEEKABLE");
                if (videoPlayerListener != null)
                    videoPlayerListener.onInfo(mp, "MEDIA_INFO_NOT_SEEKABLE");
                break;
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        player.start();
        isInited = true;
        logo.setVisibility(INVISIBLE);
        play.setImageResource(R.drawable.pause_video);
        if (videoPlayerListener != null)
            videoPlayerListener.onPrepared(mp);
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        if (videoPlayerListener != null)
            videoPlayerListener.onSeekComplete(mp);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (videoPlayerListener != null)
                videoPlayerListener.surfaceCreated(holder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (videoPlayerListener != null)
            videoPlayerListener.surfaceChanged(holder, format, width, height,landscape);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (videoPlayerListener != null)
            videoPlayerListener.surfaceDestroyed(holder);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar != null && player != null) {
            player.seekTo((int) (((float) seekBar.getProgress() / 100) * player.getDuration()));
            savedProgress = seekBar.getProgress();
            text.setText(getTimeStr(player.getCurrentPosition()) + "/" + getTimeStr(player.getDuration()));
        }
    }

    @Override
    public void onClick(View v) {
        try {
            if (v.getId() == R.id.play || v.getId() == R.id.surface) {
                if (isInited) {
                    pauseOrContinue(player.isPlaying());
                } else {
                    playAVideo(path);
                }
            } else if (v.getId() == R.id.zone) {
                LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) getLayoutParams();
                if(!landscape) {
                    vWidth=getWidth();
                    vHeight=getHeight();
                    ((Activity) v.getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    params.width=MATCH_PARENT;
                    params.height=MATCH_PARENT;
                    setLayoutParams(params);
                    landscape=true;
                }else{
                    ((Activity) v.getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    params.width=vWidth;
                    params.height=vHeight;
                    setLayoutParams(params);
                    landscape=false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String getTimeStr(long time) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        return simpleDateFormat.format(new Date(time));
    }
}
