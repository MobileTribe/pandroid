package com.leroymerlin.pandroid.templates.feature;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.leroymerlin.pandroid.demo.R;
import com.leroymerlin.pandroid.demo.main.MainActivity;
import com.leroymerlin.pandroid.ui.toast.ToastManager;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

/**
 * Created by florian on 16/10/2017.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TemplatesTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);


    @Test
    public void testFeatureTemplate() throws Throwable {

        MainActivity activity = activityRule.getActivity();
        FeatureFragment fragment = new FeatureFragment();
        fragment.toastManager = Mockito.mock(ToastManager.class);

        fragment.presenter = new FeatureFragmentPresenter();
        FeatureManager featureManager = Mockito.mock(FeatureManager.class);
        fragment.presenter.featureManager = featureManager;

        Mockito.when(featureManager.loadData()).thenReturn(Observable.error(new Exception("error")));
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_content_container, fragment)
                .commit();

        Espresso.onView(ViewMatchers.withId(R.id.feature_loader))
                .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        Mockito.verify(fragment.toastManager, VerificationModeFactory.times(1)).makeToast(Mockito.any(), Mockito.eq("error"), Mockito.any(), Mockito.anyInt());

        ArrayList<FeatureModel> models = new ArrayList<>();
        models.add(new FeatureModel());
        models.add(new FeatureModel());
        models.add(new FeatureModel());

        Mockito.when(featureManager.loadData()).thenReturn(Observable.fromIterable(models).delay(3, TimeUnit.SECONDS));
        Mockito.when(fragment.toastManager.makeToast(Mockito.any(), Mockito.anyString(), Mockito.any(), Mockito.anyInt()))
                .thenThrow(new IllegalStateException("Error should not apprend"));


        Espresso.onView(ViewMatchers.withId(R.id.feature_retry))
                .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
                .perform(ViewActions.click())
                .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        Espresso.onView(ViewMatchers.withId(R.id.feature_loader))
                .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        Espresso.onView(ViewMatchers.withId(R.id.feature_rv))
                .perform(RecyclerViewActions.actionOnItemAtPosition(2, ViewActions.click()));

        Mockito.clearInvocations(featureManager);
        Mockito.verify(featureManager, VerificationModeFactory.noMoreInteractions()).loadData();


        activityRule.runOnUiThread(fragment::reload);

        Espresso.onIdle();

    }

}
