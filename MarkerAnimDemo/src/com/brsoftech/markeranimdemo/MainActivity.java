package com.brsoftech.markeranimdemo;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.gesture.GestureOverlayView;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;

@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity implements
		OnConnectionFailedListener, ConnectionCallbacks {

	private GoogleMap googleMap;

	private GoogleApiClient mGoogleApiClient;

	private Location mLastLocation;

	private LatLng currentlatln = null;

	public static boolean markermovedtemp = false;
	public static boolean markermoved = false;
	private float mOrientation = 0;
	// private float angle = 0;

	private Location currentlocation;
	private Location updatedlocation;
	private int shadeColor;
	private int strokeColor;
	Circle mCircle;
	private CameraPosition myPosition;

	public static int c = 0;
	public static Handler overhand;
	public static GroundOverlay imageOverlay;

	ImageView myfiximg, mydustimg, myfiximgperson;
	RelativeLayout myfixlay;
	GestureOverlayView gol;
	int camera_flag = 0;
	int gauster_flag = 0;
	Animation slidebounce, fadeout;

	float intialY = 0;
	float imageinttialY = 0;
	float imageinttialX = 0;
	TranslateAnimation moveanim, bounceanim;
	long intialtime = 0;

	public long gethightofY() {
		long y = 0;
		long nowtime = System.currentTimeMillis();
		printlog("nowtime= " + nowtime);
		long difftime = nowtime - intialtime;
		printlog("difftime= " + difftime);
		if (difftime >= 1000) {
			y = 40;
		} else {
			y = (long) (0.04 * difftime);
		}
		return y;

	}

	public void startmoveanimation() {
		moveanim = new TranslateAnimation(0, 0, 0, -40);
		moveanim.setDuration(1000);
		moveanim.setFillAfter(true);
		moveanim.setInterpolator(new LinearInterpolator());

		myfixlay.startAnimation(moveanim);

	}

	public void startbounceanim() {
		moveanim.cancel();
		float diffY = gethightofY();
		printlog("diffY= " + diffY);
		bounceanim = new TranslateAnimation(0, 0, -diffY, 0);
		bounceanim.setDuration(1000);
		bounceanim.setFillAfter(true);
		bounceanim.setInterpolator(new BounceInterpolator());
		myfixlay.startAnimation(bounceanim);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		gol = new GestureOverlayView(MainActivity.this);
		View v = LayoutInflater.from(MainActivity.this).inflate(
				R.layout.activity_main, null);
		gol.addView(v);
		gol.setGestureVisible(false);
		setContentView(gol);

		gol.addOnGestureListener(new GestureOverlayView.OnGestureListener() {

			@SuppressLint("NewApi")
			@Override
			public void onGestureStarted(GestureOverlayView overlay,
					MotionEvent event) {
				switch (event.getAction()) {
				// camera_flag = 1;
				case MotionEvent.ACTION_DOWN:
					printlog("action down");
					intialY = event.getY();
					imageinttialY = myfixlay.getY();
					imageinttialX = myfixlay.getX();
					printlog("action move imageinttialY= " + imageinttialY);
					intialtime = System.currentTimeMillis();
					printlog("intialtime= " + intialtime);
					startmoveanimation();

					break;

				}

			}

			@SuppressLint("NewApi")
			@Override
			public void onGestureEnded(GestureOverlayView overlay,
					MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_UP) {

					startanimation();

				}
			}

			@Override
			public void onGestureCancelled(GestureOverlayView overlay,
					MotionEvent event) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGesture(GestureOverlayView overlay, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:

					break;
				}

			}
		});
		myfiximg = (ImageView) findViewById(R.id.myfiximg);
		mydustimg = (ImageView) findViewById(R.id.mydustimg);
		myfiximgperson = (ImageView) findViewById(R.id.myfiximgperson);
		myfixlay = (RelativeLayout) findViewById(R.id.myfixlay);
		buildGoogleApiClient();
		try {
			// Loading map
			initilizeMap();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();
		mGoogleApiClient.connect();
	}

	@SuppressLint("NewApi")
	public void startanimation() {

		startbounceanim();
		fadeout = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.fadeout);
		mydustimg.startAnimation(fadeout);
		mydustimg.setVisibility(View.VISIBLE);
		final ObjectAnimator RotateY = ObjectAnimator.ofFloat(myfiximgperson,
				"rotationY", 0, 360);
		RotateY.setStartDelay(1000);
		RotateY.setDuration(500);
		RotateY.start();
	}

	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.mymap)).getMap();
			googleMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {

				@Override
				public void onMapLoaded() {
					// TODO Auto-generated method stub
					printlog("map loaded successfully");

					markermoved = false;
					setmycurrentlocation();

				}

			});

			googleMap.setOnCameraChangeListener(new OnCameraChangeListener() {

				@Override
				public void onCameraChange(CameraPosition arg0) {
					// TODO Auto-generated method stub

				}
			});

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	private void setmycurrentlocation() {
		// TODO Auto-generated method stub
		if (currentlatln != null) {
			printlog("currentlatln not null");

			myPosition = new CameraPosition.Builder().target(currentlatln)
					.bearing(0).zoom(17).build();

			googleMap.animateCamera(
					CameraUpdateFactory.newCameraPosition(myPosition),
					new CancelableCallback() {

						@Override
						public void onFinish() {
							// TODO Auto-generated method stub
							camera_flag = 0;
						}

						@Override
						public void onCancel() {
							// TODO Auto-generated method stub
							camera_flag = 0;
						}
					});

			printlog("currentlatln set on map");

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// setupSensorManager();
		initilizeMap();

	}

	public void showToast(String msg) {
		Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
	}

	public static void printlog(String msg) {
		Log.d("LOCATIONDEMO", msg);
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		printlog("onConnected call");

		mLastLocation = LocationServices.FusedLocationApi
				.getLastLocation(mGoogleApiClient);
		if (mLastLocation != null) {
			currentlocation = mLastLocation;

			currentlatln = new LatLng(mLastLocation.getLatitude(),
					mLastLocation.getLongitude());

			printlog(String.valueOf(mLastLocation.getLatitude()));
			printlog(String.valueOf(mLastLocation.getLongitude()));
			printlog(String.valueOf(mLastLocation.getAccuracy()));

		}

	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		printlog("onConnectionSuspended call");
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		printlog("onConnectionFailed call");
	}

}
