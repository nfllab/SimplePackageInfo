/*
 * Copyright 2013,2016 Ferenc Laszlo Nagy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nfllab.android.simplepackageinfo;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

public class PackageListActivity extends ListActivity {

    private String[] packageNames;
    private ArrayAdapter<String> adapter;

    private void updateList(String filter) {
        filter = filter.toLowerCase();
        adapter.clear();
        for (String pn : packageNames) {
            if (pn.toLowerCase().contains(filter)) {
                adapter.add(pn);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_package_list);

        PackageManager pm = getPackageManager();
        List<PackageInfo> packages =
                pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        packageNames = new String[packages.size()];
        for (int i = 0; i < packages.size(); i++) {
            packageNames[i] = packages.get(i).packageName;
        }
        Arrays.sort(packageNames);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        updateList("");
        setListAdapter(adapter);

        EditText editText = (EditText) findViewById(R.id.filter);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(this, PackageDetailsActivity.class);
        intent.putExtra("package", adapter.getItem(position));
        startActivity(intent);
    }
}
