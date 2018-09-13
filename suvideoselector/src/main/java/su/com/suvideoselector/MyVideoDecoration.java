package su.com.suvideoselector;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class MyVideoDecoration extends RecyclerView.ItemDecoration {

    int space;
    int span;

    public MyVideoDecoration(int space, int span) {
        this.space = space;
        this.span=span;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if((parent.getChildAdapterPosition(view)+1)%span==0){
            outRect.set(space,space,space,0);
        }else{
            outRect.set(space,space,0,0);
        }
    }
}
