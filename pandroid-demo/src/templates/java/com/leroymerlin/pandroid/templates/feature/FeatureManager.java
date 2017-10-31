package com.leroymerlin.pandroid.templates.feature;

import com.leroymerlin.pandroid.templates.feature.FeatureModel;

import io.reactivex.Observable;

/**
 * Created by florian on 12/10/2017.
 */

public interface FeatureManager {

    Observable<FeatureModel> loadData();
}
