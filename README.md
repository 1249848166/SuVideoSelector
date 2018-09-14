# SuVideoSelector
## 1.效果图
![效果图](https://github.com/1249848166/SuVideoSelector/blob/master/app/src/main/res/raw/suvideoselector.gif)
## 2.引用
```java
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
```java
dependencies {
	        implementation 'com.github.1249848166:SuVideoSelector:1.3'
	}
```
## 3.使用
```java
//1.声明一些变量
final int CODE_REQUEST=1;
SuVideoPlayerView videoPlayerView;

//2.在需要跳转的地方调用（这个会跳转到视频选择界面）
Intent intent=new Intent(this, SelectPanelActivity.class);
startActivityForResult(intent,CODE_REQUEST);

//3.下面这些是回调，在选择到视频后将他设置到播放控件中
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
                //videoPlayerView.setPath("http://bmob-cdn-21427.b0.upaiyun.com/2018/09/12/abdf0ce140572e9e80a1434bb0109116.mp4");
                videoPlayerView.setPath(paths.get(0));//这里设置本地视频路径（也可以像上面注释的那样设置网络视频路径）
                videoPlayerView.setVideoPlayerListener(new VideoPlayerListener() {//这些回调可以帮助你精细的设置你所要的，一般不设置
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
                        //这里处理屏幕旋转，看情况吧（如果不用全屏播放可以去掉）
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
//暂停时
    @Override
    protected void onPause() {
        super.onPause();
        if(videoPlayerView!=null)
            videoPlayerView.pauseOrContinue(true);
    }
//停止时
    @Override
    protected void onStop() {
        super.onStop();
        if(videoPlayerView!=null)
            videoPlayerView.stop();
    }
```
## 4.具体使用请参照项目（我是越来越懒惰了。。。毕竟同样的内容不想重复写太多。。。）
