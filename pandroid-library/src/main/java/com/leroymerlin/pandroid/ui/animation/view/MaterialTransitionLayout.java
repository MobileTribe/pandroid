package com.leroymerlin.pandroid.ui.animation.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by florian on 19/10/2015.
 */
public class MaterialTransitionLayout extends FrameLayout {

    private static final String TAG = "MaterialTransitionLayout";
    private CircularFrameLayout circularFrameLayout;
    private int revealLayout;

    public int ANIM_FADE_DURATION;
    private int ANIM_MOVE_DURATION;


    public MaterialTransitionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        ANIM_FADE_DURATION = getResources().getInteger(R.integer.fast_anim_speed);
        ANIM_MOVE_DURATION = (getResources().getInteger(R.integer.medium_anim_speed));
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
            super.addView(child, index, params);
        }
    }


    private HashMap<ViewInfosContainer, ViewInfosContainer> animatedMap = new HashMap<>();
    private int[] revealCenter;
    private boolean opening = true;

    public View addAnimation(ViewInfosContainer fromInfos, ViewInfosContainer toInfos) {

        View transitionView = null;
        try {
            transitionView = toInfos.viewClass.getConstructor(Context.class).newInstance(getContext());
        } catch (Exception e) {
            PandroidLogger.getInstance().wtf(TAG, e);
            return null;
        }
        View targetView = findTargetView(toInfos.viewId);
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
        animatedMap.put(toInfos, fromInfos);
        return transitionView;
    }

    public View addAnimation(ViewInfosContainer fromInfos, int targetId) {
        return addAnimation(fromInfos, new ViewInfosContainer(findTargetView(targetId), this));
    }

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

    public void close(Animator.AnimatorListener listener) {
        opening = false;
        open(listener);
    }

    public void close() {
        close(null);
    }

    public void open() {
        open(null);
    }

    public void open(final Animator.AnimatorListener listener) {
        Runnable action = new Runnable() {
            @Override
            public void run() {
                List<Animator> animators = new ArrayList<>();
                for (ViewInfosContainer toInfos : animatedMap.keySet()) {
                    View targetView = findTargetView(toInfos.viewId);
                    View transitionView = findTransitionView(toInfos.viewId);
                    ViewInfosContainer targetInfos = new ViewInfosContainer(targetView, MaterialTransitionLayout.this);
                    if (opening) {
                        animators.addAll(createViewAnimators(transitionView, animatedMap.get(toInfos), targetInfos));
                    } else {
                        targetView.setVisibility(INVISIBLE);
                        animators.addAll(createViewAnimators(transitionView, targetInfos, animatedMap.get(toInfos)));
                    }

                }
                AnimatorSet animatorSet = new AnimatorSet();

                if (!opening)
                    circularFrameLayout.animate().alpha(0).setDuration(ANIM_FADE_DURATION).start();
                else {
                    circularFrameLayout.setAlpha(0);
                }

                animatorSet.playTogether(animators);
                animatorSet.setDuration(ANIM_MOVE_DURATION);
                if (opening) {
                    animatorSet.addListener(
                            new SimpleAnimatorListener() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
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
                                            ViewInfosContainer viewInfos = new ViewInfosContainer(findTargetView(revealCenter[0]), MaterialTransitionLayout.this);
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
                } else {
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

    public View findTargetView(int viewID) {
        return circularFrameLayout.findViewById(viewID);
    }

    public View findTransitionView(int viewID) {
        for (int i = 1; i < getChildCount(); i++) {
            if (getChildAt(i).getId() == viewID)
                return getChildAt(i);
        }
        return null;
    }

    private void setTransitionViewsVisibility(int visibility) {
        for (ViewInfosContainer toInfos : animatedMap.keySet()) {
            findTransitionView(toInfos.viewId).setVisibility(visibility);
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
                    for (ViewInfosContainer toInfos : map.keySet()) {
                        ViewInfosContainer fromInfos = new ViewInfosContainer(findTargetView(toInfos.viewId), MaterialTransitionLayout.this);
                        addAnimation(fromInfos, fromInfos).setVisibility(INVISIBLE);
                        animatedMap.put(fromInfos, map.get(fromInfos));
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