package com.leroymerlin.pandroid.templates.feature.managers;

import com.leroymerlin.pandroid.templates.feature.models.FeatureModel;

import io.reactivex.Observable;

/**
 * Created by florian on 12/10/2017.
 */

public interface FeatureManager {

    Observable<FeatureModel> loadData();
}
