package com.pgssoft.rxjavaweather.ui.add;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pgssoft.rxjavaweather.R;
import com.pgssoft.rxjavaweather.databinding.ItemCityBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dawidpodolak on 27.04.2017.
 */

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHoler> {

    private List<AddViewModel.CityViewModel> citiesViewModel = new ArrayList<>();

    private LayoutInflater inflater;

    public CityAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public CityViewHoler onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemCityBinding icb = DataBindingUtil.inflate(inflater, R.layout.item_city, parent, false);
        return new CityViewHoler(icb);
    }

    @Override
    public void onBindViewHolder(CityViewHoler holder, int position) {
        holder.setViewModel(citiesViewModel.get(position));
    }

    @Override
    public int getItemCount() {
        return citiesViewModel.size();
    }

    public void setCityList(List<AddViewModel.CityViewModel> cities){
        citiesViewModel = cities;
    }

    static class CityViewHoler extends RecyclerView.ViewHolder{

        private ItemCityBinding binding;
        public CityViewHoler(ItemCityBinding icb) {
            super(icb.getRoot());
            binding = icb;
        }

        public void setViewModel(AddViewModel.CityViewModel viewModel) {
            binding.setViewModel(viewModel);
        }
    }


}
