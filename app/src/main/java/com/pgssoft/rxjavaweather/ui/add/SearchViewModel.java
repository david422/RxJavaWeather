package com.pgssoft.rxjavaweather.ui.add;

import android.databinding.ObservableField;

/**
 * Created by dpodolak on 19.04.2017.
 */

public interface SearchViewModel {

//    TextWatcher getSearchTextWatcher();

    void searchClear();

    ObservableField<String> getSearchTextObservable();


}
