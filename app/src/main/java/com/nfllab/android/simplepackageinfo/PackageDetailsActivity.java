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

import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

public class PackageDetailsActivity extends Activity {

    private String packageName;

    private void SetupActionBar() {
        // Show the Up button in the action bar.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar ab = getActionBar();
            if (ab != null) {
                ab.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private CharSequence getYesOrNo(boolean isYes) {
        return isYes ? getResources().getText(R.string.yes) : getResources().getText(R.string.no);
    }

    private CharSequence getYesOrNo(int isYes) {
        return getYesOrNo(isYes != 0);
    }

    private String getIntString(int a) {
        return String.valueOf(a);
    }

    private String getTimeString(long timeMillis) {
        Date d = new Date(timeMillis);
        return d.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_details);
        SetupActionBar();
        try {
            Intent intent = getIntent();
            packageName = intent.getStringExtra("package");
            PackageManager pm = getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES
                            | PackageManager.GET_SIGNATURES);
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            TextView tv = (TextView) findViewById(R.id.textPackageName);
            tv.setText(packageName);
            tv = (TextView) findViewById(R.id.textVersionCode);
            tv.setText(getIntString(packageInfo.versionCode));
            tv = (TextView) findViewById(R.id.textVersionName);
            tv.setText(packageInfo.versionName);
            tv = (TextView) findViewById(R.id.textSourceDir);
            tv.setText(applicationInfo.sourceDir);
            tv = (TextView) findViewById(R.id.textPublicSourceDir);
            tv.setText(applicationInfo.publicSourceDir);
            tv = (TextView) findViewById(R.id.textTargetSdkVersion);
            tv.setText(getIntString(applicationInfo.targetSdkVersion));
            tv = (TextView) findViewById(R.id.textEnabled);
            tv.setText(getYesOrNo(applicationInfo.enabled));
            tv = (TextView) findViewById(R.id.textSystemApp);
            tv.setText(getYesOrNo(applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM));
            tv = (TextView) findViewById(R.id.textUpdatedSystemApp);
            tv.setText(getYesOrNo(applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP));
            if (Build.VERSION.SDK_INT >= 9) {
                tv = (TextView) findViewById(R.id.textFirstInstallTime);
                tv.setText(getTimeString(packageInfo.firstInstallTime));
                tv = (TextView) findViewById(R.id.textLastUpdateTime);
                tv.setText(getTimeString(packageInfo.lastUpdateTime));
            }
            if (Build.VERSION.SDK_INT >= 5) {
                tv = (TextView) findViewById(R.id.textInstallerPackageName);
                String ipn = pm.getInstallerPackageName(packageName);
                if (ipn == null) {
                    ipn = "(null)";
                }
                tv.setText(ipn);
            }
            Signature[] signatures = packageInfo.signatures;
            tv = (TextView) findViewById(R.id.textNumberOfSignatures);
            tv.setText(getIntString(signatures.length));
            CertificateFactory X509Factory = CertificateFactory
                    .getInstance("X509");
            X509Certificate cert = (X509Certificate) X509Factory
                    .generateCertificate(new ByteArrayInputStream(signatures[0]
                            .toByteArray()));
            tv = (TextView) findViewById(R.id.textSignature);
            tv.setText(cert.toString());
        } catch (NullPointerException e) {
            Toast.makeText(this, "NullPointerException", Toast.LENGTH_LONG)
                    .show();
        } catch (NameNotFoundException e) {
            Toast.makeText(this, "NameNotFoundException", Toast.LENGTH_LONG)
                    .show();
        } catch (CertificateException e) {
            Toast.makeText(this, "CertificateException", Toast.LENGTH_LONG)
                    .show();
        }
    }

    public void onButtonClick(View v) {
        // https://stackoverflow.com/questions/4421527/how-can-i-start-android-application-info-screen-programmatically
        if (Build.VERSION.SDK_INT >= 9) {
            try {
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                // just do nothing
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                /* This should go back to the already running parent activity, without restarting
                 * it. We don't want the filter cleared. */
                Intent upIntent = new Intent(this, PackageListActivity.class);
                upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(upIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
