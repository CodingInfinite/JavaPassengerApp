package spartons.com.javapassengerapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import spartons.com.javapassengerapp.collection.MarkerCollection;
import spartons.com.javapassengerapp.helpers.FirebaseEventListenerHelper;
import spartons.com.javapassengerapp.helpers.GoogleMapHelper;
import spartons.com.javapassengerapp.helpers.MarkerAnimationHelper;
import spartons.com.javapassengerapp.helpers.UiHelper;
import spartons.com.javapassengerapp.interfaces.FirebaseDriverListener;
import spartons.com.javapassengerapp.interfaces.LatLngInterpolator;
import spartons.com.javapassengerapp.model.Driver;

public class MainActivity extends AppCompatActivity implements FirebaseDriverListener {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2161;
    private static final String ONLINE_DRIVERS = "online_drivers";

    private final GoogleMapHelper googleMapHelper = new GoogleMapHelper();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(ONLINE_DRIVERS);

    private GoogleMap googleMap;
    private LocationRequest locationRequest;
    private UiHelper uiHelper;
    private FirebaseEventListenerHelper firebaseEventListenerHelper;
    private FusedLocationProviderClient locationProviderClient;

    private TextView totalOnlineDrivers;

    private boolean locationFlag = true;

    private LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location location = locationResult.getLastLocation();
            if (location == null) return;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (locationFlag) {
                locationFlag = false;
                animateCamera(latLng);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.supportMap);
        uiHelper = new UiHelper(this);
        assert mapFragment != null;
        mapFragment.getMapAsync(googleMap -> this.googleMap = googleMap);
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = uiHelper.getLocationRequest();
        if (!uiHelper.isPlayServicesAvailable()) {
            Toast.makeText(this, "Play Services did not installed!", Toast.LENGTH_SHORT).show();
            finish();
        } else requestLocationUpdates();
        totalOnlineDrivers = findViewById(R.id.totalOnlineDrivers);
        firebaseEventListenerHelper = new FirebaseEventListenerHelper(this);
        databaseReference.addChildEventListener(firebaseEventListenerHelper);
    }

    private void animateCamera(LatLng latLng) {
        CameraUpdate cameraUpdate = googleMapHelper.buildCameraUpdate(latLng);
        googleMap.animateCamera(cameraUpdate);
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {
        if (!uiHelper.isHaveLocationPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
        if (uiHelper.isLocationProviderEnabled())
            uiHelper.showPositiveDialogWithListener(this, getResources().getString(R.string.need_location), getResources().getString(R.string.location_content), () -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)), "Turn On", false);
        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            int value = grantResults[0];
            if (value == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Location Permission denied", Toast.LENGTH_SHORT).show();
                finish();
            } else if (value == PackageManager.PERMISSION_GRANTED) requestLocationUpdates();
        }
    }

    @Override
    public void onDriverAdded(Driver driver) {
        MarkerOptions markerOptions = googleMapHelper.getDriverMarkerOptions(new LatLng(driver.getLat(), driver.getLng()));
        Marker marker = googleMap.addMarker(markerOptions);
        marker.setTag(driver.getDriverId());
        MarkerCollection.insertMarker(marker);
        totalOnlineDrivers.setText(getResources()
                .getString(R.string.total_online_drivers)
                .concat(" ")
                .concat(String
                        .valueOf(MarkerCollection
                                .allMarkers()
                                .size())));
    }

    @Override
    public void onDriverRemoved(Driver driver) {
        MarkerCollection.removeMarker(driver.getDriverId());
        totalOnlineDrivers.setText(getResources()
                .getString(R.string.total_online_drivers)
                .concat(" ")
                .concat(String
                        .valueOf(MarkerCollection
                                .allMarkers()
                                .size())));
    }

    @Override
    public void onDriverUpdated(Driver driver) {
        Marker marker = MarkerCollection.getMarker(driver.getDriverId());
        assert marker != null;
        MarkerAnimationHelper.animateMarkerToGB(marker, new LatLng(driver.getLat(), driver.getLng()), new LatLngInterpolator.Spherical());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(firebaseEventListenerHelper);
        locationProviderClient.removeLocationUpdates(locationCallback);
        MarkerCollection.clearMarkers();
    }
}
