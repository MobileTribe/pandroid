package com.leroymerlin.pandroid.ui.animation.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.leroymerlin.pandroid.R;
import com.leroymerlin.pandroid.log.PandroidLogger;
import com.leroymerlin.pandroid.ui.animation.SimpleAnimatorListener;
import com.leroymerlin.pandroid.ui.animation.ViewInfosContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by florian on 19/10/2015.
 */
public class MaterialTransitionLayout extends FrameLayout {

    private static final String TAG = "MaterialTransitionLayout";
    private CircularFrameLayout circularFrameLayout;
    private int revealLayout;

    protected int ANIM_FADE_DURATION;
    protected int animMoveDuration;
    protected Interpolator interpolator = new AccelerateDecelerateInterpolator();


    public MaterialTransitionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        ANIM_FADE_DURATION = getResources().getInteger(R.integer.fast_anim_speed);
        animMoveDuration = (getResources().getInteger(R.integer.medium_anim_speed));
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MaterialTransitionLayout);
            circularFrameLayout = new CircularFrameLayout(context);
            circularFrameLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            addView(circularFrameLayout);
            revealLayout = a.getResourceId(R.styleable.MaterialTransitionLayout_revealLayout, 0);
            a.recycle();
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child.getId() == revealLayout) {
            circularFrameLayout.addView(child, params);
        } else {
            super.addView(child, Math.max(0, getChildCount() - 1), params);
        }
    }


    private HashMap<ViewInfosContainer, ViewInfosContainer> animatedMap = new HashMap<>();
    private int[] revealCenter;
    private boolean opening = true;


    public void setAnimMoveDuration(int animMoveDuration) {
        this.animMoveDuration = animMoveDuration;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    public View addAnimationWithViewId(ViewInfosContainer fromInfos, int targetId) {
        return addAnimation(fromInfos, new ViewInfosContainer(findTargetViewById(targetId), this));
    }

    public View addAnimationWithViewTag(ViewInfosContainer fromInfos, Object targetTag) {
        return addAnimation(fromInfos, new ViewInfosContainer(findTargetViewByTag(targetTag), this));
    }

    public View addAnimation(ViewInfosContainer fromInfos, ViewInfosContainer toInfos) {

        View transitionView = null;
        try {
            transitionView = toInfos.viewClass.getConstructor(Context.class).newInstance(getContext());
        } catch (Exception e) {
            PandroidLogger.getInstance().wtf(TAG, e);
            return null;
        }
        View targetView = findTargetView(toInfos);
        opening = true;

        if (targetView instanceof ImageView && transitionView instanceof ImageView) {
            ((ImageView) transitionView).setImageDrawable(((ImageView) targetView).getDrawable());
        } else if (targetView instanceof TextView && transitionView instanceof TextView) {
            ((TextView) transitionView).setText(((TextView) targetView).getText());
            ((TextView) transitionView).setTypeface(((TextView) targetView).getTypeface());
        }
        addView(transitionView);
        fromInfos.applyOn(transitionView);
        transitionView.setId(targetView.getId());
        transitionView.setTag(targetView.getTag());
        animatedMap.put(toInfos, fromInfos);
        return transitionView;
    }

    @SuppressWarnings(value = "ObjectAnimatorBinding")
    private List<Animator> createViewAnimators(final View transitionView, final ViewInfosContainer fromInfos, final ViewInfosContainer toInfos) {
        List<Animator> animators = new ArrayList<>();
        if (transitionView instanceof TextView) {
            ValueAnimator textColorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), fromInfos.textColor, toInfos.textColor);
            textColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    ((TextView) transitionView).setTextColor((Integer) animator.getAnimatedValue());
                }

            });
            animators.add(textColorAnimator);
            ValueAnimator textSizeAnimator = ValueAnimator.ofFloat(fromInfos.textSize, toInfos.textSize);
            textSizeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    ((TextView) transitionView).setTextSize(TypedValue.COMPLEX_UNIT_PX, (Float) animator.getAnimatedValue());
                }
            });
            animators.add(textSizeAnimator);


        }

        if (fromInfos.backgroundColor != toInfos.backgroundColor && toInfos.backgroundColor != null && fromInfos.backgroundColor != null) {
            ValueAnimator backgroundColor = ValueAnimator.ofObject(new ArgbEvaluator(), fromInfos.backgroundColor, toInfos.backgroundColor);
            backgroundColor.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    transitionView.setBackgroundColor((Integer) animator.getAnimatedValue());
                }
            });
            animators.add(backgroundColor);
        }
        ValueAnimator paddingAnimator = ValueAnimator.ofFloat(0f, 1f);
        paddingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                float value = ((float) animator.getAnimatedValue());
                transitionView.setPadding(
                        (int) (fromInfos.padding[0] * (1 - value) + toInfos.padding[0] * value),
                        (int) (fromInfos.padding[1] * (1 - value) + toInfos.padding[1] * value),
                        (int) (fromInfos.padding[2] * (1 - value) + toInfos.padding[2] * value),
                        (int) (fromInfos.padding[3] * (1 - value) + toInfos.padding[3] * value)
                );
            }
        });
        animators.add(paddingAnimator);

        ObjectAnimator animatorTranslationX = ObjectAnimator.ofFloat(transitionView, "x", toInfos.getX());
        animators.add(animatorTranslationX);
        ObjectAnimator animatorTranslationY = ObjectAnimator.ofFloat(transitionView, "y", toInfos.getY());
        animators.add(animatorTranslationY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animators.add(ObjectAnimator.ofFloat(transitionView, "elevation", toInfos.elevation));
        }


        ValueAnimator animWidth = ValueAnimator.ofInt(fromInfos.getWidth(), toInfos.getWidth());
        animWidth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = transitionView.getLayoutParams();
                layoutParams.width = val;
                transitionView.setLayoutParams(layoutParams);
            }
        });
        animators.add(animWidth);

        ValueAnimator animHeight = ValueAnimator.ofInt(fromInfos.getHeight(), toInfos.getHeight());
        animHeight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = transitionView.getLayoutParams();
                layoutParams.height = val;
                transitionView.setLayoutParams(layoutParams);
            }
        });
        animators.add(animHeight);


        return animators;
    }

    public MaterialTransitionLayout setRevealCenter(int[] center) {
        this.revealCenter = center;
        return this;
    }

    public MaterialTransitionLayout setRevealCenter(int viewId) {
        this.revealCenter = new int[]{viewId};
        return this;
    }

    public void close(@Nullable Animator.AnimatorListener listener) {
        opening = false;
        open(listener);
    }

    public void close() {
        close(null);
    }

    public void open() {
        open(null);
    }

    public void open(@Nullable final Animator.AnimatorListener listener) {
        Runnable action = new Runnable() {
            @Override
            public void run() {
                List<Animator> animators = new ArrayList<>();
                for (Map.Entry<ViewInfosContainer, ViewInfosContainer> entry : animatedMap.entrySet()) {
                    View targetView = findTargetView(entry.getKey());
                    View transitionView = findTransitionView(entry.getKey());
                    ViewInfosContainer targetInfos = new ViewInfosContainer(targetView, MaterialTransitionLayout.this);
                    if (opening) {
                        animators.addAll(createViewAnimators(transitionView, entry.getValue(), targetInfos));
                    } else {
                        //targetView.setVisibility(INVISIBLE);
                        animators.addAll(createViewAnimators(transitionView, targetInfos, entry.getValue()));
                    }

                }
                AnimatorSet animatorSet = new AnimatorSet();

                if (!opening) {
                    circularFrameLayout.animate().alpha(0).setDuration(ANIM_FADE_DURATION).start();
                } else {
                    circularFrameLayout.setAlpha(0);
                }

                animatorSet.playTogether(animators);
                animatorSet.setDuration(animMoveDuration);
                animatorSet.setInterpolator(interpolator);
                if (opening) {
                    animatorSet.addListener(
                            new SimpleAnimatorListener() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        float elevation = 0;
                                        for (int i = 0; i < getChildCount(); i++) {
                                            elevation = Math.max(getChildAt(i).getElevation(), elevation);
                                        }
                                        circularFrameLayout.setElevation(elevation);
                                    }
                                    circularFrameLayout.setVisibility(VISIBLE);
                                    SimpleAnimatorListener endAnimListener = new SimpleAnimatorListener(listener) {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            setTransitionViewsVisibility(GONE);
                                        }
                                    };
                                    if (revealCenter != null) {
                                        circularFrameLayout.setAlpha(1);
                                        if (revealCenter.length > 1)
                                            circularFrameLayout.setCenter(revealCenter[0], revealCenter[1]);
                                        else {
                                            ViewInfosContainer viewInfos = new ViewInfosContainer(findTargetViewById(revealCenter[0]), MaterialTransitionLayout.this);
                                            int[] center = viewInfos.getCenter();
                                            circularFrameLayout.setCenter(center[0], center[1]);
                                        }
                                        circularFrameLayout.setClipOutEnable(true);
                                        circularFrameLayout.open(endAnimListener);
                                    } else {
                                        circularFrameLayout.setClipOutEnable(false);
                                        circularFrameLayout.animate().alpha(1).setDuration(ANIM_FADE_DURATION).setListener(endAnimListener).start();
                                    }
                                }
                            }
                    );
                } else if (listener != null) {
                    animatorSet.addListener(listener);
                }
                animatorSet.start();
            }
        };
        if (opening)
            post(action);
        else
            action.run();

        setTransitionViewsVisibility(VISIBLE);
        circularFrameLayout.setVisibility(opening ? INVISIBLE : VISIBLE);

    }

    public View findTargetViewById(int viewID) {
        return circularFrameLayout.findViewById(viewID);
    }

    public View findTargetViewByTag(Object tag) {
        return circularFrameLayout.findViewWithTag(tag);
    }

    public View findTargetView(ViewInfosContainer infosContainer) {
        if (infosContainer.getTag() != null) {
            View targetViewByTag = findTargetViewByTag(infosContainer.getTag());
            if (targetViewByTag != null)
                return targetViewByTag;
        }
        return findTargetViewById(infosContainer.getViewId());
    }

    public View findTransitionView(ViewInfosContainer infosContainer) {
        for (int i = 0; i < getChildCount() - 1; i++) {
            if (infosContainer.getTag() != null) {
                if (infosContainer.getTag().equals(getChildAt(i).getTag()))
                    return getChildAt(i);
            } else if (infosContainer.getViewId() > 0) {
                if (getChildAt(i).getId() == infosContainer.getViewId())
                    return getChildAt(i);
            }
        }
        return null;
    }

    private void setTransitionViewsVisibility(int visibility) {
        for (ViewInfosContainer toInfos : animatedMap.keySet()) {
            findTransitionView(toInfos).setVisibility(visibility);
        }
    }

    private static final String KEY_CENTER = "KEY_CENTER";
    private static final String KEY_SUPER = "KEY_SUPER";
    private static final String KEY_OPEN = "KEY_OPEN";
    private static final String KEY_ANIMATOR_MAP = "KEY_ANIMATOR_MAP";

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable(KEY_SUPER));
            opening = bundle.getBoolean(KEY_OPEN);
            final HashMap<ViewInfosContainer, ViewInfosContainer> map = (HashMap<ViewInfosContainer, ViewInfosContainer>) bundle.getSerializable(KEY_ANIMATOR_MAP);
            post(new Runnable() {
                @Override
                public void run() {
                    for (Map.Entry<ViewInfosContainer, ViewInfosContainer> entry : map.entrySet()) {
                        View targetView = findTargetView(entry.getKey());
                        if (targetView != null) {
                            ViewInfosContainer fromInfos = new ViewInfosContainer(targetView, MaterialTransitionLayout.this);
                            addAnimation(fromInfos, fromInfos).setVisibility(INVISIBLE);
                            animatedMap.put(fromInfos, entry.getValue());
                        }
                    }
                }
            });
        } else {
            super.onRestoreInstanceState(state);
        }

    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        if (revealCenter != null) {
            bundle.putIntArray(KEY_CENTER, revealCenter);
        }
        bundle.putParcelable(KEY_SUPER, super.onSaveInstanceState());
        bundle.putBoolean(KEY_OPEN, opening);
        bundle.putSerializable(KEY_ANIMATOR_MAP, animatedMap);
        return bundle;
    }

    public boolean isClosing() {
        return !opening;
    }

}