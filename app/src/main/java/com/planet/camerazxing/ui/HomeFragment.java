package com.planet.camerazxing.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents;
import com.planet.camerazxing.R;
import com.planet.camerazxing.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by planet on 2016/9/9.
 */
public class HomeFragment extends Fragment {

    RecyclerView mRecyclerView;
    ViewPager viewPager;
    View dot0;
    View dot1;
    View dot2;

    /**
     * 设置滑动广告
     */
    private List<ImageView> imageViews; // 滑动的图片集合
    //	private String[] URLs; // 连接地址
    private int[] imageResId = new int[] { R.drawable.home_ad, R.drawable.home_ad,
            R.drawable.home_ad}; // 图片ID
    private List<View> dots; // 图片标题正文的那些点
    private int currentItem = 0; // 当前图片的索引号
    private Timer myTimer = null;

    HomeItemAdapter mAdapter;
    String[] baseList = null;
    List<String> userList = null;
    Activity mActivity = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //14个功能项
        baseList = getResources().getStringArray(R.array.home);
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_home, container, false);
        initView(mRootView);
        return mRootView;
    }


    protected void initView(View rootView) {
        viewPager = (ViewPager)rootView.findViewById(R.id.vp);
        dot0 = rootView.findViewById(R.id.v_dot0);
        dot1 = rootView.findViewById(R.id.v_dot1);
        dot2 = rootView.findViewById(R.id.v_dot2);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.home_recyclerview);
        initADImageView();
        initRecylerView();
    }

    /**
     * 初始化滑动广告
     */
    private void initADImageView() {
        imageViews = new ArrayList<ImageView>();

        // 初始化图片资源
        for(int i = 0; i < imageResId.length; i++) {
//			final String url = URLs[i];
            ImageView imageView = new ImageView(mActivity);
            imageView.setImageResource(imageResId[i]);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

//					Uri u = Uri.parse(url);
//					Intent it = new Intent(Intent.ACTION_VIEW, u);
//					MyViewPagerActivity.this.startActivity(it);

                }

            });
            imageViews.add(imageView);
        }

        dots = new ArrayList<View>();
        dots.add(dot0);
        dots.add(dot1);
        dots.add(dot2);

        viewPager.setAdapter(new MyAdapter());// 设置填充ViewPager页面的适配器
        // 设置一个监听器，当ViewPager中的页面改变时调用
        viewPager.setOnPageChangeListener(new MyPageChangeListener());

    }

    @Override
    public void onStart() {
        super.onStart();
        //切换页面恢复滑动广告
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                currentItem = (currentItem + 1) % imageViews.size();
                handler.sendEmptyMessage(1111); // 通过Handler切换图片
            }
        }, 3000,3000);//3秒切换图片
        super.onStart();
    }

    @Override
    public void onStop() {
        // 当view不可见的时候停止切换
        myTimer.cancel();
        super.onStop();
    }

    /**
     * 当ViewPager中页面的状态发生改变时调用
     *
     * @author Administrator
     *
     */
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {
        private int oldPosition = 0;

        /**
         * This method will be invoked when a new page becomes selected.
         * position: Position index of the new selected page.
         */
        public void onPageSelected(int position) {
            currentItem = position;
            dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
            dots.get(position).setBackgroundResource(R.drawable.dot_focused);
            oldPosition = position;
        }

        public void onPageScrollStateChanged(int arg0) {

        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }
    }

    /**
     * 填充ViewPager页面的适配器
     *
     * @author Administrator
     *
     */
    private class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageResId.length;
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(imageViews.get(arg1));
            return imageViews.get(arg1);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }


    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch(msg.what) {
                case 1111:
                    viewPager.setCurrentItem(currentItem);// 切换当前显示的图片
                    break;
            }
        }
    };

    private void initRecylerView() {
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mActivity, 3, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new HomeItemAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (baseList[0].equals(baseList[position])) {
                    startActivity(new Intent(mActivity, SearchShopActivity.class));
                } else if (baseList[1].equals(baseList[position])) {
                    Intent intent = new Intent();
                    intent.setAction(Intents.Scan.ACTION);
                    intent.putExtra(Intents.Scan.MODE, Intents.Scan.QR_CODE_MODE);
                    intent.setClass(getActivity(), CaptureActivity.class);
                    getActivity().startActivityForResult(intent, 55556);
                } else if(baseList[2].equals(baseList[position])) {
                    startActivity(new Intent(mActivity, InfoUpdateActivity.class));
                } else
                    Toast.makeText(mActivity, "暂未开放", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
    }

    class HomeItemAdapter extends RecyclerView.Adapter<ViewHolder> {

        public HomeItemAdapter() {
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout rootView = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_home_list, parent, false);
            // set the view's size, margins, paddings and layout parameters
            //...
            ViewHolder vh = new ViewHolder(rootView); //构建一个ViewHolder
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            Utils.setIcon(baseList, holder.mImageView, position);
            holder.mTextView.setText(baseList[position]);
            holder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(holder.mTextView, position);
                    }
                }
            });
            //为item添加长按回调
            holder.mLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemLongClick(holder.mTextView, position);
                    }
                    return true;
                }
            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            if(baseList != null)
                return baseList.length;
            else
                return 0;
        }

        private ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ImageView mImageView;
        public LinearLayout mLayout;

        public ViewHolder(View v) {
            super(v);
            mImageView = (ImageView) v.findViewById(R.id.home_item_icon);
            mTextView = (TextView) v.findViewById(R.id.home_item_title);
            mLayout = (LinearLayout) v.findViewById(R.id.home_item_layout);
        }
    }


    public interface ItemClickListener {

        /**
         * Item的普通点击
         */
        public void onItemClick(View view, int position);

        /**
         * Item长按
         */
        public void onItemLongClick(View view, int position);

    }
}
