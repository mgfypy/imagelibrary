/*
 *
 *  * Copyright (C) 2015 Eason.Lai (easonline7@gmail.com)
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.mgfypy.esafeboximagelibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mgfypy.imagelibrary.AndroidImagePicker;
import com.mgfypy.imagelibrary.ImgLoader;
import com.mgfypy.imagelibrary.UilImgLoader;
import com.mgfypy.imagelibrary.Util;
import com.mgfypy.imagelibrary.bean.ImageItem;
import com.mgfypy.imagelibrary.ui.activity.ImagesGridActivity;

import java.util.List;

public class MainActivity extends FragmentActivity implements View.OnClickListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    private final int REQ_IMAGE = 1433;

    private TextView btnSingle;
    private TextView btnMulti;
    private TextView btnCrop;
    private ImageView ivCrop;

    private CheckBox cbShowCamera;

    ImgLoader presenter = new UilImgLoader();
    GridView mGridView;
    SelectAdapter mAdapter;

    private int screenWidth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //btnSingleWithCamera = (TextView) findViewById(R.id.btn_single_with_camera);
        //btnMultiWithCamera = (TextView) findViewById(R.id.btn_multi_with_camera);
        btnSingle = (TextView) findViewById(R.id.btn_single);
        btnMulti = (TextView) findViewById(R.id.btn_multi);
        btnCrop = (TextView) findViewById(R.id.btn_crop);
        ivCrop = (ImageView) findViewById(R.id.iv_crop);
        //ivShow = (ImageView) findViewById(R.id.iv_show);
        cbShowCamera = (CheckBox) findViewById(R.id.cb_show_camera);
        mGridView = (GridView) findViewById(R.id.gridview);
        mAdapter = new SelectAdapter(this);
        mGridView.setAdapter(mAdapter);

        //btnSingleWithCamera.setOnClickListener(this);
        //btnMultiWithCamera.setOnClickListener(this);
        btnSingle.setOnClickListener(this);
        btnMulti.setOnClickListener(this);
        btnCrop.setOnClickListener(this);

        screenWidth = getWindowManager().getDefaultDisplay().getWidth();

        //AndroidImagePicker.getInstance().addOnImageCropCompleteListener(this);

    }

    @Override
    protected void onResume() {
        //AndroidImagePicker.getInstance().setOnPictureTakeCompleteListener(this);//watching Picture taking
        //AndroidImagePicker.getInstance().setOnImagePickCompleteListener(this);
        super.onResume();
    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent();
        int requestCode = REQ_IMAGE;

        boolean isShowCamera = cbShowCamera.isChecked();


        switch (v.getId()){

            case R.id.btn_single:
                /*AndroidImagePicker.getInstance().setSelectMode(AndroidImagePicker.Select_Mode.MODE_SINGLE);
                AndroidImagePicker.getInstance().setShouldShowCamera(false);
                break;*/
                AndroidImagePicker.getInstance().pickSingle(MainActivity.this, isShowCamera, new AndroidImagePicker.OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(List<ImageItem> items) {
                        if(items != null && items.size() > 0){
                            Log.i(TAG,"=====选择了："+items.get(0).path);
                            mAdapter.clear();
                            mAdapter.addAll(items);
                        }
                    }
                });
                return;
            case R.id.btn_multi:
                /*AndroidImagePicker.getInstance().setSelectMode(AndroidImagePicker.Select_Mode.MODE_MULTI);
                AndroidImagePicker.getInstance().setShouldShowCamera(false);
                break;*/
                AndroidImagePicker.getInstance().pickMulti(MainActivity.this, isShowCamera, new AndroidImagePicker.OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(List<ImageItem> items) {
                        if(items != null && items.size() > 0){
                            Log.i(TAG,"=====选择了："+items.get(0).path);
                            mAdapter.clear();
                            mAdapter.addAll(items);
                        }
                    }
                });
                return;

            case R.id.btn_crop:

                AndroidImagePicker.getInstance().pickAndCrop(MainActivity.this, true, 120, new AndroidImagePicker.OnImageCropCompleteListener() {
                    @Override
                    public void onImageCropComplete(Bitmap bmp, float ratio) {
                        Log.i(TAG,"=====onImageCropComplete (get bitmap="+bmp.toString());
                        ivCrop.setVisibility(View.VISIBLE);
                        ivCrop.setImageBitmap(bmp);
                    }
                });

                return;

                /*AndroidImagePicker.getInstance().setSelectMode(AndroidImagePicker.Select_Mode.MODE_SINGLE);
                AndroidImagePicker.getInstance().setShouldShowCamera(true);
                intent.putExtra("isCrop", true);
                AndroidImagePicker.getInstance().cropMode = true;
                requestCode = REQ_IMAGE_CROP;*/
            default:
                break;

        }


        intent.setClass(this,ImagesGridActivity.class);
        startActivityForResult(intent, requestCode);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == Activity.RESULT_OK){
            if (requestCode == REQ_IMAGE) {
                ivCrop.setVisibility(View.GONE);

                List<ImageItem> imageList = AndroidImagePicker.getInstance().getSelectedImages();
                mAdapter.clear();
                mAdapter.addAll(imageList);
            }/*else if(requestCode == REQ_IMAGE_CROP){
                Bitmap bmp = (Bitmap)data.getExtras().get("bitmap");
                Log.i(TAG,"-----"+bmp.getRowBytes());
            }*/
        }

    }

    class SelectAdapter extends ArrayAdapter<ImageItem>{

        //private int mResourceId;
        public SelectAdapter(Context context) {
            super(context, 0);
            //this.mResourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ImageItem item = getItem(position);
            LayoutInflater inflater = getLayoutInflater();
            //View view = inflater.inflate(mResourceId, null);
            int width = (screenWidth - Util.dp2px(MainActivity.this,10*2))/3;

            ImageView imageView = new ImageView(MainActivity.this);
            imageView.setBackgroundColor(Color.GRAY);
            GridView.LayoutParams params = new AbsListView.LayoutParams(width, width);
            imageView.setLayoutParams(params);

            presenter.onPresentImage(imageView,item.path,width);

            return imageView;
        }

    }

    @Override
    protected void onDestroy() {
        //AndroidImagePicker.getInstance().deleteOnPictureTakeCompleteListener(this);
        AndroidImagePicker.getInstance().onDestroy();
        super.onDestroy();
    }

}