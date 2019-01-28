package kristof.ccm.ccm2018goooglemap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

public class ActivitePrincipale extends AppCompatActivity implements LocationListener {

    private static final int ID_DEMANDE_PERMISSION=123;
    private LocationManager monLocationManager;
    private GoogleMap maGoogleMap;
    private MapFragment monMapFragment;

    private Marker myMarker;
    private Map<String, Marker> friendsMarkers = new HashMap<String, Marker>();

    private double latitude;
    private double longitude;
    private String name;
    private String phoneNumber;
    private String userId;

    private String otherName;

    private ViewGroup infoWindow;
    private TextView infoTitle;
    private Button telephone;
    private Button sms;
    private OnInfoWindowElemTouchListener infoButtonListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activite_principale);
        Intent unIntent = getIntent();
        Bundle leBundle = unIntent.getExtras();
        userId = leBundle.getString("userId");
        name = leBundle.getString("name");
        phoneNumber = leBundle.getString("phoneNumber");

        FragmentManager monFragmentManager=getFragmentManager();
        monMapFragment=(MapFragment)monFragmentManager.findFragmentById(R.id.mamap);

        this.infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.custom_infowindow, null);

        this.infoTitle = infoWindow.findViewById(R.id.nameTxt);
        this.telephone = infoWindow.findViewById(R.id.btnOne);
        this.sms = infoWindow.findViewById(R.id.btnOne2);

        this.infoButtonListener = new OnInfoWindowElemTouchListener(telephone, getResources().getDrawable(R.drawable.common_google_signin_btn_icon_dark), getResources().getDrawable(R.drawable.common_google_signin_btn_icon_dark)){
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here we can perform some action triggered after clicking the button
                Toast.makeText(ActivitePrincipale.this, "click on button 1", Toast.LENGTH_SHORT).show();
            }
        };
        this.telephone.setOnTouchListener(infoButtonListener);

        this.infoButtonListener = new OnInfoWindowElemTouchListener(sms, getResources().getDrawable(R.drawable.common_google_signin_btn_icon_dark),getResources().getDrawable(R.drawable.common_google_signin_btn_icon_dark)){
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                Toast.makeText(ActivitePrincipale.this, "click on button 2", Toast.LENGTH_LONG).show();
            }
        };
        this.sms.setOnTouchListener(infoButtonListener);

        verifierPermission();


    }

    private void verifierPermission(){
        if ( !(
                ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED
        &&
                ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED))
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}
                    , ID_DEMANDE_PERMISSION);
            return;
        }
        monLocationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        monLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
        monLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, this);
        chargerMap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        verifierPermission();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if( requestCode == ID_DEMANDE_PERMISSION){

        }
    }

    @Override
    public void onLocationChanged(final Location location) {
        latitude=location.getLatitude();
        longitude=location.getLongitude();
        LatLng myLocation = new LatLng(latitude, longitude);
        if(maGoogleMap != null){
            addMarker(myLocation, "Moi", "moi");
            final CollectionReference localisationCollection = FirebaseFirestore.getInstance().collection("localisation");
            localisationCollection.whereEqualTo("id_user", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Localisation localisation = new Localisation(userId, location.getLatitude(), location.getLongitude());
                            localisationCollection.document(document.getId()).set(localisation, SetOptions.merge());
                        }
                    }
                }
            });
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @SuppressLint("ClickableViewAccessibility")
    private void chargerMap(){
        monMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public String toString() {
                return "$classname{}";
            }

            @Override
            public void onMapReady(GoogleMap googleMap) {
                maGoogleMap=googleMap;
                LatLng myLocation = new LatLng(latitude, longitude);
                maGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                maGoogleMap.getUiSettings().setZoomControlsEnabled(true);
                maGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 8.0f));
                getOthersUsersLocations();
                maGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        // Setting up the infoWindow with current's marker info
                        infoTitle.setText(marker.getTitle());
                        infoButtonListener.setMarker(marker);

                        // We must call this to set the current marker and infoWindow references
                        // to the MapWrapperLayout
                        //.setMarkerWithInfoWindow(marker, infoWindow);
                        return infoWindow;
                    }
                });
            }
        });
    }

    private void getOthersUsersLocations() {
        CollectionReference localisationCollection = FirebaseFirestore.getInstance().collection("localisation");
        localisationCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null) return;
                assert queryDocumentSnapshots != null;
                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                    if (!userId.matches(Objects.requireNonNull(documentChange.getDocument().get("id_user").toString()))) {
                        LatLng otherLocation = new LatLng(documentChange.getDocument().getDouble("latitude"), documentChange.getDocument().getDouble("longitude"));
                        Log.d("GEO", "Changement de localistation");
                        CollectionReference userCollection = FirebaseFirestore.getInstance().collection("user");
                        userCollection.document(Objects.requireNonNull(documentChange.getDocument().getString("id_user"))).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    otherName = document.getString("name");
                                    Log.d("GEO", otherName);
                                }
                            }
                        });
                        if(otherName != null) addMarker(otherLocation, otherName, "ami");
                    }
                }
            }
        });

    }

    private void addMarker(LatLng location, String name, String typeRelation){
        if(location != null && maGoogleMap != null){
            switch (typeRelation) {
                case "moi":
                    if(myMarker != null) myMarker.remove();
                    myMarker = maGoogleMap.addMarker(new MarkerOptions().position(location)
                            .title(name)
                            .snippet("tel : "+phoneNumber)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    );
                    break;
                case "ami":
                    if(friendsMarkers.get(name) != null) friendsMarkers.get(name).remove();
                    friendsMarkers.put(name, maGoogleMap.addMarker(new MarkerOptions().position(location).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))));
            }
        }
    }

    public void test(View view) {
        Log.d("GEO","clic sur un bt");
    }
}
