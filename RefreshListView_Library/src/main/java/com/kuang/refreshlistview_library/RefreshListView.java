package com.kuang.refreshlistview_library;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by KZY on 2016/2/21.
 */
public class RefreshListView extends ListView {

    private ProgressBar  mPb_load_headlistview;
    private ImageView    mIv_arrow_headlistview;
    private TextView     mTv_refresh_headlistview;
    private TextView     mTv_time_item_detailnew;
    private LinearLayout mHeadRootView;
    private float mDownY = -1;
    private float          mMoveY;
    private RelativeLayout mRl_refresh;
    private int            mHeadMeasuredHeight;

    private static final int PULL_STATE    = 0;//下拉
    private static final int RELEASE_STATE = 1;//松开
    private static final int REFRESH_STATE = 2;//刷新

    private int state = 0;
    private View            mLunboView;
    private RotateAnimation mRa_toUp;
    private RotateAnimation mRa_toDown;
    private boolean isTimeShow = true;
    private LinearLayout mFootView;
    private int mFootMeasuredHeight;

    public RefreshListView(Context context) {
        super(context, null);

    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initAnimation();

        initHead();

        initfoot();

        initListener();
    }

    private void initListener() {

        this.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.d("scrollstate1",""+getLastVisiblePosition() + "<>" + getAdapter().getCount());
                // 静止状态,滑动到最后一条数据(foot) 拖出加载更多
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && getLastVisiblePosition() == getAdapter().getCount() - 1) {

                    mFootView.setPadding(0, 0, 0, 0);

                    //设置加载更多的数据界面显示出来
                    setSelection(getAdapter().getCount());

                    if (mOnRefreshDataListener != null) {

                        mOnRefreshDataListener.loadMore();
                    }

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });


    }

    private void initfoot() {

        mFootView = (LinearLayout) View.inflate(getContext(), R.layout.foot_listview, null);
        //此listview添加脚部分
        addFooterView(mFootView);
        // 隐藏加载更多footer
        mFootView.measure(0, 0);
        mFootMeasuredHeight = mFootView.getMeasuredHeight();
        mFootView.setPadding(0, -mFootMeasuredHeight, 0, 0);

    }


    private void initHead() {

        mHeadRootView = (LinearLayout) View.inflate(getContext(), R.layout.head_listview, null);
        //此listview添加头部
        addHeaderView(mHeadRootView);

        mRl_refresh = (RelativeLayout) mHeadRootView.findViewById(R.id.rl_refresh);

        mPb_load_headlistview = (ProgressBar) mHeadRootView.findViewById(R.id.pb_load_headlistview);

        mIv_arrow_headlistview = (ImageView) mHeadRootView.findViewById(R.id.iv_arrow_headlistview);

        mTv_refresh_headlistview = (TextView) mHeadRootView.findViewById(R.id.tv_refresh_headlistview);

        mTv_time_item_detailnew = (TextView) mHeadRootView.findViewById(R.id.tv_time_headlistview);

        //隐藏头部的刷新部分:
        mRl_refresh.measure(0, 0);

        mHeadMeasuredHeight = mRl_refresh.getMeasuredHeight();
        //setpadding可以控制控件的内容显现多少
        mRl_refresh.setPadding(0, -mHeadMeasuredHeight, 0, 0);
    }



    private void initAnimation() {

        mRa_toUp = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRa_toUp.setDuration(500);
        mRa_toUp.setFillAfter(true);

        mRa_toDown = new RotateAnimation(-180, -360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRa_toDown.setDuration(500);
        mRa_toDown.setFillAfter(true);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){

            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:


                if (state==REFRESH_STATE){

                    return true;//屏蔽掉listview的touch事件
                }

                if (mDownY==-1){//没变

                    mDownY = ev.getY();
                }

//                if (!isLunboShow()){
                    //轮播图没完全显示
//                    break;
//                }

                mMoveY = ev.getY();

                float dy = mMoveY - mDownY;

                float hintHight = -mHeadMeasuredHeight+dy;

                if (dy>0 && isTimeShow){
                    isTimeShow = false;
                    mTv_time_item_detailnew.setText(getCurrentTime());
                }
                //从上往下拖动并且listview显示第一个数据(头部),并且轮播图完全显示，还有第一个位置中:轮播图完全显示 则  拖动出下拉刷新的View ,记录状态
                if (dy>0 && getFirstVisiblePosition()==0 && isLunboShow()){
                    //拖动出下拉刷新的View
                    mRl_refresh.setPadding(0,(int)hintHight,0,0);

                    if (hintHight >= 0 && state != RELEASE_STATE){

                        state = RELEASE_STATE;
                        processState();

                    }else if(hintHight<0 && state != PULL_STATE){

                        state = PULL_STATE;
                        processState();
                    }
                    //自己拖动出下拉刷新的View
                    return  true;//屏蔽掉listview的touch事件
                }
                break;
            case MotionEvent.ACTION_UP:

                if (state == PULL_STATE){
                    //继续隐藏
                    mRl_refresh.setPadding(0,-mHeadMeasuredHeight,0,0);

                }else if (state == RELEASE_STATE){

                    state = REFRESH_STATE;//刷新
                    processState();
                    mRl_refresh.setPadding(0, 0, 0, 0);//刚刚完全显示

                    if (mOnRefreshDataListener!=null){

                        mOnRefreshDataListener.refreshData();

                    }
                }

                isTimeShow = true;
                break;

            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    //处理状态
    //会被多次调用
    private void processState() {

        switch (state ){

            case PULL_STATE:
                mIv_arrow_headlistview.startAnimation(mRa_toDown);
                mTv_refresh_headlistview.setText("下拉刷新");
                break;

            case RELEASE_STATE:
                mIv_arrow_headlistview.startAnimation(mRa_toUp);
                mTv_refresh_headlistview.setText("松开刷新");
                break;

            case REFRESH_STATE:
                //箭头清除动画
                mIv_arrow_headlistview.clearAnimation();
                mIv_arrow_headlistview.setVisibility(GONE);
                mPb_load_headlistview.setVisibility(VISIBLE);
                mTv_refresh_headlistview.setText("正在刷新");
                break;

            default:
                break;
        }
    }


    /*
    * 获得当前时间  2016-02-21 23:17:24
    * */
    private String getCurrentTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    /**
     * 第一个位置中:轮播图是否完全显示
     *
     * @return
     */
    private boolean isLunboShow(){

        if (mLunboView == null){
            return true;
        }

        int[] location = new int[2];

        // 获取listview在屏幕中的坐标
        this.getLocationInWindow(location);
        // listview在屏幕中的y坐标
        int lv_y = location[1];

        // 获取轮播图在屏幕中的位置
        mLunboView.getLocationInWindow(location);
        //获取轮播图在屏幕中的y坐标
        int lb_y = location[1];

        if (lb_y>=lv_y){
            //轮播图完全显示
            return true;

        }else{

            return false;
        }
    }

    //头部的线性布局添加轮播图或其他的view的API，供外界调用
    public void addLunBoView(View view) {
        mLunboView = view;
        if (mLunboView!=null){

            mHeadRootView.addView(mLunboView);
        }
    }

    //供外界调用，更新刷新头状态
    public void updateRefreshState() {

        //更新状态
        state = PULL_STATE;//让listview可用

        mPb_load_headlistview.setVisibility(GONE);

        mIv_arrow_headlistview.setVisibility(VISIBLE);

        mTv_refresh_headlistview.setText("下拉刷新");

        mTv_time_item_detailnew.setText(getCurrentTime());

        //隐藏刷新的view
        mRl_refresh.setPadding(0, -mHeadMeasuredHeight, 0, 0);

    }

    //供外界调用，更新加载更多状态
    public void updateLoadmore(){

        mFootView.setPadding(0,-mFootMeasuredHeight,0,0);
    }

    //定义接口，通知刷新数据和加载更多
    public interface OnRefreshDataListener{

        void refreshData();

        void loadMore();
    }

    public OnRefreshDataListener mOnRefreshDataListener;

    public void setOnRefreshDataListener(OnRefreshDataListener onRefreshDataListener){

        mOnRefreshDataListener = onRefreshDataListener;
    }
}
