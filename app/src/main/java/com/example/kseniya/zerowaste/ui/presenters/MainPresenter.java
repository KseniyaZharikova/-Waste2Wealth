package com.example.kseniya.zerowaste.ui.presenters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.kseniya.zerowaste.data.ReceptionPoint;
import com.example.kseniya.zerowaste.interfaces.MainInterface;
import com.example.kseniya.zerowaste.utils.Constants;
import com.example.kseniya.zerowaste.utils.PermissionUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.kseniya.zerowaste.BuildConfig.BASE_URL_FIREBASE;

public class MainPresenter implements MainInterface.Presenter, LocationListener {
	private MainInterface.View mainView;
	private FusedLocationProviderClient mFusedLocationProviderClient;
	private double lat;
	private double lng;

	private final String TAG = getClass().getSimpleName();

	public MainPresenter() {
	}

	private List<ReceptionPoint> pointList;

	@Override
	public void getPermission(Activity activity) {
		if (PermissionUtils.Companion.isLocationEnable(activity)) {
			getCurrentLocation(activity);
			downloadMarkers();
		}
	}


	@SuppressLint("MissingPermission")
	@Override
	public void getCurrentLocation(Activity activity) {
		mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
		mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
			lat = location.getLatitude();
			lng = location.getLongitude();

		});
	}


	@Override
	public void downloadMarkers() {

		FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
		DatabaseReference databaseReference = firebaseDatabase.getReferenceFromUrl(BASE_URL_FIREBASE + Constants.FIREBASE_RECEPTION_POINTS);
		databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				pointList = new ArrayList<>();
				for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
					ReceptionPoint point = postSnapshot.getValue(ReceptionPoint.class);
					pointList.add(point);
					Log.d(TAG, "onDataChange: " + pointList.size());
				}
				mainView.startActivity(lat,lng,pointList);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {

			}
		});
	}

	@Override
	public void bind(MainInterface.View view) {
		mainView = view;
	}

	@Override
	public void unbind() {
		mainView = null;
	}


	@SuppressLint("MissingPermission")
	public void startLocationUpdates() {

		int INTERVAL_UPDATES = 5000;
		int MINIMUM_INTERVAL_UPDATES = 1000;

		LocationRequest locationRequest = new LocationRequest();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationRequest.setInterval(INTERVAL_UPDATES);
		locationRequest.setFastestInterval(MINIMUM_INTERVAL_UPDATES);
		mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback,
				Looper.myLooper());
	}

	LocationCallback mLocationCallback = new LocationCallback() {
		@Override
		public void onLocationResult(LocationResult locationResult) {
			super.onLocationResult(locationResult);
			Log.d(TAG, "onLocationChanged: " + locationResult.getLastLocation().getLongitude() + " " + locationResult.getLastLocation().getLatitude());
		}
	};

	@Override
	public void onLocationChanged(Location location) {
		mainView.showMarkers(location.getLatitude(), location.getLongitude());

	}
}