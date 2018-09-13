package su.com.suvideoselector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class MyVideoAdapter extends RecyclerView.Adapter<MyVideoAdapter.Holder> implements OnVideoSelectListener {

    Context context;
    List<String> vedios;
    int screenWidth;
    int span;
    int space;
    CheckGroup group;

    public void setGroup(CheckGroup group) {
        this.group = group;
    }

    public MyVideoAdapter(Context context, List<String> vedios, int span, int space) {
        this.context = context;
        this.vedios = vedios;
        DisplayMetrics metrics=context.getResources().getDisplayMetrics();
        screenWidth=metrics.widthPixels;
        this.span=span;
        this.space=space;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView itemView= (ImageView) LayoutInflater.from(context).inflate(R.layout.image_item,parent,false);
        return new Holder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        try {
            holder.itemview.setLayoutParams(new ViewGroup.LayoutParams((screenWidth-(span+1)*space)/span,(screenWidth-(span+1)*space)/span));
            ((CheckImageView)holder.itemview).setGroup(group);
            ((CheckImageView)holder.itemview).checkContains(vedios.get(position));//检查是否包含，解决复用带来显示问题
            holder.itemview.setImageResource(R.drawable.unload120_150);
            MyImageLoader.getInstance().loadImage(vedios.get(position),holder.itemview);
            ((CheckImageView)holder.itemview).setPath(vedios.get(position));
            ((CheckImageView)holder.itemview).setOnVideoSelectListener(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return vedios.size();
    }

    @Override
    public void onVedioSelect(String path){
        if(group!=null)
            group.checkSelect(path,context);
    }

    class Holder extends RecyclerView.ViewHolder{

        ImageView itemview;

        Holder(ImageView itemView) {
            super(itemView);
            this.itemview=itemView;
        }
    }
}
