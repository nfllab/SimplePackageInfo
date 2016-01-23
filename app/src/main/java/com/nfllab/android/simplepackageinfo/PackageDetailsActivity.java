package com.nfllab.android.simplepackageinfo;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class PackageDetailsActivity extends Activity {

    private void SetupActionBar() {
        // Show the Up button in the action bar.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar ab = getActionBar();
            if (ab != null) {
                ab.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_details);
        SetupActionBar();
        try {
            Intent intent = getIntent();
            String packageName = intent.getStringExtra("package");
            PackageManager pm = getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES
                            | PackageManager.GET_SIGNATURES);
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            TextView tv = (TextView) findViewById(R.id.textPackageName);
            tv.setText(packageName);
            tv = (TextView) findViewById(R.id.textVersionCode);
            tv.setText(Integer.toString(packageInfo.versionCode));
            tv = (TextView) findViewById(R.id.textVersionName);
            tv.setText(packageInfo.versionName);
            tv = (TextView) findViewById(R.id.textSourceDir);
            tv.setText(applicationInfo.sourceDir);
            tv = (TextView) findViewById(R.id.textPublicSourceDir);
            tv.setText(applicationInfo.publicSourceDir);
            Signature[] signatures = packageInfo.signatures;
            tv = (TextView) findViewById(R.id.textNumberOfSignatures);
            tv.setText(Integer.toString(signatures.length));
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
