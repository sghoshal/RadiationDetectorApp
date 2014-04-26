/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.raddetector;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * This shows how to change the camera position for the map.
 */
public class CameraDemoActivity extends FragmentActivity {

	/**
	 * The amount by which to scroll the camera. 
	 * Note that this amount is in raw pixels, not dp
	 * (density-independent pixels).
	 */
	private static final int SCROLL_BY_PX = 100;
	private String latitude = "";
	private String longitude = "";
	private GoogleMap mMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_demo);
		latitude = getIntent().getStringExtra("latitude");
		longitude = getIntent().getStringExtra("longitude");
		System.out.println("CamerActivity Received " + 
				"Lat: " + latitude + " Long: " + longitude);
		setUpMapIfNeeded();
		goToLocationReceivedFromBT();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}

	/**
	 * Set camera position to the latitude and longitude readings
	 * obtained from the device
	 */
	public void goToLocationReceivedFromBT() {
		final CameraPosition BARC =
				new CameraPosition.Builder().target(new LatLng(
						Double.parseDouble(latitude), Double.parseDouble(longitude)))
						.zoom(15.5f)
						.bearing(0)
						.tilt(25)
						.build();

		if (!checkReady()) {
			return;
		}

		changeCamera(CameraUpdateFactory.newCameraPosition(BARC), 
				new CancelableCallback() {
			
			@Override
			public void onFinish() {
				Toast.makeText(getBaseContext(), 
						"Animation to Location complete", 
						Toast.LENGTH_SHORT).show();
				
				mMap.addMarker(new MarkerOptions().position(
						new LatLng(Double.parseDouble(latitude), 
								Double.parseDouble(longitude))).title("Marker"));
			}

			@Override
			public void onCancel() {
				Toast.makeText(getBaseContext(), 
						"Animation to Sydney canceled", 
						Toast.LENGTH_SHORT).show();
				
				mMap.addMarker(new MarkerOptions().position(
						new LatLng(Double.parseDouble(latitude), 
								Double.parseDouble(longitude))).title("Marker"));
			}
		});
	}

	private void setUpMapIfNeeded() {
		if (mMap == null) {
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map))
					.getMap();
			
			if (mMap != null) {
				setUpMap();
			}
		}
	}

	private void setUpMap() {
		// We will provide our own zoom controls.
		mMap.getUiSettings().setZoomControlsEnabled(false);
		goToLocationReceivedFromBT();
	}

	/**
	 * When the map is not ready the CameraUpdateFactory cannot be used. 
	 * This should be called on all entry points that call 
	 * methods on the Google Maps API.
	 */
	private boolean checkReady() {
		if (mMap == null) {
			Toast.makeText(this, R.string.map_not_ready, 
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * Called when the stop button is clicked.
	 */
	public void onStopAnimation(View view) {
		if (!checkReady()) {
			return;
		}
		mMap.stopAnimation();
	}

	/**
	 * Called when the zoom in button (the one with the +) is clicked.
	 */
	public void onZoomIn(View view) {
		if (!checkReady()) {
			return;
		}
		changeCamera(CameraUpdateFactory.zoomIn());
	}

	/**
	 * Called when the zoom out button (the one with the -) is clicked.
	 */
	public void onZoomOut(View view) {
		if (!checkReady()) {
			return;
		}
		changeCamera(CameraUpdateFactory.zoomOut());
	}

	/**
	 * Called when the tilt more button (the one with the /) is clicked.
	 */
	public void onTiltMore(View view) {
		CameraPosition currentCameraPosition = mMap.getCameraPosition();
		float currentTilt = currentCameraPosition.tilt;
		float newTilt = currentTilt + 10;

		newTilt = (newTilt > 90) ? 90 : newTilt;

		CameraPosition cameraPosition = new CameraPosition.Builder(currentCameraPosition)
		.tilt(newTilt).build();

		changeCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	/**
	 * Called when the tilt less button (the one with the \) is clicked.
	 */
	public void onTiltLess(View view) {
		CameraPosition currentCameraPosition = mMap.getCameraPosition();

		float currentTilt = currentCameraPosition.tilt;

		float newTilt = currentTilt - 10;
		newTilt = (newTilt > 0) ? newTilt : 0;

		CameraPosition cameraPosition = new CameraPosition.Builder(currentCameraPosition)
		.tilt(newTilt).build();

		changeCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	/**
	 * Called when the left arrow button is clicked. 
	 * This causes the camera to move to the left
	 */
	public void onScrollLeft(View view) {
		if (!checkReady()) {
			return;
		}
		changeCamera(CameraUpdateFactory.scrollBy(-SCROLL_BY_PX, 0));
	}

	/**
	 * Called when the right arrow button is clicked. 
	 * This causes the camera to move to the right.
	 */
	public void onScrollRight(View view) {
		if (!checkReady()) {
			return;
		}
		changeCamera(CameraUpdateFactory.scrollBy(SCROLL_BY_PX, 0));
	}

	/**
	 * Called when the up arrow button is clicked. 
	 * This causes the camera to move up.
	 */
	public void onScrollUp(View view) {
		if (!checkReady()) {
			return;
		}
		changeCamera(CameraUpdateFactory.scrollBy(0, -SCROLL_BY_PX));
	}

	/**
	 * Called when the down arrow button is clicked. 
	 * This causes the camera to move down.
	 */
	public void onScrollDown(View view) {
		if (!checkReady()) {
			return;
		}
		changeCamera(CameraUpdateFactory.scrollBy(0, SCROLL_BY_PX));
	}

	private void changeCamera(CameraUpdate update) {
		changeCamera(update, null);
	}

	/**
	 * Change the camera position by moving or animating the camera 
	 * depending on the state of the animate toggle button.
	 */
	private void changeCamera(CameraUpdate update, CancelableCallback callback) {
		boolean animated = ((CompoundButton) findViewById(R.id.animate)).isChecked();
		if (animated) {
			mMap.animateCamera(update, callback);
		} else {
			mMap.moveCamera(update);
		}
	}
}
