package com.planet.camerazxing.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents;
import com.planet.camerazxing.R;
import com.planet.camerazxing.adapter.ViewPagerAdapter;
import com.planet.camerazxing.app.MyApplication;
import com.planet.camerazxing.bean.AppInfo;
import com.planet.camerazxing.bean.VersionReqBean;
import com.planet.camerazxing.bean.VersonResBean;
import com.planet.camerazxing.cnst.Const;
import com.planet.camerazxing.util.BottomNavigationViewHelper;
import com.planet.camerazxing.util.OkHttpUtils;
import com.zywx.wbpalmstar.engine.universalex.EUExCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class HomeActivity extends AppCompatActivity {

    private NoScrollViewPager viewPager;
    private MenuItem menuItem;
    private BottomNavigationView navigation;
    ProgressDialog progressDialog;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.navigation_dashboard:
                    viewPager.setCurrentItem(0);
                    Intent intent = new Intent();
                    intent.setAction(Intents.Scan.ACTION);
                    intent.putExtra(Intents.Scan.MODE, Intents.Scan.QR_CODE_MODE);
                    intent.setClass(HomeActivity.this, CaptureActivity.class);
                    startActivityForResult(intent, 55555);
                    break;
                case R.id.navigation_notifications:
                    viewPager.setCurrentItem(0);
                    startActivity(new Intent(HomeActivity.this, SearchShopActivity.class));
                    break;
                case R.id.navigation_setting:
                    viewPager.setCurrentItem(3);
                    break;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        viewPager = (NoScrollViewPager) findViewById(R.id.viewpager);
        viewPager.setNoScroll(true);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                menuItem = navigation.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //禁止ViewPager滑动
//        viewPager.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });

        setupViewPager(viewPager);
        checkUpdate();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new HomeFragment());
        adapter.addFragment(BlankFragment.newInstance("scan"));
        adapter.addFragment(BlankFragment.newInstance("search"));
        adapter.addFragment(BlankFragment.newInstance("Ver." + MyApplication.getAppConfig().verName));
        viewPager.setAdapter(adapter);
    }

    private void checkUpdate() {
        progressDialog = new ProgressDialog(this);
//        if(!BuildConfig.DEBUG)
            progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.app_update_loading));
        progressDialog.show();
        VersionReqBean reqBean = new VersionReqBean();
        reqBean.setOsType(AppInfo.DEVICE_TYPE);
        reqBean.setPackagename(MyApplication.getAppConfig().packageName);
        reqBean.setVersion(MyApplication.getAppConfig().verName);
        OkHttpUtils.post(Const.WEB_update , reqBean, handler);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            if(Const.REQUEST_SERVER_SUCCESS == msg.what) {
                Gson gson = new Gson();
                VersonResBean bean = gson.fromJson(msg.obj.toString(), VersonResBean.class);
                if(bean != null && "1".equals(bean.getUpdateflag())){
                    updateStartApp(bean);
                }
            }
        }
    };

    private void updateStartApp(final VersonResBean bean) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("应用更新");
        builder.setMessage("最新版本:" + bean.getNewversion() + "\n" + bean.getNewversionintro()+ "\n\n" + "请更新");
//        if(!BuildConfig.DEBUG){
            builder.setCancelable(false);
//        }
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Uri mUrl = Uri.parse(bean.getUrl());
//                Intent intent = new Intent(Intent.ACTION_VIEW, mUrl);
//                startActivity(intent);
                progressDialog.setMessage(getString(R.string.app_tips_loading));
                progressDialog.show();
                OkHttpUtils.post(bean.getUrl(), downHandler);
            }
        });
        builder.create().show();
    }

    Handler downHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            if(Const.REQUEST_SERVER_SUCCESS == msg.what) {
                String fileName = msg.obj.toString();
                File f = new File(fileName);
                Log.e("=============", f.getPath());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(f), "application/vnd.android.package-archive");
                startActivity(intent);
            } else
                Toast.makeText(HomeActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == 55555) {

                try {
                    JSONObject jobj = new JSONObject();
                    jobj.put(EUExCallback.F_JK_CODE,
                            data.getStringExtra(EUExCallback.F_JK_CODE));
                    jobj.put(EUExCallback.F_JK_TYPE,
                            data.getStringExtra(EUExCallback.F_JK_TYPE));
//                    String result = jobj.toString();
//                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                    String result = jobj.getString(EUExCallback.F_JK_CODE);
                    if(!TextUtils.isEmpty(result)) {
//                        numberView.setText(result);

                        Log.e("===============", result);
                        startActivity(new Intent(HomeActivity.this, MyWebActivity.class).putExtra("code", result));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } if (requestCode == 55556) {

                    try {
                        JSONObject jobj = new JSONObject();
                        jobj.put(EUExCallback.F_JK_CODE,
                                data.getStringExtra(EUExCallback.F_JK_CODE));
                        jobj.put(EUExCallback.F_JK_TYPE,
                                data.getStringExtra(EUExCallback.F_JK_TYPE));
//                    String result = jobj.toString();
//                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                        String result = jobj.getString(EUExCallback.F_JK_CODE);
                        if(!TextUtils.isEmpty(result)) {
//                        numberView.setText(result);

                            Log.e("===============", result);
                            startActivity(new Intent(this, MyWebActivity.class).putExtra("code", result));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }

        }
    }
}
