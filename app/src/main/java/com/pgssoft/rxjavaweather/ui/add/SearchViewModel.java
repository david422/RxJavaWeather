package com.pgssoft.rxjavaweather.ui.add;

import android.databinding.ObservableField;

/**
 * Created by dpodolak on 19.04.2017.
 *
 * This interface should be implement by viewModel which handle search view
 */
public interface SearchViewModel {

    void searchClear();

    ObservableField<String> getSearchTextObservable();

}
