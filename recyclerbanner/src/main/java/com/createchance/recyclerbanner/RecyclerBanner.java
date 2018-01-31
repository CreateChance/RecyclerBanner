package com.createchance.recyclerbanner;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 使用RecyclerView实现的轮播banner
 *
 * @author gaochao1-iri
 * @date 16/01/2018
 */
public class RecyclerBanner extends FrameLayout {

    private static final String TAG = RecyclerBanner.class.getSimpleName();

    /**
     * 自动轮播的时间间隔，单位为毫秒
     */
    private long mInterval;

    /**
     * 指示器默认的选中颜色为保色
     */
    private static final int DEFAULT_SELECTED_COLOR = 0xffffffff;
    /**
     * 指示器伯任未选中的颜色为灰色
     */
    private static final int DEFAULT_UNSELECTED_COLOR = 0x50ffffff;
    /**
     * 指示器选中的drawable对象
     */
    private Drawable mSelectedDrawable;
    /**
     * 指示器未选中的drawable对象
     */
    private Drawable mUnselectedDrawable;
    /**
     * 指示器大小，就是圆圈尺寸，默认为4dp
     */
    private int mSize;
    /**
     * 指示器之间的间隔大小，默认为8dp
     */
    private int mSpace;
    /**
     * 指示器view的view group容器
     */
    private LinearLayout mIndicatorContainer;

    /**
     * 承载列表的recycler view
     */
    private RecyclerView mRecyclerView;

    /**
     * 承载列表的recycler view adapter
     */
    private RecyclerAdapter mRecyclerViewAdapter;

    /**
     * 轮播banner的回调接口
     */
    private RecyclerBannerCallback mCallback;

    /**
     * 轮播banner回调接口定义
     */
    public interface RecyclerBannerCallback {
        /**
         * 创建banner view对象，需要客户端创建banner view对象，然后返回
         *
         * @param parent banner view所处于的parent容器
         * @return 初始化完毕的banner view
         */
        View createBannerView(ViewGroup parent);

        /**
         * 当前banner发生了切换
         *
         * @param position   切换后的位置
         * @param bannerView 当前banner view对象，客户端需要将必要的数据填充到view中以展示
         */
        void switchBanner(int position, View bannerView);
    }

    /**
     * 轮播banner的大小，也就是有多少个banner需要展示
     */
    private int mBannerSize;

    /**
     * 手动滑动操作轮播banner相关
     */
    private int mStartX, mStartY;

    /**
     * 当前banner位置index
     */
    private int mCurrentIndex;

    /**
     * 主线程handler，轮播的task运行在这个handler中
     */
    private Handler mMainHandler;

    /**
     * 当前是不是正在自动播放banner
     */
    private boolean mIsPlaying;

    /**
     * 当前是否发生了手指触碰，手指触碰需要停止自动播放，因为此时用户需要手动滑动观看
     */
    private boolean mIsTouched;

    /**
     * 是否展示位置指示器
     */
    private boolean mIsShowIndicator;

    /**
     * 是否自动播放banner
     */
    private boolean mIsAutoPlaying = true;

    /**
     * banner自动播放task，在主线程中执行
     */
    private Runnable mAutoPlayTask = new Runnable() {

        @Override
        public void run() {
            mRecyclerView.smoothScrollToPosition(++mCurrentIndex);
            if (mIsShowIndicator) {
                switchIndicator();
            }
            mMainHandler.postDelayed(this, mInterval);
        }
    };

    public RecyclerBanner(Context context) {
        this(context, null);
    }

    public RecyclerBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mMainHandler = new Handler(context.getMainLooper());

        // 从xml布局中获取设置的参数
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecyclerBanner);
        mInterval = a.getInt(R.styleable.RecyclerBanner_playInterval, 3000);
        mIsShowIndicator = a.getBoolean(R.styleable.RecyclerBanner_showIndicator, true);
        mIsAutoPlaying = a.getBoolean(R.styleable.RecyclerBanner_autoPlaying, true);
        Drawable selectedDrawable = a.getDrawable(R.styleable.RecyclerBanner_indicatorSelected);
        Drawable unselectedDrawable = a.getDrawable(R.styleable.RecyclerBanner_indicatorUnselected);
        if (selectedDrawable == null) {
            mSelectedDrawable = generateDefaultDrawable(DEFAULT_SELECTED_COLOR);
        } else {
            if (selectedDrawable instanceof ColorDrawable) {
                mSelectedDrawable = generateDefaultDrawable(((ColorDrawable) selectedDrawable).getColor());
            } else {
                mSelectedDrawable = selectedDrawable;
            }
        }
        if (unselectedDrawable == null) {
            mUnselectedDrawable = generateDefaultDrawable(DEFAULT_UNSELECTED_COLOR);
        } else {
            if (unselectedDrawable instanceof ColorDrawable) {
                mUnselectedDrawable = generateDefaultDrawable(((ColorDrawable) unselectedDrawable).getColor());
            } else {
                mUnselectedDrawable = unselectedDrawable;
            }
        }
        mSize = a.getDimensionPixelSize(R.styleable.RecyclerBanner_indicatorSize, 0);
        mSpace = a.getDimensionPixelSize(R.styleable.RecyclerBanner_indicatorSpace, dp2px(4));
        int indicatorMargin = a.getDimensionPixelSize(R.styleable.RecyclerBanner_indicatorMargin, dp2px(8));
        int gravityValue = a.getInt(R.styleable.RecyclerBanner_indicatorGravity, 1);
        int gravity;
        if (gravityValue == 0) {
            gravity = GravityCompat.START;
        } else if (gravityValue == 2) {
            gravity = GravityCompat.END;
        } else {
            gravity = Gravity.CENTER;
        }
        a.recycle();

        mRecyclerView = new RecyclerView(context);
        mIndicatorContainer = new LinearLayout(context);

        new PagerSnapHelper().attachToRecyclerView(mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false));
        mRecyclerViewAdapter = new RecyclerAdapter();
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int first = ((LinearLayoutManager) recyclerView.getLayoutManager()).
                            findFirstCompletelyVisibleItemPosition();
                    int last = ((LinearLayoutManager) recyclerView.getLayoutManager()).
                            findLastCompletelyVisibleItemPosition();
                    if (first == last && mCurrentIndex != last) {
                        mCurrentIndex = last;
                        if (mIsShowIndicator && mIsTouched) {
                            mIsTouched = false;
                            switchIndicator();
                        }
                    }
                }
            }
        });
        mIndicatorContainer.setOrientation(LinearLayout.HORIZONTAL);
        mIndicatorContainer.setGravity(Gravity.CENTER);

        LayoutParams vpLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        LayoutParams linearLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayoutParams.gravity = Gravity.BOTTOM | gravity;
        linearLayoutParams.setMargins(indicatorMargin, indicatorMargin, indicatorMargin, indicatorMargin);
        addView(mRecyclerView, vpLayoutParams);
        addView(mIndicatorContainer, linearLayoutParams);

        // 便于在xml中编辑时观察，运行时不执行
        if (isInEditMode()) {
            mBannerSize = 5;
            createIndicators();
        }
    }

    /******************************** APIS START ********************************/

    /**
     * 获取RecyclerView实例，便于满足自定义{@link RecyclerView.ItemAnimator}
     * 或者{@link RecyclerView.Adapter}的需求
     *
     * @return RecyclerView实例
     */
    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    /**
     * 设置回调器
     *
     * @param callback 回调器对象
     */
    public void setRecyclerBannerCallback(RecyclerBannerCallback callback) {
        this.mCallback = callback;
    }

    /**
     * 获得当前banner的位置
     *
     * @return 当前banner的位置
     */
    public int getCurrentPosition() {
        return mCurrentIndex % mBannerSize;
    }

    /**
     * 刷新banner轮播，用于banner列表数据发生变化时，刷新展示
     */
    public void refresh() {
        mRecyclerViewAdapter.notifyDataSetChanged();
        if (mIsShowIndicator) {
            createIndicators();
        }
    }

    /**
     * 设置是否禁止滚动播放
     *
     * @param isAutoPlaying true  是自动滚动播放,false 是禁止自动滚动
     */
    public void setAutoPlaying(boolean isAutoPlaying) {
        this.mIsAutoPlaying = isAutoPlaying;
    }

    /**
     * 设置轮播数据集尺寸大小
     *
     * @param size banner列表的尺寸
     */
    public void setBannerCount(int size) {
        this.mBannerSize = size;
        if (mIsShowIndicator) {
            createIndicators();
        }
        // try to playing banner.
        setPlaying(true);
    }

    /**
     * 设置是否显示指示器导航点
     *
     * @param show 显示
     */
    public void isShowIndicator(boolean show) {
        this.mIsShowIndicator = show;
    }

    /**
     * 设置轮播间隔时间
     *
     * @param millisecond 时间毫秒
     */
    public void setIndicatorInterval(int millisecond) {
        this.mInterval = millisecond;
    }

    /******************************** APIS END ********************************/

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //手动触摸的时候，停止自动播放，根据手势变换
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = (int) ev.getX();
                mStartY = (int) ev.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) ev.getX();
                int moveY = (int) ev.getY();
                int disX = moveX - mStartX;
                int disY = moveY - mStartY;
                boolean hasMoved = 2 * Math.abs(disX) > Math.abs(disY);
                getParent().requestDisallowInterceptTouchEvent(hasMoved);
                if (hasMoved) {
                    setPlaying(false);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!mIsPlaying) {
                    mIsTouched = true;
                    setPlaying(true);
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setPlaying(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setPlaying(false);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        if (visibility == GONE || visibility == INVISIBLE) {
            // 停止轮播
            setPlaying(false);
        } else if (visibility == VISIBLE) {
            // 开始轮播
            setPlaying(true);
        }
        super.onWindowVisibilityChanged(visibility);
    }

    /**
     * RecyclerView适配器
     */
    private class RecyclerAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = null;

            if (mCallback != null) {
                itemView = mCallback.createBannerView(parent);
            }

            return new RecyclerView.ViewHolder(itemView) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (mCallback != null) {
                mCallback.switchBanner(position % mBannerSize, holder.itemView);
            }
        }

        @Override
        public int getItemCount() {
            return mBannerSize == 0 ? 0 : mBannerSize < 2 ? mBannerSize : Integer.MAX_VALUE;
        }
    }

    /**
     * 自定义LinearSnapHelper，原理是根据当前滑动的速度计算因该滑动到的位置
     * 这里我们统一将滑动的目标位置定为下一个位置或者上一个位置，根据滑动的方向而定
     */
    private class PagerSnapHelper extends LinearSnapHelper {

        @Override
        public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager,
                                          int velocityX, int velocityY) {
            int targetPos = super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
            final View currentView = findSnapView(layoutManager);
            if (targetPos != RecyclerView.NO_POSITION && currentView != null) {
                int currentPos = layoutManager.getPosition(currentView);
                int first = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                int last = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                currentPos = targetPos < currentPos ?
                        last : (targetPos > currentPos ? first : currentPos);
                targetPos = targetPos < currentPos ?
                        currentPos - 1 : (targetPos > currentPos ? currentPos + 1 : currentPos);
            }
            return targetPos;
        }
    }

    /**
     * 改变导航的指示点
     */
    private void switchIndicator() {
        if (mIndicatorContainer != null && mIndicatorContainer.getChildCount() > 0) {
            for (int i = 0; i < mIndicatorContainer.getChildCount(); i++) {
                ((ImageView) mIndicatorContainer.getChildAt(i)).setImageDrawable(
                        i == mCurrentIndex % mBannerSize ? mSelectedDrawable : mUnselectedDrawable);
            }
        }
    }

    /**
     * 将dp值转为像素单位
     *
     * @param dp 需要转换的dp值
     * @return 转完之后的像素单位
     */
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }

    /**
     * 设置是否自动播放（上锁）
     *
     * @param playing 开始播放
     */
    private synchronized void setPlaying(boolean playing) {
        if (mIsAutoPlaying) {
            if (!mIsPlaying && playing && mRecyclerViewAdapter != null &&
                    mRecyclerViewAdapter.getItemCount() >= 2) {
                mMainHandler.postDelayed(mAutoPlayTask, mInterval);
                mIsPlaying = true;
            } else if (mIsPlaying && !playing) {
                mMainHandler.removeCallbacksAndMessages(null);
                mIsPlaying = false;
            }
        }
    }

    /**
     * 默认指示器是一系列直径为6dp的小圆点
     */
    private GradientDrawable generateDefaultDrawable(int color) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setSize(dp2px(6), dp2px(6));
        gradientDrawable.setCornerRadius(dp2px(6));
        gradientDrawable.setColor(color);
        return gradientDrawable;
    }

    /**
     * 指示器整体由数据列表容量数量的AppCompatImageView均匀分布在一个横向的LinearLayout中构成
     * 使用AppCompatImageView的好处是在Fragment中也使用Compat相关属性
     */
    private void createIndicators() {
        mIndicatorContainer.removeAllViews();
        for (int i = 0; i < mBannerSize; i++) {
            ImageView img = new ImageView(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = mSpace / 2;
            lp.rightMargin = mSpace / 2;
            // 设置了indicatorSize属性
            if (mSize >= dp2px(4)) {
                lp.width = lp.height = mSize;
            } else {
                // 如果设置的resource.xml没有明确的宽高，默认最小2dp，否则太小看不清
                img.setMinimumWidth(dp2px(2));
                img.setMinimumHeight(dp2px(2));
            }
            img.setImageDrawable(i == 0 ? mSelectedDrawable : mUnselectedDrawable);
            mIndicatorContainer.addView(img, lp);
        }
    }
}
