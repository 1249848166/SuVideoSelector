package su.com.suvideoselector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class CheckImageView extends ImageView {

    Bitmap bitmap;
    Paint paint;
    int filterColor= Color.parseColor("#99000000");
    boolean checked=false;
    OnVideoSelectListener onVideoSelectListener;
    String path;
    CheckGroup group;

    public void setGroup(CheckGroup group) {
        this.group = group;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setOnVideoSelectListener(OnVideoSelectListener onVideoSelectListener) {
        this.onVideoSelectListener = onVideoSelectListener;
    }

    public CheckImageView(Context context) {
        this(context,null);
    }

    public CheckImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CheckImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    void init(Context context){
        bitmap= BitmapFactory.decodeResource(context.getResources(), R.mipmap.check);
        paint=new Paint();
        paint.setAntiAlias(true);
    }

    boolean contain=false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x,y;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x= (int) event.getX()+getLeft();
                y= (int) event.getY()+getTop();
                if(contains(x,y)) {
                    contain = true;
                }
                else
                    contain=false;
                break;
            case MotionEvent.ACTION_UP:
                x= (int) event.getX()+getLeft();
                y= (int) event.getY()+getTop();
                if(contain&&contains(x,y)) {
                    try {
                        if (onVideoSelectListener != null) {
                            group.vedioSelect(this,!checked);
                            onVideoSelectListener.onVedioSelect(path);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
        }
        return true;
    }

    boolean contains(int x,int y){
        if(getLeft()<x&&x<getRight()&&getTop()<y&&y<getBottom()) {
            return true;
        }
        else {
            return false;
        }
    }

    public void checkContains(String path) throws NullPointerException{
        try {
            if (!group.getPaths().contains(path)) {
                this.checked = false;
            }
            else {
                this.checked = true;
            }
            setFilter();
            invalidate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void check(boolean checked) {
        try {
            if(group.getPaths().size()<group.getMaxNum()) {
                this.checked = checked;
                setFilter();
                invalidate();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    void setFilter(){
        if(checked) {
            setColorFilter(filterColor);
        }else{
            setColorFilter(null);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if(checked) {
            canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth() / 2, getHeight() / 2 - bitmap.getHeight() / 2, paint);
        }
    }
}
