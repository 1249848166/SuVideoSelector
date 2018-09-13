package su.com.suvideoselector;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CheckGroup {

    GroupListener groupListener;

    int maxNum=5;

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public void setGroupListener(GroupListener groupListener) {
        this.groupListener = groupListener;
    }

    List<String> paths=new ArrayList<>();

    static CheckGroup instance;

    public static CheckGroup getInstance(){
        if(instance==null){
            synchronized (CheckGroup.class){
                if(instance==null){
                    instance=new CheckGroup();
                }
            }
        }
        return instance;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void vedioSelect(CheckImageView view,boolean checked){
        view.check(checked);
    }

    public void checkSelect(String path, Context context){
        try {
            if (paths.contains(path))
                paths.remove(path);
            else {
                if(paths.size()<maxNum)
                    paths.add(path);
                else
                    Toast.makeText(context, "不能超过"+maxNum+"张", Toast.LENGTH_SHORT).show();
            }
            if(groupListener!=null)
                groupListener.selectedNum(paths.size());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
