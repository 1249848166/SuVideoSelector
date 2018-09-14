package su.com.suvideoselector;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectVideoPanelActivity extends AppCompatActivity implements View.OnClickListener,FolderListener{

    View parent;
    List<String> paths;
    List<MyFolder> folders;
    MyFolder selectedFolder;
    PopupWindow popupWindow;
    int screenWidth=0,screenheight=0;
    Toolbar toolbar;
    TextView title;
    Button selectedNum;
    View bottombar;
    TextView directory;
    TextView num;
    RecyclerView folderRecycler;
    MyFolderAdapter folderAdapter;
    RecyclerView vedioRecycler;
    MyVideoAdapter vedioAdapter;
    int span=3;
    int space=1;
    final int MSG_UPDATE_FOLDER =1;
    final int MSG_UPDATE_VEDIO =2;
    CheckGroup checkGroup;
    int maxNum=5;

    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_UPDATE_FOLDER:
                    try {
                        folderAdapter.notifyDataSetChanged();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    break;
                case MSG_UPDATE_VEDIO:
                    try {
                        directory.setText(selectedFolder.getName());
                        num.setText(selectedFolder.getNum()+"张");
                        vedioAdapter.notifyDataSetChanged();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            parent=LayoutInflater.from(this).inflate(R.layout.activity_select_video_panel,null);
            setContentView(parent);

            initScreen();
            initViews();
            initPopupWindow();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        initFolders();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void initScreen(){
        try {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            screenWidth = metrics.widthPixels;
            screenheight = metrics.heightPixels;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void initViews(){
        try {
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            bottombar = findViewById(R.id.bottombar);
            bottombar.setOnClickListener(this);
            title = findViewById(R.id.title);
            selectedNum = findViewById(R.id.selectedNum);
            selectedNum.setOnClickListener(this);
            directory = findViewById(R.id.directory);
            num = findViewById(R.id.num);

            vedioRecycler = findViewById(R.id.recycler);
            paths = new ArrayList<>();
            vedioAdapter = new MyVideoAdapter(this, paths, span, space);
            checkGroup = CheckGroup.getInstance();
            checkGroup.setGroupListener(new GroupListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void selectedNum(int num) {
                    try {
                        selectedNum.setText(num + "/" + maxNum);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            vedioAdapter.setGroup(checkGroup);
            RecyclerView.LayoutManager manager = new GridLayoutManager(this, span);
            vedioRecycler.setLayoutManager(manager);
            MyVideoDecoration decoration = new MyVideoDecoration(space, span);
            vedioRecycler.addItemDecoration(decoration);
            vedioRecycler.setAdapter(vedioAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void initFolders(){
        try {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Toast.makeText(this, "没有内存卡", Toast.LENGTH_SHORT).show();
                return;
            }
            Set<String> loopSet = new HashSet<>();
            Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            ContentResolver mContentResolver = this.getContentResolver();
            String[] projVideo = {MediaStore.Video.Thumbnails._ID
                    , MediaStore.Video.Thumbnails.DATA
                    , MediaStore.Video.Media.DURATION
                    , MediaStore.Video.Media.SIZE
                    , MediaStore.Video.Media.DISPLAY_NAME
                    , MediaStore.Video.Media.DATE_MODIFIED};
            Cursor mCursor = mContentResolver.query(videoUri, projVideo,
                    MediaStore.Video.Media.MIME_TYPE + " in(?, ?, ?, ?)",
                    new String[]{"video/mp4", "video/3gp", "video/avi", "video/rmvb"},
                    MediaStore.Video.Media.DATE_MODIFIED + " desc");
            if (mCursor != null) {
                folders.clear();
                while (mCursor.moveToNext()) {
                    String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    File directory = new File(path).getParentFile();
                    if (directory == null) continue;
                    if (directory.list() == null||directory.list().length==0) continue;
                    if (!loopSet.contains(directory.getAbsolutePath())) {
                        loopSet.add(directory.getAbsolutePath());
                        MyFolder folder = new MyFolder();
                        folder.setFirstVediopath(path);
                        folder.setPath(directory.getAbsolutePath());
                        folder.setName(directory.getAbsolutePath().substring(directory.getAbsolutePath().lastIndexOf("/") + 1,
                                directory.getAbsolutePath().length()));
                        int num = directory.list(new FilenameFilter() {
                            @Override
                            public boolean accept(File dir, String name) {
                                if (name.endsWith(".mp4") || name.endsWith(".mov") || name.endsWith(".rmvb") || name.endsWith(".avi"))
                                    return true;
                                return false;
                            }
                        }).length;
                        folder.setNum(num);
                        folders.add(folder);
                    }
                }
                mCursor.close();
                Message msg = handler.obtainMessage();
                msg.what = MSG_UPDATE_FOLDER;
                handler.sendMessage(msg);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void initPopupWindow(){
        try {
            View content = LayoutInflater.from(this).inflate(R.layout.layout_list, null);
            folderRecycler = content.findViewById(R.id.list);
            folders = new ArrayList<>();
            folderAdapter = new MyFolderAdapter(this, folders);
            MyFolderDecoration decoration = new MyFolderDecoration(5);
            folderRecycler.addItemDecoration(decoration);
            RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            folderRecycler.setLayoutManager(manager);
            folderRecycler.setAdapter(folderAdapter);

            popupWindow = new PopupWindow(content, screenWidth, (int) (screenheight * 0.8));
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popupwindow));
            popupWindow.setAnimationStyle(R.style.PopupWindowAnim);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    try {
                        backgroundAlpha(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void openPopupWindow(View view,int offx,int offy){
        try {
            popupWindow.showAtLocation(parent, Gravity.BOTTOM, offx, offy);
            backgroundAlpha(0.3f);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void backgroundAlpha(float bgAlpha){
        try {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = bgAlpha;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        try {
            int id = v.getId();
            if (id == R.id.bottombar) {
                try {
                    openPopupWindow(parent, 0, 0);
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else if (id == R.id.selectedNum) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                AlertDialog dialog = builder.create();
                dialog.setMessage("是否确定选择？");
                dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        try {
                            if(checkGroup.getPaths().size()>0) {
                                Intent intent = new Intent();
                                intent.putStringArrayListExtra("paths", (ArrayList<String>) checkGroup.getPaths());
                                setResult(RESULT_OK, intent);
                                finish();
                            }else{
                                Toast.makeText(SelectVideoPanelActivity.this, "请选择", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                dialog.show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if(popupWindow.isShowing()) {
            popupWindow.dismiss();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onFolderSelect(MyFolder folder) {
        try {
            selectFolder(folder);
            popupWindow.dismiss();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void selectFolder(MyFolder folder){
        try {
            selectedFolder = folder;
            String directory = folder.getPath();
            File parentFile = new File(directory);
            File[] files = parentFile.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.endsWith(".rmvb") || name.endsWith(".avi") || name.endsWith(".mp4") || name.endsWith(".3gp")) {
                        return true;
                    }
                    return false;
                }
            });
            List<String> list = new ArrayList<>();
            for (File f : files) {
                list.add(f.getAbsolutePath());
            }
            paths.clear();
            paths.addAll(list);
            Message msg = handler.obtainMessage();
            msg.what = MSG_UPDATE_VEDIO;
            handler.sendMessage(msg);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
