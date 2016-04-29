package com.leroymerlin.pandroid.ui.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.leroymerlin.pandroid.log.LogWrapper;
import com.leroymerlin.pandroid.log.PandroidLogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by paillardf on 03/03/15.
 */
public class QuickHeaderLayout extends FrameLayout {

    private final static String TAG = "QuickHeaderLayout";

    LogWrapper logWrapper = PandroidLogger.getInstance();


    private int headerHeight = 0;
    private int initY = 0;
    private float initPos = 0;
    private OnScrollListener mListener;
    private boolean enable = true;


    public QuickHeaderLayout(Context context) {
        super(context);
    }


    public QuickHeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() > 1) {
            throw new IllegalStateException("QuickHeaderLayout can host only two direct child");
        }

        if (getChildCount() == 0 && !isSupportedView(child)) {
            throw new IllegalStateException("First child view has to be scrollable");
        }


        super.addView(child, index, params);


        if (child instanceof AbsListView) {
            ((AbsListView) child).setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (SCROLL_STATE_TOUCH_SCROLL == scrollState) {// scroll begin
                        QuickHeaderLayout.this.onScrollStarted();
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    QuickHeaderLayout.this.onScroll();
                }
            });
        } else if (child instanceof RecyclerView) {
            ((RecyclerView) child).addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == recyclerView.SCROLL_STATE_IDLE) {
                        QuickHeaderLayout.this.onScrollStarted();
                    }
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    QuickHeaderLayout.this.onScroll();
                }
            });
        } else if (child instanceof ScrollView) {
            child.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

                @Override
                public void onScrollChanged() {
                    onScroll();
                }
            });


        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            QuickHeaderLayout.this.onScrollStarted();
        }
        return super.onInterceptTouchEvent(ev);
    }


    private boolean isSupportedView(View view) {
        return view instanceof AbsListView || view instanceof ScrollView || view instanceof RecyclerView;
    }

    /**
     * getReceivers the opening value of the header
     *
     * @return 1.0 if the header is completely open, 0 if he is completely hide
     */
    public float getHeaderOpenValue() {
        return 1 + getHeaderView().getTranslationY() / headerHeight;
    }

    /**
     * set the open value of the header
     *
     * @param openValue float between 1 (open) and 0 (close)
     * @param anim      true to animate, false otherwise
     */
    public void setHeaderOpenValue(float openValue, boolean anim) {
        float futureTranslation = (Math.min(1, Math.max(0, openValue)) - 1) * headerHeight;
        if (anim) {
            getHeaderView().animate().translationY(futureTranslation);
        } else {
            getHeaderView().setTranslationY(futureTranslation);
        }
    }

    public void setHeaderEnable(boolean enable) {
        this.enable = enable;
        if (!enable && getHeaderOpenValue() != 0) {
            setHeaderOpenValue(1, true);
        }
    }

    private void onScrollStarted() {
        View headerView = getHeaderView();
        if (headerView == null) {
            return;
        }
        initY = -getScroll();
        initPos = headerView.getTranslationY();

    }

    private void onScroll() {

        int scrollPos = -getScroll();
        int diffY = scrollPos - initY;

        float pos = Math.max(Math.min(0, initPos + diffY), -headerHeight);

        View headerView = getHeaderView();
        if (headerView != null && enable) {
            headerView.setTranslationY(pos);
        }
        if (mListener != null)
            mListener.onScroll();
    }

    boolean init = false;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (init)
            return;

        View scrollableView = getScrollableView();
        View headerView = getHeaderView();
        if (headerView != null && scrollableView != null) {
            headerHeight = headerView.getMeasuredHeight();
            int top = scrollableView.getTop();
            if (top < headerHeight) {
                top += headerHeight;
            }

            scrollableView.setPadding(scrollableView.getLeft(), top, scrollableView.getRight(), scrollableView.getBottom());

            if (scrollableView instanceof AbsListView) {
                ((AbsListView) scrollableView).setClipToPadding(false);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec); //needed to recalculate listPadding.top on abstractListView
            } else if (scrollableView instanceof RecyclerView)
                ((RecyclerView) scrollableView).setClipToPadding(false);
            else if (scrollableView instanceof ScrollView)
                ((ScrollView) scrollableView).setClipToPadding(false);

            onScrollStarted();
            init = true;
        }
    }

    /**
     * The views height could be different so we need to remember all item height
     */
    private Hashtable<Integer, Integer> listViewItemHeights = new Hashtable<Integer, Integer>();


    int initialPos = -1;

    //If all view have the same height a variant could be: view.getChildAt(0).getTop()-view.getFirstVisiblePosition()*view.getChildAt(0).getHeight();
    private int getScroll() {
        View view = getScrollableView();
        if (view instanceof AbsListView) {
            AbsListView listView = (AbsListView) view;

            View c = listView.getChildAt(0); //this is the first visible row
            if (c == null)
                return 0;
            int scrollY = -c.getTop();
            int dividerHeight = 0;
            if (listView instanceof ListView) {
                dividerHeight = ((ListView) listView).getDividerHeight();
            }
            listViewItemHeights.put(listView.getFirstVisiblePosition(), c.getHeight() + dividerHeight);

            if (initialPos < 0) {
                initialPos = listView.getFirstVisiblePosition();
            }

            for (Map.Entry<Integer, Integer> set : listViewItemHeights.entrySet()) {
                if (listView.getFirstVisiblePosition() > set.getKey() && initialPos <= set.getKey()) {
                    scrollY += set.getValue();
                } else if (listView.getFirstVisiblePosition() <= set.getKey() && initialPos > set.getKey()) {
                    scrollY -= set.getValue();
                }

            }
            return scrollY;
        } else if (view instanceof RecyclerView) {
            int scroll = 0;
            Method privateStringMethod = null;
            try {
                privateStringMethod = RecyclerView.class.
                        getDeclaredMethod("computeVerticalScrollOffset");
                privateStringMethod.setAccessible(true);
                scroll = (Integer)
                        privateStringMethod.invoke(view);
            } catch (InvocationTargetException e) {
                logWrapper.wtf(TAG, e);
            } catch (IllegalAccessException e) {
                logWrapper.wtf(TAG, e);
            } catch (NoSuchMethodException e) {
                logWrapper.wtf(TAG, e);
            }
            return scroll;


        } else {
            return view.getScrollY();
        }

    }

    public void setScrollListener(OnScrollListener listener) {
        mListener = listener;
    }

    public View getScrollableView() {
        return getChildAt(0);
    }

    public View getHeaderView() {
        return getChildAt(1);

    }


    public interface OnScrollListener {
        public void onScroll();
    }
}
