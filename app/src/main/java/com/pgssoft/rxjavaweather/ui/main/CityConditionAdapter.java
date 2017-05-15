package com.pgssoft.rxjavaweather.ui.main;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pgssoft.rxjavaweather.R;
import com.pgssoft.rxjavaweather.databinding.ItemCityConditionBinding;
import com.pgssoft.rxjavaweather.model.city.City;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by dpodolak on 04.05.2017.
 */

public class CityConditionAdapter extends RecyclerView.Adapter<CityConditionAdapter.CityConditionViewHolder> implements Consumer<List<City>> {

    private LayoutInflater inflater;
    private List<City> cities = new ArrayList();

    public CityConditionAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public CityConditionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemCityConditionBinding iccb = DataBindingUtil.inflate(inflater, R.layout.item_city_condition, parent, false);
        return new CityConditionViewHolder(iccb);
    }

    @Override
    public void onBindViewHolder(CityConditionViewHolder holder, int position) {
        holder.setCity(cities.get(position));
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    @Override
    public void accept(@NonNull List<City> cities) throws Exception {
        this.cities = cities;
        notifyDataSetChanged();
    }

    public static class CityConditionViewHolder extends RecyclerView.ViewHolder {

        private ItemCityConditionBinding binding;

        public CityConditionViewHolder(ItemCityConditionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setCity(City city){
            binding.setCity(city);
        }
    }
}
