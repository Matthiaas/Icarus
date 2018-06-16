package com.example.photo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery.LayoutParams;
import et.song.vspfv.C0127R;
import et.song.vspfv.SysApp;
import java.io.File;
import java.util.LinkedList;

public class PlayPhotoActivity extends Activity {
    public static int SCREEN_HEIGHT;
    public static int SCREEN_WIDTH;
    private PhotosGalleryAdapter mAdapter;
    private int mCurrentItem;
    LinkedList<File> mDirList = new LinkedList();
    private PhotosGallery mGallery;
    private String mPath;

    class C00831 implements OnItemClickListener {
        C00831() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        }
    }

    class C00842 implements OnItemSelectedListener {
        C00842() {
        }

        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            PlayPhotoActivity.this.mCurrentItem = position;
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    public class PhotosGalleryAdapter extends BaseAdapter {
        private Context mContext;

        PhotosGalleryAdapter(Context context) {
            this.mContext = context;
        }

        public int getCount() {
            return PlayPhotoActivity.this.mDirList.size();
        }

        public Object getItem(int position) {
            return Integer.valueOf(position);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            PhotosImageView view;
            if (convertView == null) {
                view = new PhotosImageView(this.mContext);
                view.setLayoutParams(new LayoutParams(PlayPhotoActivity.SCREEN_WIDTH, PlayPhotoActivity.SCREEN_HEIGHT));
                convertView = view;
            } else {
                view = (PhotosImageView) convertView;
            }
            new Options().inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeFile(((File) PlayPhotoActivity.this.mDirList.get(position)).getAbsolutePath());
            if (bitmap != null) {
                float scale = PlayPhotoActivity.this.getScale(bitmap);
                int bitmapWidth = (int) (((float) bitmap.getWidth()) * scale);
                int bitmapHeight = (int) (((float) bitmap.getHeight()) * scale);
                Bitmap zoomBitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, true);
                view.setImageWidth(bitmapWidth);
                view.setImageHeight(bitmapHeight);
                view.setImageBitmap(zoomBitmap);
            }
            return convertView;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(1);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(1024, 1024);
        getWindow().addFlags(128);
        Display display = getWindowManager().getDefaultDisplay();
        SCREEN_WIDTH = display.getWidth();
        SCREEN_HEIGHT = display.getHeight();
        setContentView(C0127R.layout.activity_play_photo);
        this.mPath = getIntent().getStringExtra("path");
        ScanPhotoFiles();
        CheckCurIndex(this.mPath);
        findViewById();
        setListener();
        init();
    }

    private void ScanPhotoFiles() {
        File dir = new File(SysApp.SAVE_PATH);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (int ii = 0; ii < files.length; ii++) {
                String name = files[ii].getAbsolutePath().toLowerCase();
                if (name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".bmp")) {
                    this.mDirList.add(files[ii]);
                }
            }
        }
    }

    private void CheckCurIndex(String path) {
        for (int ii = 0; ii < this.mDirList.size(); ii++) {
            if (((File) this.mDirList.get(ii)).getAbsolutePath().equals(path)) {
                this.mCurrentItem = ii;
            }
        }
    }

    private void findViewById() {
        this.mGallery = (PhotosGallery) findViewById(C0127R.id.photosdetail_gallery);
    }

    private void setListener() {
        this.mGallery.setOnItemClickListener(new C00831());
        this.mGallery.setOnItemSelectedListener(new C00842());
    }

    private void init() {
        this.mAdapter = new PhotosGalleryAdapter(this);
        this.mGallery.setHorizontalFadingEdgeEnabled(false);
        this.mGallery.setVerticalFadingEdgeEnabled(false);
        this.mGallery.setAdapter(this.mAdapter);
        this.mGallery.setSelection(this.mCurrentItem);
    }

    private float getScale(Bitmap bitmap) {
        return Math.min(((float) SCREEN_WIDTH) / ((float) bitmap.getWidth()), ((float) SCREEN_HEIGHT) / ((float) bitmap.getHeight()));
    }
}
