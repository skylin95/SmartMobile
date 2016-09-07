package com.pansoft.app.smartmobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.pansoft.app.smartmobile.R;
import com.pansoft.app.smartmobile.constant.SmartHttpConstant;
import com.pansoft.app.smartmobile.http.SessionValidator;
import com.pansoft.app.smartmobile.http.SmartHttpClient;
import com.pansoft.app.smartmobile.loader.BaseImageInfo;
import com.pansoft.app.smartmobile.loader.ImageInfo;
import com.pansoft.app.smartmobile.loader.ImageLoader;
import com.pansoft.app.smartmobile.util.SmartMobileUtil;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;


public class PersonalMsgActivity extends Activity {
    public static final int CUT_PICTURE = 1;
    public static final int SHOW_PICTURE = 2;

    TextView tv_userName;
    TextView tv_sex;
    TextView tv_phone;
    TextView tv_department;
    ImageView iv_back;
    ImageView iv_picture;
    private PopupWindow popupWindow;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_msg);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        iv_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow();
            }
        });
    }

    private void initView() {
        tv_userName = (TextView) findViewById(R.id.tv_userName);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_department = (TextView) findViewById(R.id.tv_department);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_picture = (ImageView) findViewById(R.id.iv_photo);
    }

    private void initData() {
        try {
            String userId = SmartHttpClient.getInstance().getLoginUserId();
            loadUserInfo(userId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
                break;
            default:
                break;
        }
    }

    private void loadUserInfo(String userId, String sqlWhere) throws Exception {
        try {
            //校验session是否失效
            SessionValidator sessionValidator = new SessionValidator(null, this);
            sessionValidator.start();
            sessionValidator.join();

            //session失效，跳转登录页面
            if (!sessionValidator.isValidate()) {
                sessionValidator = null;

                Intent loginIntent = new Intent();
                loginIntent.setClass(this, LoginActivity.class);
                startActivity(loginIntent);

                return;
            }

            sessionValidator = null;

            String appPath = SmartMobileUtil.getAppPath(this);
            String reqUrl = appPath + "mobileGeneralAction.do";

            Map<String, String> reqData = new HashMap<String, String>();
            JSONObject reqObj = new JSONObject();
            reqObj.put("service", "SmartMobileService");
            reqObj.put("method", "queryUserInfo");
            reqObj.put("F_USER_ID", userId);

            reqData.put("jsondata", reqObj.toString());

            OnResponseListener listener = new OnResponseListener<JSONObject>() {
                @Override
                public void onStart(int what) {

                }

                @Override
                public void onSucceed(int what, Response<JSONObject> response) {
                    JSONObject rspData = response.get();

                    try {
                        //成功请求到数据
                        if (rspData != null && "0".equals(rspData.getString("RET_CODE"))) {
                            showUserInfo(rspData);
                        } else {
                            throw new Exception(rspData.getString("RET_MSG"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

                }

                @Override
                public void onFinish(int what) {

                }
            };

            SmartHttpClient.getInstance().sendRequest(40001,
                    SmartHttpConstant.HTTP_REQUEST_JSONOBJECT,
                    SmartHttpConstant.HTTP_METHOD_POST,
                    reqUrl,
                    reqData,
                    null,
                    listener);

        } catch (Exception e) {
            Log.e("LoadUserInfo", "", e);
        }
    }

    private void showUserInfo(JSONObject userInfo) throws Exception {
        String appPath = SmartMobileUtil.getAppPath(this);
        String userid= userInfo.getString("F_USER_ID");
        String userName = userInfo.getString("F_USER_NAME");
        String gender = userInfo.getString("F_GENDER");
        String phone = userInfo.getString("F_TEL_NUM");
        String dept = userInfo.getString("F_BASE_DEPT");
        String deptName = userInfo.getString("F_DEPT_NAME");
        String picUrl = userInfo.getString("F_USER_PIC");

        tv_userName.setText(userName);
        tv_sex.setText(gender);
        tv_phone.setText(phone);
        tv_department.setText(deptName);
//        iv_picture.setBackgroundResource(R.drawable.user_default);

//        String url = "http://10.199.201.253:9902/YXPT/fileTransferDownloadAction.do?F_NEED_APPLY=1&F_USER_ID=9999&F_TYPE=1&F_OBJECT=JT002NB201607120011&F_FILE_NAME=个人信息.png&F_STORE_KEY=g1/2016/09/02/E97A20CD9D54F4A0E8210CFE5A4B1212.png";

        ImageInfo imageInfo = new BaseImageInfo(userid, "", "", appPath + picUrl, ImageInfo.IMAGE_PURPOSE_NORMAL);
        imageInfo.setCircleImage(true);
        Glide.with(this).using(new ImageLoader()).load(imageInfo).into(iv_picture);
    }

    public Bitmap getRemoteImage(String picUrl) throws IOException {
        Bitmap bm = null;
        URL url = null;
        URLConnection conn = null;
        BufferedInputStream bis = null;
        try {
            url = new URL(picUrl);
            conn = url.openConnection();
            conn.connect();

            bis = new BufferedInputStream(conn.getInputStream());
            bm = BitmapFactory.decodeStream(bis);

            return bm;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                bis.close();
            }
        }

        return bm;
    }

    private void loadUserPicture(String userId, String picUrl) throws Exception {
        try {
            //校验session是否失效
            SessionValidator sessionValidator = new SessionValidator(null, this);
            sessionValidator.start();
            sessionValidator.join();

            //ssion失效，跳转登录页面
            if (!sessionValidator.isValidate()) {
                sessionValidator = null;

                Intent loginIntent = new Intent();
                loginIntent.setClass(this, LoginActivity.class);
                startActivity(loginIntent);

                return;
            }

            sessionValidator = null;


            String appPath = SmartMobileUtil.getAppPath(this);
            String reqUrl = appPath + picUrl;

            OnResponseListener listener = new OnResponseListener<Bitmap>() {
                @Override
                public void onStart(int what) {

                }

                @Override
                public void onSucceed(int what, Response<Bitmap> response) {
                    Bitmap image = response.get();

                    try {
                        //成功请求到数据
                        if (image != null) {
                            iv_picture.setImageBitmap(image);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

                }

                @Override
                public void onFinish(int what) {

                }
            };

            SmartHttpClient.getInstance().sendRequest(30001,
                    SmartHttpConstant.HTTP_REQUEST_IMAGE,
                    SmartHttpConstant.HTTP_METHOD_GET,
                    reqUrl,
                    null,
                    null,
                    listener);

        } catch (Exception e) {
            Log.e("LoadTask", "", e);
        }
    }
    private void showPopupWindow() {
        TextView tv_takePhoto;
        TextView tv_pickPhoto;
        Button bt_cancel;
        View view_outside;
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.popupwindow_photo_select, null);
        view_outside = contentView.findViewById(R.id.view_outside);
        tv_takePhoto = (TextView) contentView.findViewById(R.id.tv_takePhoto);
        tv_pickPhoto = (TextView) contentView.findViewById(R.id.tv_pickPhoto);
        bt_cancel = (Button) contentView.findViewById(R.id.bt_cancel);
        view_outside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        tv_takePhoto.setOnClickListener(takePhoto);
        tv_pickPhoto.setOnClickListener(pickPhoto);
        popupWindow = new PopupWindow(contentView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        // 设置好参数之后再show
        popupWindow.showAtLocation(contentView, Gravity.CENTER,0,0);

    }

    private View.OnClickListener takePhoto = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            popupWindow.dismiss();
//创建File对象，用于存储拍照后的图片
            //将此图片存储于SD卡的根目录下
            File outputImage = new File(Environment.getExternalStorageDirectory(),
                    "output_image.jpg");
            try {
                if (outputImage.exists()) {
                    outputImage.delete();
                }
                outputImage.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //将File对象转换成Uri对象
            //Uri表标识着图片的地址
            imageUri = Uri.fromFile(outputImage);
            //隐式调用照相机程序
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            //拍下的照片会被输出到output_image.jpg中去
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            //此处是使用的startActivityForResult（）
            //因此在拍照完后悔有结果返回到onActivityResult（）中去，返回值即为<span style="font-size: 13.3333px; font-family: Arial, Helvetica, sans-serif;">CUT_PICTURE</span>
            //onActivityResult（）中主要是实现图片裁剪
            startActivityForResult(intent, CUT_PICTURE);
        }
    };
    private View.OnClickListener pickPhoto = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            popupWindow.dismiss();
            File outputImage = new File(Environment.getExternalStorageDirectory(),
                    "output_image.jpg");
            imageUri = Uri.fromFile(outputImage);
            try {
                if (outputImage.exists()) {
                    outputImage.delete();
                }
                outputImage.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(Intent.ACTION_PICK,null);
            //此处调用了图片选择器
            //如果直接写intent.setDataAndType("image/*");
            //调用的是系统图库
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, CUT_PICTURE);
        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CUT_PICTURE:
                if (resultCode == RESULT_OK) {
                    //此处启动裁剪程序
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    //此处注释掉的部分是针对android 4.4路径修改的一个测试
                    //有兴趣的读者可以自己调试看看
//              String text=data.getData().toString();
//              Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
                    intent.setDataAndType(data.getData(), "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, SHOW_PICTURE);
                }
                break;
            case SHOW_PICTURE:
                if (resultCode == RESULT_OK) {
                    try {
                        //将output_image.jpg对象解析成Bitmap对象，然后设置到ImageView中显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver()
                                .openInputStream(imageUri));
                        Bitmap output= SmartMobileUtil.toRoundCorner(bitmap, 15.0f);
                        iv_picture.setImageBitmap(output);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }
}
