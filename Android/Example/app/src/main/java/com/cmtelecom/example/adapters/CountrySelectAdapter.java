package com.cmtelecom.example.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cm.example.R;
import com.cmtelecom.example.domain.Country;

import java.util.ArrayList;

/**
 * Created by Dion on 13/01/15
 */
public class CountrySelectAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<Country> countries;

    public CountrySelectAdapter(Context context, ArrayList<Country> countries) {
        mInflater = LayoutInflater.from(context);
        this.countries = countries;
    }

    public int getPositionForCountryISO(String countryISO) {
        if(countries == null) return 0;

        int index = 0;
        for(Country country : countries) {
            if(country.getIsoCountryName().toLowerCase().equals(countryISO.toLowerCase())) {
                return index;
            }
            index ++;
        }
        return 0;
    }

    @Override
    public int getCount() {
        return countries.size();
    }

    @Override
    public Object getItem(int position) {
        return countries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Country country = countries.get(position);

        TextView textview = null;
        if (convertView == null)
            convertView = mInflater.inflate(R.layout.row_country_select, null);


        TextView lblCountry = (TextView) convertView.findViewById(R.id.lblCountry);
        TextView lblCountryCode = (TextView) convertView.findViewById(R.id.lblCountryCode);

        lblCountry.setText(country.getCountryName());
        lblCountryCode.setText(country.getCountryCodeAsString());

        return convertView;
    }
}
