package et.song.vspfv;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.app.util.MyApplication;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class MoreCfg extends Activity {
    private static final String[] DecodeType = new String[]{"软解码", "硬解码"};
    static String FILE = "transInfo";
    private static final String[] FormatArray = new String[]{"PCM", "G711-ALAW", "G711-ULAW"};
    private static final String[] FrameCacheNum = new String[]{"0", "10", "20", "30", "40", "50"};
    private static final String[] MDArray = new String[]{"关闭", "开启"};
    private static final String[] ModeArray = new String[]{"TCP", "UDP"};
    private static final String[] SampleRateArray = new String[]{"8000", "16000"};
    private static final String TAG = "MoreCfgActivity";
    ArrayList<HashMap<String, Object>> listItem = new ArrayList();
    private OnItemClickListener listItemListener = new C01261();
    private int mAudioFormat = 0;
    private int mDecodeType = 0;
    private int mFrameCacheNum = 0;
    private int mMDStatus = 0;
    private int mSampleRate = 0;
    private int mTransMode = 0;
    private ListView myList = null;
    private SimpleAdapter simpleAdapter;
    private SharedPreferences sp = null;

    class C01261 implements OnItemClickListener {
        C01261() {
        }

        public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
            final HashMap<String, String> map = (HashMap) MoreCfg.this.myList.getItemAtPosition(arg2);
            switch (arg2) {
                case 0:
                    new Builder(MoreCfg.this).setTitle("请选择").setIcon(17301659).setSingleChoiceItems(MoreCfg.ModeArray, MoreCfg.this.mTransMode, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MoreCfg.this.mTransMode = which;
                            MoreCfg.this.rememberTheData();
                            map.put("ItemChose", MoreCfg.ModeArray[MoreCfg.this.mTransMode]);
                            MoreCfg.this.simpleAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }).setNegativeButton("取消", null).show();
                    return;
                case 1:
                    new Builder(MoreCfg.this).setTitle("请选择").setIcon(17301659).setSingleChoiceItems(MoreCfg.MDArray, MoreCfg.this.mMDStatus, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MoreCfg.this.mMDStatus = which;
                            MoreCfg.this.rememberTheData();
                            map.put("ItemChose", MoreCfg.MDArray[MoreCfg.this.mMDStatus]);
                            MoreCfg.this.simpleAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }).setNegativeButton("取消", null).show();
                    return;
                case 2:
                    new Builder(MoreCfg.this).setTitle("请选择").setIcon(17301659).setSingleChoiceItems(MoreCfg.SampleRateArray, MoreCfg.this.mSampleRate, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MoreCfg.this.mSampleRate = which;
                            MoreCfg.this.rememberTheData();
                            map.put("ItemChose", MoreCfg.SampleRateArray[MoreCfg.this.mSampleRate]);
                            MoreCfg.this.simpleAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }).setNegativeButton("取消", null).show();
                    return;
                case 3:
                    new Builder(MoreCfg.this).setTitle("请选择").setIcon(17301659).setSingleChoiceItems(MoreCfg.FormatArray, MoreCfg.this.mAudioFormat, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MoreCfg.this.mAudioFormat = which;
                            MoreCfg.this.rememberTheData();
                            map.put("ItemChose", MoreCfg.FormatArray[MoreCfg.this.mAudioFormat]);
                            MoreCfg.this.simpleAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }).setNegativeButton("取消", null).show();
                    return;
                case 4:
                    new Builder(MoreCfg.this).setTitle("请选择").setIcon(17301659).setSingleChoiceItems(MoreCfg.FrameCacheNum, MoreCfg.this.mFrameCacheNum, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MoreCfg.this.mFrameCacheNum = which;
                            MoreCfg.this.rememberTheData();
                            map.put("ItemChose", MoreCfg.FrameCacheNum[MoreCfg.this.mFrameCacheNum]);
                            MoreCfg.this.simpleAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }).setNegativeButton("取消", null).show();
                    return;
                case 5:
                    new Builder(MoreCfg.this).setTitle("请选择").setIcon(17301659).setSingleChoiceItems(MoreCfg.DecodeType, MoreCfg.this.mDecodeType, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MoreCfg.this.mDecodeType = which;
                            MoreCfg.this.rememberTheData();
                            map.put("ItemChose", MoreCfg.DecodeType[MoreCfg.this.mDecodeType]);
                            MoreCfg.this.simpleAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }).setNegativeButton("取消", null).show();
                    return;
                default:
                    return;
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setTitle(getString(C0127R.string.title_moreCfg));
        setContentView(C0127R.layout.more_cfg_list);
        MyApplication.getInstance().addActivity(this);
        initView();
    }

    public void initView() {
        this.sp = getSharedPreferences(FILE, 0);
        if (this.sp != null) {
            if ("" == this.sp.getString("mFrameCacheNum", "")) {
                this.mFrameCacheNum = 0;
            } else {
                this.mFrameCacheNum = Integer.parseInt(this.sp.getString("mFrameCacheNum", ""));
            }
            if ("" == this.sp.getString("transMode", "")) {
                this.mTransMode = 0;
            } else {
                this.mTransMode = Integer.parseInt(this.sp.getString("transMode", ""));
            }
            if ("" == this.sp.getString("MDStatus", "")) {
                this.mMDStatus = 0;
            } else {
                this.mMDStatus = Integer.parseInt(this.sp.getString("MDStatus", ""));
            }
            if ("" == this.sp.getString("mSampleRate", "")) {
                this.mSampleRate = 1;
            } else {
                this.mSampleRate = Integer.parseInt(this.sp.getString("mSampleRate", ""));
            }
            if ("" == this.sp.getString("mAudioFormat", "")) {
                this.mAudioFormat = 0;
            } else {
                this.mAudioFormat = Integer.parseInt(this.sp.getString("mAudioFormat", ""));
            }
            if ("" == this.sp.getString("mDecodeType", "")) {
                this.mDecodeType = 0;
            } else {
                this.mDecodeType = Integer.parseInt(this.sp.getString("mDecodeType", ""));
            }
        }
        this.myList = (ListView) findViewById(C0127R.id.listView1);
        this.myList.setOnItemClickListener(this.listItemListener);
        addListItem();
    }

    public void addListItem() {
        HashMap<String, Object> map = new HashMap();
        map.put("ItemTitle", "预览传输模式");
        map.put("ItemChose", ModeArray[this.mTransMode]);
        map.put("ItemImage", Integer.valueOf(C0127R.drawable.arrow_down));
        this.listItem.add(map);
        map = new HashMap();
        map.put("ItemTitle", "移动侦测提醒");
        map.put("ItemChose", MDArray[this.mMDStatus]);
        map.put("ItemImage", Integer.valueOf(C0127R.drawable.arrow_down));
        this.listItem.add(map);
        map = new HashMap();
        map.put("ItemTitle", "语音对讲采样率");
        map.put("ItemChose", SampleRateArray[this.mSampleRate]);
        map.put("ItemImage", Integer.valueOf(C0127R.drawable.arrow_down));
        this.listItem.add(map);
        map = new HashMap();
        map.put("ItemTitle", "语音对讲编码格式");
        map.put("ItemChose", FormatArray[this.mAudioFormat]);
        map.put("ItemImage", Integer.valueOf(C0127R.drawable.arrow_down));
        this.listItem.add(map);
        map = new HashMap();
        map.put("ItemTitle", "视频缓存帧数");
        map.put("ItemChose", FrameCacheNum[this.mFrameCacheNum]);
        map.put("ItemImage", Integer.valueOf(C0127R.drawable.arrow_down));
        this.listItem.add(map);
        map = new HashMap();
        map.put("ItemTitle", "解码类型");
        map.put("ItemChose", DecodeType[this.mDecodeType]);
        map.put("ItemImage", Integer.valueOf(C0127R.drawable.arrow_down));
        this.listItem.add(map);
        this.simpleAdapter = new SimpleAdapter(this, this.listItem, C0127R.layout.more_cfg_item, new String[]{"ItemTitle", "ItemChose", "ItemImage"}, new int[]{C0127R.id.tvItemTitle, C0127R.id.textView2, C0127R.id.imageView1});
        this.myList.setAdapter(this.simpleAdapter);
    }

    public void rememberTheData() {
        if (this.sp == null) {
            this.sp = getSharedPreferences(FILE, 0);
        }
        Editor edit = this.sp.edit();
        edit.putString("transMode", String.valueOf(this.mTransMode));
        edit.putString("MDStatus", String.valueOf(this.mMDStatus));
        edit.putString("mSampleRate", String.valueOf(this.mSampleRate));
        edit.putString("mAudioFormat", String.valueOf(this.mAudioFormat));
        edit.putString("mFrameCacheNum", String.valueOf(this.mFrameCacheNum));
        edit.putString("mDecodeType", String.valueOf(this.mDecodeType));
        edit.commit();
    }

    public int getSampleRate(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(FILE, 0);
        if (sp == null) {
            return 1;
        }
        if ("" == sp.getString("mSampleRate", "")) {
            return 1;
        }
        return Integer.parseInt(sp.getString("mSampleRate", ""));
    }

    public int getAudioFormat(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(FILE, 0);
        if (sp == null) {
            return 0;
        }
        if ("" == sp.getString("mAudioFormat", "")) {
            return 0;
        }
        return Integer.parseInt(sp.getString("mAudioFormat", ""));
    }

    public int getTransMode(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(FILE, 0);
        if (sp == null) {
            return 1;
        }
        if ("" == sp.getString("transMode", "")) {
            return 0;
        }
        return Integer.parseInt(sp.getString("transMode", ""));
    }

    public int getFrameCacheNum(Context ctx) {
        int mFrameCacheNum = 0;
        SharedPreferences sp = ctx.getSharedPreferences(FILE, 0);
        if (sp != null) {
            if ("" == sp.getString("mFrameCacheNum", "")) {
                mFrameCacheNum = 0;
            } else {
                mFrameCacheNum = Integer.parseInt(sp.getString("mFrameCacheNum", ""));
            }
        }
        return mFrameCacheNum * 10;
    }

    public int getDecodeType(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(FILE, 0);
        if (sp == null) {
            return 0;
        }
        if ("" == sp.getString("mDecodeType", "")) {
            return 0;
        }
        return Integer.parseInt(sp.getString("mDecodeType", ""));
    }

    public boolean isSupportMediaCodecHardDecoder() {
        boolean isHardcode = false;
        InputStream inFile = null;
        try {
            inFile = new FileInputStream(new File("/system/etc/media_codecs.xml"));
        } catch (Exception e) {
        }
        if (inFile != null) {
            try {
                XmlPullParser xmlPullParser = XmlPullParserFactory.newInstance().newPullParser();
                xmlPullParser.setInput(inFile, "UTF-8");
                for (int eventType = xmlPullParser.getEventType(); eventType != 1; eventType = xmlPullParser.next()) {
                    String tagName = xmlPullParser.getName();
                    switch (eventType) {
                        case 2:
                            if (!"MediaCodec".equals(tagName)) {
                                break;
                            }
                            String componentName = xmlPullParser.getAttributeValue(0);
                            if (componentName.startsWith("OMX.") && !componentName.startsWith("OMX.google.")) {
                                isHardcode = true;
                                break;
                            }
                        default:
                            break;
                    }
                }
            } catch (Exception e2) {
            }
        }
        return isHardcode;
    }
}
