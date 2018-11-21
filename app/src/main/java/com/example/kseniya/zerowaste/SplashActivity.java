package com.example.kseniya.zerowaste;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.kseniya.zerowaste.utils.PermissionUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;


public class SplashActivity extends AppCompatActivity {
	private FusedLocationProviderClient locationProviderClient;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		if (PermissionUtils.Companion.isLocationEnable(this)) {
			getCurrentLocation();
		}else {
			Toast.makeText(this, "FAILED", Toast.LENGTH_LONG).show();
		}


	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1){
			for (int result: grantResults){
				if (result == PackageManager.PERMISSION_GRANTED){
					getCurrentLocation();
				}else {
					startActivity(new Intent(this, MainActivity.class));
					finish();
				}
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@SuppressLint("MissingPermission")
	private void getCurrentLocation() {
		locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
		locationProviderClient.getLastLocation().addOnSuccessListener(this::startActivity);
	}

	private void startActivity(Location location) {
		Log.d("SplashActivity", "onCreate: " + location.getLatitude() + " " + location.getLongitude());

		startActivity(new Intent(this, MainActivity.class)
				.putExtra("lat", location.getLatitude())
				.putExtra("lng", location.getLongitude()));

		finish();
	}

}