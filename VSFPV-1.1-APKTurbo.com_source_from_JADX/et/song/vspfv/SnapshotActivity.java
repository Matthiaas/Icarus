package et.song.vspfv;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import com.app.util.ActivtyUtil;
import com.app.util.MyApplication;
import com.example.photo.PlayPhotoActivity;
import com.example.photo.PlayerVideoActivity;
import com.fh.lib.PlayInfo;
import et.song.ui.widgets.ETButton;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressLint({"ResourceAsColor"})
public class SnapshotActivity extends Activity {
    private ImageView btn_back;
    private boolean isPicPage = true;
    public Context mContext;
    private ListView mSnapShotList;
    private FileAdapter mSnapshotAdapter;
    private List<pFileInfo> mSnapshotData = new ArrayList();
    int[] mTabIds = new int[]{C0127R.id.tab1, C0127R.id.tab2};
    int[] mTabImage = new int[]{C0127R.drawable.ic_snapshot_0, C0127R.drawable.ic_video_0};
    int[] mTabImage1 = new int[]{C0127R.drawable.ic_snapshot_1, C0127R.drawable.ic_video_1};
    View[] mTabPages = new View[2];
    int[] mTabTitle = new int[]{C0127R.string.lable_title_snapshot, C0127R.string.lable_title_video};
    private TabHost mTabhostView;
    private FileAdapter mVideoAdapter;
    private List<pFileInfo> mVideoData = new ArrayList();
    private ListView mVideoList;
    private MoreCfg moreCfgObj = new MoreCfg();
    private TextView tvDelete;
    OnClickListener tvDeleteClickListener = new C01301();

    class C01301 implements OnClickListener {

        class C01281 implements DialogInterface.OnClickListener {
            C01281() {
            }

            public void onClick(DialogInterface dialog, int which) {
                try {
                    if (Environment.getExternalStorageState().equals("mounted")) {
                        SnapshotActivity.this.deleteData();
                        if (SnapshotActivity.this.isPicPage) {
                            SnapshotActivity.this.mSnapshotData.clear();
                            SnapshotActivity.this.mSnapshotAdapter.notifyDataSetChanged();
                        } else {
                            SnapshotActivity.this.mVideoData.clear();
                            SnapshotActivity.this.mVideoAdapter.notifyDataSetChanged();
                        }
                        SnapshotActivity.this.tvDelete.setVisibility(8);
                    }
                } catch (Exception e) {
                    ActivtyUtil.openToast(SnapshotActivity.this.mContext, e.getMessage());
                }
            }
        }

        class C01292 implements DialogInterface.OnClickListener {
            C01292() {
            }

            public void onClick(DialogInterface dialog, int which) {
            }
        }

        C01301() {
        }

        public void onClick(View arg0) {
            new Builder(SnapshotActivity.this.mContext).setTitle(SnapshotActivity.this.getString(C0127R.string.str_deleteList)).setMessage(SnapshotActivity.this.getString(C0127R.string.str_deleteAllRecordSure)).setPositiveButton(SnapshotActivity.this.getString(C0127R.string.id_sure), new C01281()).setNegativeButton(SnapshotActivity.this.getString(C0127R.string.id_cancel), new C01292()).show();
        }
    }

    class C01312 implements OnClickListener {
        C01312() {
        }

        public void onClick(View v) {
            if (PlayInfo.udpDevType == 7) {
                SnapshotActivity.this.startActivity(new Intent(SnapshotActivity.this, RTSPActivity.class));
                SnapshotActivity.this.finish();
                return;
            }
            if (PlayInfo.udpDevType == 4 || PlayInfo.udpDevType == 5) {
                PlayInfo.playType = 4;
            } else {
                PlayInfo.playType = 1;
                SysApp.getMe().StartActive(true);
            }
            if (PlayInfo.decodeType == 2) {
                SnapshotActivity.this.startActivity(new Intent(SnapshotActivity.this, VideoPlayBySDL.class));
            } else {
                SnapshotActivity.this.startActivity(new Intent(SnapshotActivity.this, VideoPlayByOpengl.class));
            }
            SnapshotActivity.this.finish();
        }
    }

    class C01323 implements OnTabChangeListener {
        C01323() {
        }

        public void onTabChanged(String tabId) {
            int curIndex = SnapshotActivity.this.mTabhostView.getCurrentTab();
            switch (curIndex) {
                case 0:
                    SnapshotActivity.this.isPicPage = true;
                    break;
                case 1:
                    SnapshotActivity.this.isPicPage = false;
                    break;
            }
            for (int i = 0; i < SnapshotActivity.this.mTabPages.length; i++) {
                if (i == curIndex) {
                    ((ImageView) SnapshotActivity.this.mTabPages[i].findViewById(C0127R.id.TabBntIconimageView)).setImageResource(SnapshotActivity.this.mTabImage1[i]);
                    SnapshotActivity.this.mTabPages[i].setBackgroundResource(C0127R.color.list_select_background);
                } else {
                    ((ImageView) SnapshotActivity.this.mTabPages[i].findViewById(C0127R.id.TabBntIconimageView)).setImageResource(SnapshotActivity.this.mTabImage[i]);
                    SnapshotActivity.this.mTabPages[i].setBackgroundResource(C0127R.color.list_noselect_background);
                }
            }
        }
    }

    public class FileAdapter extends BaseAdapter {
        private List<pFileInfo> mFileData = new ArrayList();
        private LayoutInflater mInflater;
        private int mTSx = 0;
        private int mTSy = 0;

        class ViewHolder {
            public TextView date;
            public ETButton delIcon;
            public ImageView icon;
            public RelativeLayout iconLayout;
            public TextView size;
            public TextView title;

            public ViewHolder(View view) {
                this.title = (TextView) view.findViewById(C0127R.id.textView1);
                this.date = (TextView) view.findViewById(C0127R.id.textView2);
                this.size = (TextView) view.findViewById(C0127R.id.textView3);
                this.icon = (ImageView) view.findViewById(C0127R.id.imageView1);
                this.iconLayout = (RelativeLayout) view.findViewById(C0127R.id.IconsLayout);
                this.delIcon = (ETButton) view.findViewById(C0127R.id.DeleteeTButton);
                view.setTag(this);
            }
        }

        public FileAdapter(Context context, List<pFileInfo> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mFileData = data;
        }

        public int getCount() {
            return this.mFileData.size();
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public pFileInfo getItem(int index) {
            if (index < this.mFileData.size()) {
                return (pFileInfo) this.mFileData.get(index);
            }
            return null;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(SnapshotActivity.this.getApplicationContext(), C0127R.layout.list_item_snapshotfile, null);
                ViewHolder viewHolder = new ViewHolder(convertView);
            }
            final ViewHolder holder = (ViewHolder) convertView.getTag();
            final pFileInfo item = getItem(position);
            holder.title.setText(item.Name);
            holder.date.setText(item.DTime);
            holder.size.setText(item.getSize());
            if (item.Image == null) {
                holder.icon.setImageDrawable(SnapshotActivity.this.getResources().getDrawable(C0127R.drawable.ic_new_file));
            } else {
                holder.icon.setImageDrawable(item.Image);
            }
            holder.iconLayout.setVisibility(8);
            convertView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    SnapshotActivity.this.ListItemClick(item.Path);
                }
            });
            holder.delIcon.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    SnapshotActivity.this.DeleteFile(item);
                }
            });
            convertView.setOnTouchListener(new OnTouchListener() {
                boolean isMoveRvent = false;

                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case 0:
                            FileAdapter.this.mTSx = (int) event.getX();
                            FileAdapter.this.mTSy = (int) event.getY();
                            break;
                        case 1:
                            if (this.isMoveRvent) {
                                this.isMoveRvent = false;
                                return true;
                            } else if (holder.iconLayout.getVisibility() == 0) {
                                return true;
                            }
                            break;
                        case 2:
                            int curx = (int) event.getX();
                            if (Math.abs(((int) event.getY()) - FileAdapter.this.mTSy) < SnapshotActivity.this.dp2px(30)) {
                                if (curx > FileAdapter.this.mTSx + SnapshotActivity.this.dp2px(20)) {
                                    this.isMoveRvent = true;
                                    holder.iconLayout.setVisibility(8);
                                    return true;
                                } else if (curx < FileAdapter.this.mTSx - SnapshotActivity.this.dp2px(20)) {
                                    this.isMoveRvent = true;
                                    holder.iconLayout.setVisibility(0);
                                    return true;
                                }
                            }
                            break;
                        case 3:
                            break;
                    }
                    FileAdapter.this.mTSx = 0;
                    FileAdapter.this.mTSy = 0;
                    this.isMoveRvent = false;
                    return false;
                }
            });
            return convertView;
        }
    }

    class pFileInfo {
        public static final int SIZETYPE_B = 1;
        public static final int SIZETYPE_GB = 4;
        public static final int SIZETYPE_KB = 2;
        public static final int SIZETYPE_MB = 3;
        public String DTime = "";
        public Drawable Image = null;
        public String Name = "";
        public String Path = "";
        public long Size = 0;
        public int Type = 0;

        public pFileInfo(File file, int type) {
            this.Path = file.getAbsolutePath();
            this.Name = file.getName();
            this.DTime = new Date(file.lastModified()).toLocaleString();
            this.Size = file.length();
            this.Type = type;
        }

        public String getSize() {
            return FormetFileSize(this.Size);
        }

        public String FormetFileSize(long fileS) {
            DecimalFormat df = new DecimalFormat("#.00");
            String fileSizeString = "";
            String wrongSize = "0B";
            if (fileS == 0) {
                return wrongSize;
            }
            if (fileS < 1024) {
                fileSizeString = df.format((double) fileS) + "B";
            } else if (fileS < 1048576) {
                fileSizeString = df.format(((double) fileS) / 1024.0d) + "KB";
            } else if (fileS < 1073741824) {
                fileSizeString = df.format(((double) fileS) / 1048576.0d) + "MB";
            } else {
                fileSizeString = df.format(((double) fileS) / 1.073741824E9d) + "GB";
            }
            return fileSizeString;
        }

        public double FormetFileSize(long fileS, int sizeType) {
            DecimalFormat df = new DecimalFormat("#.00");
            switch (sizeType) {
                case 1:
                    return Double.valueOf(df.format((double) fileS)).doubleValue();
                case 2:
                    return Double.valueOf(df.format(((double) fileS) / 1024.0d)).doubleValue();
                case 3:
                    return Double.valueOf(df.format(((double) fileS) / 1048576.0d)).doubleValue();
                case 4:
                    return Double.valueOf(df.format(((double) fileS) / 1.073741824E9d)).doubleValue();
                default:
                    return 0.0d;
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(1);
        super.onCreate(savedInstanceState);
        this.mContext = this;
        MyApplication.getInstance().addActivity(this);
        getWindow().setFlags(1024, 1024);
        getWindow().addFlags(128);
        setContentView(C0127R.layout.activity_snapshot);
        this.tvDelete = (TextView) findViewById(C0127R.id.tvDelete);
        this.tvDelete.setOnClickListener(this.tvDeleteClickListener);
        this.tvDelete.setVisibility(8);
        this.btn_back = (ImageView) findViewById(C0127R.id.btn_back);
        this.btn_back.setOnClickListener(new C01312());
        this.mTabhostView = (TabHost) findViewById(C0127R.id.tabhost);
        this.mTabhostView.setup();
        for (int i = 0; i < this.mTabPages.length; i++) {
            this.mTabPages[i] = LayoutInflater.from(this).inflate(C0127R.layout.view_tabhost_bnt, null);
            ((TextView) this.mTabPages[i].findViewById(C0127R.id.TabBntTitletextView)).setText(this.mTabTitle[i]);
            if (i == 0) {
                ((ImageView) this.mTabPages[i].findViewById(C0127R.id.TabBntIconimageView)).setImageResource(this.mTabImage1[i]);
                this.mTabPages[i].setBackgroundResource(C0127R.color.list_select_background);
            } else {
                ((ImageView) this.mTabPages[i].findViewById(C0127R.id.TabBntIconimageView)).setImageResource(this.mTabImage[i]);
            }
            this.mTabhostView.addTab(this.mTabhostView.newTabSpec(getResources().getString(this.mTabTitle[i])).setIndicator(this.mTabPages[i]).setContent(this.mTabIds[i]));
        }
        this.mTabhostView.setOnTabChangedListener(new C01323());
        this.mSnapShotList = (ListView) findViewById(C0127R.id.listView1);
        this.mVideoList = (ListView) findViewById(C0127R.id.listView2);
        RefreshListData();
        this.mSnapshotAdapter = new FileAdapter(this, this.mSnapshotData);
        this.mVideoAdapter = new FileAdapter(this, this.mVideoData);
        this.mSnapShotList.setAdapter(this.mSnapshotAdapter);
        this.mVideoList.setAdapter(this.mVideoAdapter);
    }

    private void deleteData() {
        File[] subFile = new File(SysApp.SAVE_PATH).listFiles();
        for (int ii = 0; ii < subFile.length; ii++) {
            if (!subFile[ii].isDirectory()) {
                String filename = subFile[ii].getName().trim().toLowerCase();
                if (filename.endsWith(".mp4") || filename.endsWith(".avi") || filename.endsWith(".h264")) {
                    if (!this.isPicPage) {
                        subFile[ii].delete();
                    }
                } else if ((filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".bmp")) && this.isPicPage) {
                    subFile[ii].delete();
                }
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            if (PlayInfo.udpDevType == 7) {
                startActivity(new Intent(this, RTSPActivity.class));
                finish();
                return true;
            }
            if (PlayInfo.udpDevType == 4 || PlayInfo.udpDevType == 5) {
                PlayInfo.playType = 4;
            } else {
                PlayInfo.playType = 1;
                SysApp.getMe().StartActive(true);
            }
            if (PlayInfo.decodeType == 2 || PlayInfo.decodeType == 3) {
                startActivity(new Intent(this, VideoPlayBySDL.class));
            } else {
                startActivity(new Intent(this, VideoPlayByOpengl.class));
            }
            finish();
            return true;
        } else if (keyCode != 82) {
            return super.onKeyDown(keyCode, event);
        } else {
            if (8 == this.tvDelete.getVisibility()) {
                this.tvDelete.setVisibility(0);
                return true;
            }
            this.tvDelete.setVisibility(8);
            return true;
        }
    }

    private void RefreshListData() {
        this.mSnapshotData.clear();
        this.mVideoData.clear();
        File[] subFile = new File(SysApp.SAVE_PATH).listFiles();
        for (int ii = 0; ii < subFile.length; ii++) {
            if (!subFile[ii].isDirectory()) {
                String filename = subFile[ii].getName().trim().toLowerCase();
                if (filename.endsWith(".mp4") || filename.endsWith(".avi") || filename.endsWith(".h264")) {
                    this.mVideoData.add(new pFileInfo(subFile[ii], 2));
                } else if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".bmp")) {
                    this.mSnapshotData.add(new pFileInfo(subFile[ii], 1));
                }
            }
        }
    }

    private void ListItemClick(String file) {
        String filename = file.toLowerCase();
        Intent setIntent = null;
        if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".bmp")) {
            setIntent = new Intent(this, PlayPhotoActivity.class);
        } else if (filename.endsWith(".mp4") || filename.endsWith(".avi")) {
            setIntent = new Intent(this, PlayerVideoActivity.class);
        } else if (filename.endsWith(".h264")) {
            PlayInfo.playType = 3;
            PlayInfo.pbRecFilePath = file;
            Log.e("xx", "path = " + PlayInfo.pbRecFilePath);
            if (PlayInfo.decodeType == 2 || PlayInfo.decodeType == 3) {
                startActivity(new Intent(this, VideoPlayBySDL.class));
            } else {
                startActivity(new Intent(this, VideoPlayByOpengl.class));
            }
        }
        if (setIntent != null) {
            setIntent.putExtra("path", file);
            startActivity(setIntent);
        }
    }

    private void DeleteFile(pFileInfo file) {
        File f = new File(file.Path);
        if (f.exists()) {
            f.delete();
            if (file.Type == 1) {
                this.mSnapshotData.remove(file);
                this.mSnapshotAdapter.notifyDataSetChanged();
            } else if (file.Type == 2) {
                this.mVideoData.remove(file);
                this.mVideoAdapter.notifyDataSetChanged();
            }
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(1, (float) dp, getResources().getDisplayMetrics());
    }
}
