package com.nfllab.android.simplepackageinfo;

import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PackageListActivity extends ListActivity {

	private List<PackageInfo> packages;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PackageManager pm = getPackageManager();
		packages = pm
				.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		String[] values = new String[packages.size()];
		for (int i = 0; i < packages.size(); i++) {
			values[i] = packages.get(i).packageName;
		}
		// the package manager used to return the packages in alphabetical order,
		// but it was changed some time ago
		Arrays.sort(values);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this, PackageDetailsActivity.class);
		intent.putExtra("package", packages.get(position).packageName);
		startActivity(intent);
	}
}
