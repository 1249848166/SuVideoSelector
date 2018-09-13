package su.com.videoselectortest;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;

import java.util.List;

import su.com.suvideoselector.SelectPanelActivity;
import su.com.suvideoselector.SuVideoPlayerView;
import su.com.suvideoselector.VideoPlayerListener;

public class MainActivity extends AppCompatActivity {

    final int CODE_REQUEST=1;
    SuVideoPlayerView videoPlayerView;
    Toolbar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bar=findViewById(R.id.bar);
        setSupportActionBar(bar);

        Intent intent=new Intent(this, SelectPanelActivity.class);
        startActivityForResult(intent,CODE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CODE_REQUEST&&resultCode==RESULT_OK){
            final List<String> paths= data.getStringArrayListExtra("paths");
            for(String path:paths){
                System.out.println(path);
            }
            try {
                videoPlayerView=findViewById(R.id.videoPlayerView);
                //videoPlayerView.playAVideo("http://bmob-cdn-21427.b0.upaiyun.com/2018/09/12/abdf0ce140572e9e80a1434bb0109116.mp4");
                videoPlayerView.setPath(paths.get(0));
                videoPlayerView.setVideoPlayerListener(new VideoPlayerListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        //当播放完成
                    }

                    @Override
                    public void onError(MediaPlayer mp, String msg) {
                        //当播放出错
                    }

                    @Override
                    public void onInfo(MediaPlayer mp, String msg) {
                        //播放中的信息
                    }

                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        //当准备播放
                    }

                    @Override
                    public void onSeekComplete(MediaPlayer mp) {
                        //当进度条追踪结束
                    }

                    @Override
                    public void surfaceCreated(SurfaceHolder holder) {
                        //当画布创建
                    }

                    @Override
                    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height,boolean landscape) {
                        //当画布大小改变
                        //当影片尺寸改变
                        try {
                            if (landscape) {
                                if(bar!=null){
                                    bar.setVisibility(View.GONE);
                                }
                            } else {
                                if(bar!=null){
                                    bar.setVisibility(View.VISIBLE);
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void surfaceDestroyed(SurfaceHolder holder) {
                        //当画布销毁
                    }

                    @Override
                    public void onUpdate(MediaPlayer mp, int percent) {
                        //当更新进度时
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(videoPlayerView!=null)
            videoPlayerView.pauseOrContinue(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(videoPlayerView!=null)
            videoPlayerView.stop();
    }
}
