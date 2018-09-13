package su.com.suvideoselector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MyFolderAdapter extends RecyclerView.Adapter<MyFolderAdapter.Holder> implements View.OnClickListener{

    Context context;
    List<MyFolder> folders;

    public MyFolderAdapter(Context context, List<MyFolder> folders) {
        this.context = context;
        this.folders = folders;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView=null;
        try {
            itemView = LayoutInflater.from(context).inflate(R.layout.folder_item, parent, false);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new Holder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        try {
            TextView name = holder.itemView.findViewById(R.id.name);
            TextView num = holder.itemView.findViewById(R.id.num);
            ImageView img = holder.itemView.findViewById(R.id.img);
            name.setText(folders.get(position).getName());
            num.setText(folders.get(position).getNum() + "张");
            MyImageLoader.getInstance().loadImage(folders.get(position).getFirstVediopath(),img);
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    @Override
    public void onClick(View v) {
        try {
            int index = (int) v.getTag();
            ((FolderListener) context).onFolderSelect(folders.get(index));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    class Holder extends RecyclerView.ViewHolder{

        View itemview;

        Holder(View itemView) {
            super(itemView);
            this.itemview=itemView;
        }
    }
}
