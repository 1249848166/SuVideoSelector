package su.com.suvideoselector;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyImageLoader {

    static MyImageLoader instance;
    Handler uiHandler;
    LruCache<String,Bitmap> lruCache;
    ExecutorService executorService;

    @SuppressLint("HandlerLeak")
    private MyImageLoader(){
        uiHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                MyImage myImage= (MyImage) msg.obj;
                Bitmap bitmap=myImage.bitmap;
                ImageView imageView=myImage.imageView;
                String path=myImage.path;
                if(imageView.getTag().equals(path))
                    imageView.setImageBitmap(bitmap);
            }
        };
        int maxSize= (int) (Runtime.getRuntime().maxMemory()/8);
        lruCache=new LruCache<String,Bitmap>(maxSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getHeight()*value.getRowBytes();
            }
        };
        executorService= Executors.newFixedThreadPool(10);
    }

    public static MyImageLoader getInstance(){
        if(instance==null){
            synchronized (MyImageLoader.class){
                if(instance==null){
                    instance=new MyImageLoader();
                }
            }
        }
        return instance;
    }

    public void loadImage(final String path, final ImageView imageView){
        final Bitmap[] bitmap = {null};
        imageView.setTag(path);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    bitmap[0] = lruCache.get(path);
                    if (bitmap[0] == null) {
                        bitmap[0] = getFirstFrame(path);
                        lruCache.put(path, bitmap[0]);
                    }
                    Message msg = uiHandler.obtainMessage();
                    msg.obj = new MyImage(path, imageView, bitmap[0]);
                    uiHandler.sendMessage(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    Bitmap getFirstFrame(String videoPath){
        Bitmap bitmap=null;
        try {
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            media.setDataSource(videoPath);
            bitmap = media.getFrameAtTime();
        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    class MyImage{
        String path;
        ImageView imageView;
        Bitmap bitmap;

        MyImage(String path, ImageView imageView,Bitmap bitmap) {
            this.path = path;
            this.imageView = imageView;
            this.bitmap=bitmap;
        }
    }
}
