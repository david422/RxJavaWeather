package com.pgssoft.rxjavaweather.ui.add;

import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;

import com.pgssoft.rxjavaweather.R;
import com.pgssoft.rxjavaweather.RXApp;
import com.pgssoft.rxjavaweather.databinding.ActivityAddBinding;
import com.pgssoft.rxjavaweather.ui.ProgressEvent;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by dpodolak on 18.04.2017.
 */

public class AddActivity extends AppCompatActivity {


    private ActivityAddBinding binding;

    private AddViewModel viewModel;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private ProgressDialog progressDialog;

    private CityAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add);
        viewModel = new AddViewModel(RXApp.getInstance().getApi(), RXApp.getInstance().getDbManager());
        binding.setViewModel(viewModel);
        setSupportActionBar(binding.toolbarLayout.toolbar);

        adapter = new CityAdapter(this);
        binding.cityRecyclerView.setAdapter(adapter);

        binding.cityRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        binding.cityRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        compositeDisposable.add(viewModel.getCitiesObservable()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(adapter));

        viewModel.getProgressObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ProgressEvent>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(ProgressEvent progressEvent) {
                        switch (progressEvent.getState()) {
                            case ProgressEvent.SHOW: {
                                progressDialog = new ProgressDialog(AddActivity.this);
                                progressDialog.setMessage(progressEvent.getMessage(AddActivity.this));
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                            }
                            break;

                            case ProgressEvent.CLOSE: {
                                if (progressDialog != null) {
                                    progressDialog.dismiss();
                                    progressDialog = null;
                                }
                            }
                            break;

                            case ProgressEvent.ERROR: {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddActivity.this);
                                if (progressEvent.getMessage(AddActivity.this) != null) {
                                    alertDialog.setMessage(progressEvent.getMessage(AddActivity.this));
                                }
                                alertDialog.setPositiveButton(R.string.ok, null);
                                alertDialog.show();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        viewModel.setSuccessEvent(new AddViewModel.SuccessEventCallback() {
            @Override
            public void success() {
                finishWithSuccess();
            }

            @Override
            public void noDataModified() {
                AddActivity.this.finish();
            }
        });
    }

    private void finishWithSuccess() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
        viewModel.close();
    }
}
