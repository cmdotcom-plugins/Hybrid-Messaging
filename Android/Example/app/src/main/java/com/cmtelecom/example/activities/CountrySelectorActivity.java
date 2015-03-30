package com.cmtelecom.example.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cm.example.R;
import com.cmtelecom.example.adapters.CountrySelectAdapter;
import com.cmtelecom.example.domain.Country;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

public class CountrySelectorActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_selector);

        Bundle bundle = getIntent().getExtras();
        String countryISO = bundle.getString("countryISO");

        CountrySelectAdapter adapter = new CountrySelectAdapter(getApplicationContext(), initializeCountries());
        ListView listView = (ListView) findViewById(R.id.lvCountries);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Country country = (Country)parent.getAdapter().getItem(position);
                Intent intent = new Intent(CountrySelectorActivity.this, RegistrationActivity.class);
                intent.putExtra("countryCode", String.valueOf(country.getCountryCode()));
                intent.putExtra("countryName", country.getCountryName());
                intent.putExtra("countryISO", country.getIsoCountryName());
                startActivity(intent);
            }
        });

        listView.setSelection(adapter.getPositionForCountryISO(countryISO));
    }

    /* Initialize all countries and sort them by country name  */
    private ArrayList<Country> initializeCountries() {

        ArrayList<Country> countries = new ArrayList<>();
        ArrayList<String> isoCountries = new ArrayList<String>(Arrays.asList(Locale.getISOCountries()));
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

        for(String c : isoCountries) {
            countries.add(new Country(c, phoneNumberUtil.getCountryCodeForRegion(c)));
        }
        Collections.sort(countries, new Country.CustomComparator());
        return countries;
    }
}
