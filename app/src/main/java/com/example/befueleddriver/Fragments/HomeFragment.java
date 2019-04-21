package com.example.befueleddriver.Fragments;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.befueleddriver.Adapters.RecyclerViewAdapter;
import com.example.befueleddriver.Models.CustomerRequest;
import com.example.befueleddriver.R;
import com.example.befueleddriver.Utils.ViewWeightAnimationWrapper;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.befueleddriver.Utils.Constants.MAPVIEW_BUNDLE_KEY;


public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener, com.google.android.gms.location.LocationListener {

    private static final String TAG = "HomeFragment";
    private static final int MAP_LAYOUT_STATE_CONTRACTED = 0;
    private static final int MAP_LAYOUT_STATE_EXPANDED = 1;
    private static final int LIMIT = 1;
    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    FirebaseAuth mAuth;
    DatabaseReference myRef;
    ArrayList<String> username, email;
    FrameLayout frameLayout;

    boolean isDriverFound = false;
    ArrayList<CustomerRequest> list;
    TextView request_count;
    private MapView mMapView;
    private GoogleMap mMap;
    private RecyclerViewAdapter adapter;
    private String userID;
    private RecyclerView recyclerView;
    private RelativeLayout mMapContainer;
    private LatLng latLng;
    private FusedLocationProviderClient mFusedLocationClient;
    private int mMapLayoutState = 0;
//    private int radius = 1;
    private int distance = 1;
    private String customerid;
    private ArrayList<String> mkey;
    private String mlocalkey;
    private int i = 0;
    private boolean initrecyclerview = true;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void MakeFrameInvisible(){
        frameLayout.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    public void MakeFrameVisible(){
        frameLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mAuth = FirebaseAuth.getInstance();
//        mkey = new ArrayList<>();
        userID = mAuth.getCurrentUser().getUid();
        mMapView = view.findViewById(R.id.user_list_map);
        mMapContainer = view.findViewById(R.id.map_container);
        Log.d(TAG, "onCreateViewuserid: " + userID);
        view.findViewById(R.id.btn_full_screen_map).setOnClickListener(this);
        myRef = FirebaseDatabase.getInstance().getReference().child("driverAvailable").child(userID);
        // Inflate the layout for this fragment
        request_count = view.findViewById(R.id.text_request_count);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
//        loadAllCustomers();

        initGoogleMap(savedInstanceState);
        initRecyclerView(view);
        return view;
    }



    public void initRecyclerView(final View view) {

        list = new ArrayList<>();
        Log.d(TAG, "initRecyclerView: ");
        mkey = new ArrayList<>();
        recyclerView = view.findViewById(R.id.user_list_recycler_view);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerViewAdapter(getActivity(), list, getActivity().getSupportFragmentManager(), new RecyclerViewAdapter.OnFragmentChangeClickListener() {
            @Override
            public void onFragmentchange(int position) {
                // When the stuff is clicked
                Log.d(TAG, "onFragmentchange: fragment changed  recyclerview is invisible");
                recyclerView.setVisibility(View.INVISIBLE);
                list.get(position);
                Log.d(TAG, "onFragmentchange: list from fragment" + list.get(position).getUserID() + " " + list.get(position).getCarId());
                frameLayout = view.findViewById(R.id.frameLayout_next_fragment);
                frameLayout.setVisibility(View.VISIBLE);
                RequestFragment requestFragment = new RequestFragment();
                Bundle bundle = new Bundle();
                bundle.putString("userId",list.get(position).getUserID());
                bundle.putString("carId",list.get(position).getCarId());
                bundle.putBoolean("isOrderPlace",list.get(position).isIsorderplaced());
                requestFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_next_fragment, requestFragment).commit();


            }
        });
        recyclerView.setAdapter(adapter);
        ref.child("customerRequestInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<CustomerRequest> mList = new ArrayList<>();
                Log.d(TAG, "onDataChange:DATA " + dataSnapshot.toString());
                dataSnapshot.getKey();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    i++;
                    CustomerRequest mRequest = dataSnapshot1.getValue(CustomerRequest.class);

                    Log.d(TAG, "onDataChange:bools " + mkey + " " + mRequest.isIsorderplaced() + " " + mRequest.getUserID());
                    if (mkey.contains(mRequest.getUserID()) && mRequest.isIsorderplaced()) {
//                        list.add(mRequest);
                        mList.add(mRequest);
                        Log.d(TAG, "onDataChange:datasnapshots " + " " + mRequest.getCarId() + " " + mRequest.getUserID());
                        Log.d(TAG, "onDataChange:datasnapshot" + mRequest);
                        Log.d(TAG, "onDataChange:datasnapshot" + "---------------");
                    }
                }
                addReqs(mList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


//        populaterecyclerView();
    }

//    public void populaterecyclerView(){
//
//        ref.child("customerRequestInfo").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                ArrayList<CustomerRequest> mList = new ArrayList<>();
//                Log.d(TAG, "onDataChange:DATA " + dataSnapshot.toString());
//                dataSnapshot.getKey();
//                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                    i++;
//                    CustomerRequest mRequest = dataSnapshot1.getValue(CustomerRequest.class);
//
//                    Log.d(TAG, "onDataChange:bools " + mkey.contains(mRequest.getUserID()) + " " + mRequest.isIsorderplaced() + " " + mRequest.getUserID());
//                    if (mkey.contains(mRequest.getUserID()) && mRequest.isIsorderplaced()) {
////                        list.add(mRequest);
//                        mList.add(mRequest);
//                        Log.d(TAG, "onDataChange:datasnapshots " + " " + mRequest.getCarId() + " " + mRequest.getUserID());
//                        Log.d(TAG, "onDataChange:datasnapshot" + mRequest);
//                        Log.d(TAG, "onDataChange:datasnapshot" + "---------------");
//                    }
//                }
//                addReqs(mList);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void addReqs(ArrayList<CustomerRequest> requests) {
        Log.d(TAG, "addReqs: Adding ALL REQUESTS size= " + requests.size());
        list.clear();
        list.addAll(requests);
        request_count.setText("COMPLETE NEXT (" + list.size() + ")");
        adapter.notifyDataSetChanged();
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
//        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("driverAvailable");
//        GeoFire geoFire = new GeoFire(myRef);
//        geoFire.removeLocation(userID);
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        buildGoogleApiClient();

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);

        mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        mMap.clear();
//        mMap.addMarker(new MarkerOptions().position(latLng).title("Right Now"));

        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driverAvailable");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation(userid, new GeoLocation(latLng.latitude, latLng.longitude), new GeoFire.CompletionListener() {

            @Override
            public void onComplete(String key, DatabaseError error) {
                Log.d(TAG, "keyss:" + key);
            }
        });

        loadAllCustomers();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_full_screen_map: {
                if (mMapLayoutState == MAP_LAYOUT_STATE_CONTRACTED) {
                    mMapLayoutState = MAP_LAYOUT_STATE_EXPANDED;
                    expandMapAnimation();
                } else if (mMapLayoutState == MAP_LAYOUT_STATE_EXPANDED) {
                    mMapLayoutState = MAP_LAYOUT_STATE_CONTRACTED;
                    contractMapAnimation();
                }

                break;
            }

        }
    }


//    private void checkDataChange() {
//        ref.child("customerRequestInfo").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mkey = new ArrayList<>();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void loadAllCustomers() {
        DatabaseReference customerLocation = FirebaseDatabase.getInstance().getReference().child("customerRequest");
        Log.d(TAG, "loadAllCustomersss: " + mLastLocation);
        GeoFire geoFire = new GeoFire(customerLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), distance);
//        mMap.clear();
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String key, final GeoLocation location) {
                Log.d(TAG, "onKeyEntered: " + key);
                FirebaseDatabase.getInstance().getReference("customerRequest").child(key)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                Log.d(TAG, "onDataChange:int "+j++);
//                                checkDataChange();
                                mlocalkey = key;
                                Log.d(TAG, "onDataChange:load " + mlocalkey +" "+mkey.contains(mlocalkey));
                                if (!mkey.contains(mlocalkey)) {
                                    mkey.add(mlocalkey);
//                                    initRecyclerView(getView());
                                    Log.d(TAG, "onDataChange:s " + mkey);

                                }
                                mMap.addMarker(new MarkerOptions().
                                        position(new LatLng(location.latitude, location.longitude))
                                        .flat(true)
                                        .title("Hello"));


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (distance <= LIMIT) {
                    distance++;
                    Log.d(TAG, "onGeoQueryReady: " + mlocalkey);
                    loadAllCustomers();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

        if (initrecyclerview ) {
            initRecyclerView(getView());
            initrecyclerview = false;
        }
    }




//    private void getCustomerLocation(){
//        DatabaseReference customerLocation = FirebaseDatabase.getInstance().getReference().child("customerRequest");
//        GeoFire geoFire = new GeoFire(customerLocation);
//        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()),radius);
//        geoQuery.removeAllListeners();
//        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
//            @Override
//            public void onKeyEntered(String key, GeoLocation location) {
//                if (!isDriverFound){
//                    isDriverFound = true;
//                    customerid = key;
//                    Toast.makeText(getContext(), ""+key, Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onKeyExited(String key) {
//
//            }
//
//            @Override
//            public void onKeyMoved(String key, GeoLocation location) {
//
//            }
//
//            @Override
//            public void onGeoQueryReady() {
//
//                if (!isDriverFound){
//                    radius++;
//                    getCustomerLocation();
//                }
//            }
//
//            @Override
//            public void onGeoQueryError(DatabaseError error) {
//
//            }
//        });
//
//
//
//
//
//
////        geoFire.getLocation(userID, new LocationCallback() {
////            @Override
////            public void onLocationResult(String key, GeoLocation location) {
////
////            }
////
////            @Override
////            public void onCancelled(DatabaseError databaseError) {
////
////            }
////        });
////        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng))
//    }

    private void expandMapAnimation() {
        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                50,
                100);
        mapAnimation.setDuration(800);

        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(recyclerView);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                50,
                0);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
    }

    private void contractMapAnimation() {
        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                100,
                50);
        mapAnimation.setDuration(800);

        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(recyclerView);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                0,
                50);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
    }

}
