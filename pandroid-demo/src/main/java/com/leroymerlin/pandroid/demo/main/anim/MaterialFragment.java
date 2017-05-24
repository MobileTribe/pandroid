package com.leroymerlin.pandroid.demo.main.anim;

import android.animation.Animator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leroymerlin.pandroid.app.PandroidFragment;
import com.leroymerlin.pandroid.app.ResumeState;
import com.leroymerlin.pandroid.demo.R;
import com.leroymerlin.pandroid.event.OnBackListener;
import com.leroymerlin.pandroid.ui.animation.SimpleAnimatorListener;
import com.leroymerlin.pandroid.ui.animation.view.MaterialTransitionLayout;

import butterknife.BindView;


/**
 * Created by florian on 20/10/2015.
 */
//tag::FragmentWithOpener[]
//I can specify the Opener type here and I wont have to cast it later
public class MaterialFragment extends PandroidFragment<MaterialOpener> implements OnBackListener {

//end::FragmentWithOpener[]

    @BindView(R.id.material_mtl)
    MaterialTransitionLayout materialTransitionLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_material, container, false);
    }


    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    //tag::FragmentWithOpener[]
    @Override
    public void onResume(ResumeState state) {
        super.onResume(state);
        switch (state) {
            case FIRST_START:
                //the field mOpener is the Fragment Opener that was send to the event bus to open the fragment
                //here this opener is a MaterialOpener
                getView().findViewById(R.id.material_tv).setTag("coucou");
                materialTransitionLayout.addAnimationWithViewId(mOpener.ivInfos, R.id.material_iv);
                materialTransitionLayout.addAnimationWithViewTag(mOpener.tvInfos, "coucou");
                //end::FragmentWithOpener[]

                materialTransitionLayout.setRevealCenter(R.id.material_iv);
                materialTransitionLayout.open();
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
        boolean closing = materialTransitionLayout.isClosing();
        if (!closing) {
            materialTransitionLayout.close(new SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (getActivity() != null)
                        getActivity().onBackPressed();
                }
            });
        }
        return !closing;
    }
}
