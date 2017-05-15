package com.pgssoft.rxjavaweather.ui.main;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.pgssoft.rxjavaweather.R;
import com.pgssoft.rxjavaweather.RXApp;
import com.pgssoft.rxjavaweather.databinding.ActivityMainBinding;
import com.pgssoft.rxjavaweather.model.city.City;
import com.pgssoft.rxjavaweather.ui.BaseActivity;
import com.pgssoft.rxjavaweather.ui.OpenActivityEvent;
import com.pgssoft.rxjavaweather.ui.add.AddActivity;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends BaseActivity {

    private final static int ADD_CITY_REQUEST = 100;

    private MainViewModel viewModel;

    private ActivityMainBinding binding;

    private CityConditionAdapter cityConditionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new MainViewModel(RXApp.getInstance().getApi(), RXApp.getInstance().getDbManager());
        binding.setViewModel(viewModel);

        setSupportActionBar(binding.toolbarLayout.toolbar);
        setTitle(getString(R.string.app_name));
        cityConditionAdapter = new CityConditionAdapter(this);

        viewModel.getOpenActivityObservable()
                .subscribe(new Observer<OpenActivityEvent>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(OpenActivityEvent openActivityEvent) {
                        openActivity(openActivityEvent);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        binding.cityConditionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.cityConditionRecyclerView.setAdapter(cityConditionAdapter);


        compositeDisposable.add(viewModel.citiesObservable()
                .subscribe(cityConditionAdapter));

    }

    private void openActivity(OpenActivityEvent openActivityEvent) {
        switch (openActivityEvent.getActivity()) {
            case OpenActivityEvent.AddActivity:
                startActivityForResult(new Intent(this, AddActivity.class), ADD_CITY_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_CITY_REQUEST && resultCode == RESULT_OK) {
            viewModel.updateAndShowWeather();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.close();
    }
}
