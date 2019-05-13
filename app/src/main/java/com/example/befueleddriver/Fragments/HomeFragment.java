package com.example.befueleddriver.Fragments;

import android.Manifest;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.befueleddriver.Adapters.RecyclerViewAdapter;
import com.example.befueleddriver.Models.CustomInfoWindow;
import com.example.befueleddriver.Models.CustomerCarInfo;
import com.example.befueleddriver.Models.CustomerRequest;
import com.example.befueleddriver.R;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.befueleddriver.Utils.Constants.MAPVIEW_BUNDLE_KEY;
import static com.example.befueleddriver.Utils.Constants.mLastLocation;


public class HomeFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener,
        com.google.android.gms.location.LocationListener, RecyclerViewAdapter.UserListRecyclerClickListner, RoutingListener {

    private static final String TAG = "HomeFragment";
    private static final int MAP_LAYOUT_STATE_CONTRACTED = 0;
    private static final int MAP_LAYOUT_STATE_EXPANDED = 1;
    private static final int LIMIT = 1;
    private static final int[] COLORS = new int[]{R.color.White};
    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    public String cancelKey = "";
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    FirebaseAuth mAuth;
    DatabaseReference myRef;
    ArrayList<String> username, email;
    FrameLayout frameLayout;
    ArrayList<CustomerRequest> list;
    TextView request_count;
    int j = 0;
    boolean isDriverFound = false;
    int k = 1;
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
    private ImageView mNotify;
    private double mDriverdistance = 1;
    private String mDriverId;
    private boolean mIsDriverNeed = false;
    private List<Polyline> polylines;
    private LatLng otherDriverLocation;
    private ArrayList<Route> mRoute;
    private boolean checksingle = true;
    private boolean isNotify = false;
    private Button btnGetDirection;
    private LatLng requestedDriverLocation;

    public HomeFragment() {
        // Required empty public constructor
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

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isNotify = getArguments().getBoolean("getDirection");
        }

    }

    public void MakeFrameInvisible() {
        frameLayout.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    public void MakeFrameVisible() {
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
//        mService = Constants.getFCMService();
        mMapView = view.findViewById(R.id.user_list_map);
        polylines = new ArrayList<>();
        mMapContainer = view.findViewById(R.id.map_container);
        Log.d(TAG, "onCreateViewuserid: " + userID);
        btnGetDirection = view.findViewById(R.id.btn_full_screen_map);
        btnGetDirection.setVisibility(View.GONE);
        btnGetDirection.setOnClickListener(this);
        myRef = FirebaseDatabase.getInstance().getReference().child("driverAvailable").child(userID);
        // Inflate the layout for this fragment
        request_count = view.findViewById(R.id.text_request_count);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

//        loadAllCustomers();


        mNotify = view.findViewById(R.id.ic_notify);
        mNotify.setOnClickListener(this);
        initGoogleMap(savedInstanceState);
//        initRecyclerView(view);
//        initRequestNotifier();
        return view;
    }

    private void initRequestNotifier() {
        ValueEventListener mListener;

        FirebaseDatabase.getInstance().getReference().child("Notifiers").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "onDataChange: datasnapshot contains => " + dataSnapshot.toString());
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        btnGetDirection.setVisibility(View.VISIBLE);

//                        mListener = new ValueEventListener(){
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        };


                        requestedDriverLocation = getLatLngFromString(dataSnapshot1.getValue(String.class));
                        cancelKey = dataSnapshot1.getKey();
//                        mMap.addMarker(new MarkerOptions().
//                                position(new LatLng(requestedDriverLocation.latitude, requestedDriverLocation.longitude))
//                                .flat(true)
//                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
//                                .title("Driver"));

                        getRouteToMarker(requestedDriverLocation);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("NearestDriverCall").child(userID);
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.d(TAG, "onDataChange:datasnapshot " + dataSnapshot);
//                btnGetDirection.setVisibility(View.VISIBLE);
//                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                    RequestDriver requestDriver = dataSnapshot.getValue(RequestDriver.class);
//                    Log.d(TAG, "onDataChange:request" + requestDriver.getUserId() + "" + requestDriver.getDriverRequestLat());
////                    if (requestDriver.getUserId().equals(userID)) {
////                        Log.d(TAG, "onKeyEnterednearestdriver: key " + requestDriver + " userId " + userID);
////
//
//                    mMap.addMarker(new MarkerOptions().
//                            position(new LatLng(requestDriver.getDriverRequstedLat(), requestDriver.getDriverRequestedLng()))
//                            .flat(true)
//                            .title("Driver"));
//                    mMap.addMarker(new MarkerOptions().
//                            position(new LatLng(requestDriver.getDriverRequestLat(), requestDriver.getDriverRequestLng()))
//                            .flat(true)
//                            .title("Driver"));
////                        otherDriverLocation = new LatLng(requestDriver.getDriverRequestLat(), requestDriver.getDriverRequestLng());
////                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

//        GeoFire geoFire = new GeoFire(ref);
//        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 30);
//        geoQuery.removeAllListeners();
//        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
//            @Override
//            public void onKeyEntered(String key, GeoLocation location) {
//                Log.d(TAG, "onKeyEnterednearestdriver: key " + key + " userId " + userID);
//                if (key.equals(userID)) {
//                    Log.d(TAG, "onKeyEnterednearestdriver: key " + key + " userId " + userID);
////                    if ()
//                    btnGetDirection.setVisibility(View.VISIBLE);
//                    mMap.addMarker(new MarkerOptions().
//                            position(new LatLng(location.latitude,location.longitude))
//                            .flat(true)
//                            .title("Driver"));
//                    otherDriverLocation = new LatLng(location.latitude, location.longitude);
////                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DriverNotifyFragment()).commit();
//
//
//
//                }
//
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
//            }
//
//            @Override
//            public void onGeoQueryError(DatabaseError error) {
//
//            }
//        });

//        geoFire.getLocation(userID, new LocationCallback() {
//            @Override
//            public void onLocationResult(String key, GeoLocation location) {
//                if (location!=null){
//                    Log.d(TAG, "onLocationResultNearestDriver: "+String.format("The location for key %s is [%f,%f]", key, location.latitude, location.longitude));
//                }
//                else {
//                    Log.d(TAG, "onLocationResultNearestDriver: "+String.format("There is no location for key %s in GeoFire", key));
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d(TAG, "onCancelled: "+"There was an error getting the GeoFire location: " + databaseError);
//            }
//        });
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
                bundle.putString("userId", list.get(position).getUserID());
                bundle.putString("carId", list.get(position).getCarId());
                bundle.putBoolean("isOrderPlace", list.get(position).isIsorderplaced());
                bundle.putString("quantity", list.get(position).getFuelQty());

                requestFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_next_fragment, requestFragment).commit();


            }
        }, this);
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

                        Log.d(TAG, "onDataChange:datasnapshots " + " " + mRequest.getFuelQty() + " " + mRequest.getUserID());
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

        mMap.setOnInfoWindowClickListener(this);
        mMap.setMyLocationEnabled(true);
        mMap.setInfoWindowAdapter(new CustomInfoWindow(getContext()));
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

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        mMap.clear();
//        mMap.addMarker(new MarkerOptions().position(latLng).title("Right Now"));

//        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driverAvailable");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation(userID, new GeoLocation(latLng.latitude, latLng.longitude), new GeoFire.CompletionListener() {

            @Override
            public void onComplete(String key, DatabaseError error) {
                Log.d(TAG, "keyss:" + key);
            }
        });

        loadAllCustomers();
        if (checksingle) {
            initRequestNotifier();
            checksingle = false;
        }
//        if (mIsDriverNeed) {
//            callNearestDriver();
//            mIsDriverNeed = false;
//            isDriverFound = false;
//        }

        if (isNotify) {
            onRoutingSuccess(mRoute, 0);
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
//    IFCMService mService;
//    private void sendRequestToDriver(String mDriverId) {
//
//        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
//        tokens.orderByKey().equalTo(mDriverId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot postSnapShot: dataSnapshot.getChildren()){
//                    Token token = postSnapShot.getValue(Token.class);
//                    String json_lat_lng = new Gson().toJson(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()));
//                    Data data = new Data("Driver",json_lat_lng);
//                    Sender content = new Sender(data,token.getToken());
//
//                    mService.sendMessage(sender)
//                            .enqueue(new Callback<FCMResponse>() {
//                                @Override
//                                public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
//                                    if (response.body().success == 1){
//                                        Toast.makeText(getContext(), "Request Sent", Toast.LENGTH_SHORT).show();
//                                    }
//                                    else {
//                                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<FCMResponse> call, Throwable t) {
//                                    Log.d(TAG, "onFailure: "+t.getMessage());
//                                }
//                            });
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_full_screen_map:
//                if (mMapLayoutState == MAP_LAYOUT_STATE_CONTRACTED) {
//                    mMapLayoutState = MAP_LAYOUT_STATE_EXPANDED;
//                    expandMapAnimation();
//                } else if (mMapLayoutState == MAP_LAYOUT_STATE_EXPANDED) {
//                    mMapLayoutState = MAP_LAYOUT_STATE_CONTRACTED;
//                    contractMapAnimation();
//                }
//                break;

                erasePolylines();
                mMap.clear();
                FirebaseDatabase.getInstance().getReference().child("Notifiers").child(FirebaseAuth.getInstance().getUid()).removeValue();
                btnGetDirection.setVisibility(View.GONE);

//                if (btnGetDirection.getText().equals("Get Directions")) {
//
//                    getRouteToMarker(otherDriverLocation);
//                    btnGetDirection.setText("Cancel");
//                } else if (btnGetDirection.getText().equals("Cancel")) {
//                    erasePolylines();
//                    btnGetDirection.setText("Get Directions");
//                    btnGetDirection.setVisibility(View.GONE);
//                }

                break;
            case R.id.ic_notify:
                mIsDriverNeed = true;
                isDriverFound = false;
                callNearestDriver();

//                sendRequestToDriver(mDriverId);
                break;

        }
    }

    public LatLng getLatLngFromString(String location) {
        return new LatLng(Double.valueOf(location.split(",")[0]), Double.valueOf(location.split(",")[1]));
    }

    private void callNearestDriver() {
        final DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driverAvailable");
        Log.d(TAG, "callNearestDriver:location of last driver " + mLastLocation);
        final GeoFire geoFire = new GeoFire(driverLocation);
        final GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), mDriverdistance);
//        mMap.clear();
        geoQuery.removeAllListeners();


        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {
                Log.d(TAG, "onKeyEnteredcallNearestDriver: " + key + " userID" + userID);

                if (!isDriverFound && !userID.equals(key)) {
                    isDriverFound = true;
                    mDriverId = key;

                    Log.d(TAG, "onKeyEntered:true ");
//                    Requesting Driver => userID
//                    To be notified Driver => mDriverID
//                     Location of requesting driver => mLastLocation

                    //Set the value
                    FirebaseDatabase.getInstance().getReference().child("Notifiers").child(mDriverId).child(FirebaseAuth.getInstance().getUid()).setValue(getStringLocation(mLastLocation));
                    Log.d(TAG, "onKeyEntered: LOOKING FOR DENOTIFIERS");
//                    FirebaseDatabase.getInstance().getReference().child("Denotifier").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            if (dataSnapshot.exists()) {
//                                Log.d(TAG, "onDataChange: FOUND THE DENOTIFIER");
//                                geoQuery.removeAllListeners();
//                                FirebaseDatabase.getInstance().getReference().child("Denotifier").child(FirebaseAuth.getInstance().getUid()).removeValue();
//                                FirebaseDatabase.getInstance().getReference().child("Notifiers").child(FirebaseAuth.getInstance().getUid()).removeValue();
//                                mIsDriverNeed = false;
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });

//                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("NearestDriverCall");
//                    GeoFire geoFire = new GeoFire(ref);
//                    otherDriverLocation = new LatLng(location.latitude, location.longitude);
//                    geoFire.setLocation(mDriverId, new GeoLocation(otherDriverLocation.latitude, otherDriverLocation.longitude), new GeoFire.CompletionListener() {
//                        @Override
//                        public void onComplete(String key, DatabaseError error) {
////
//                            Log.d(TAG, "onCompleteNearestDriver: " + key);
//                        }
//                    });


//                    Map<String,Object> driverRequestID = new HashMap<>();
//                    Map<String,Object> driverRequestCoord = new HashMap<>();
//                    driverRequestCoord.put("userId",key);
//                    driverRequestCoord.put("driverRequstedLat",location.latitude);
//                    driverRequestCoord.put("driverRequstedLng",location.longitude);
//                    driverRequestCoord.put("driverRequestLat",mLastLocation.getLatitude());
//                    driverRequestCoord.put("driverRequestLng",mLastLocation.getLongitude());
//                    driverRequestID.put(userID,driverRequestCoord);
//                    ref.updateChildren(driverRequestID);

//                    mIsDriverNeed = true;


//                    driverLocation.child(mDriverId).addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            Log.d(TAG, "onDataChange:keyentered " + dataSnapshot);
//
//                            mIsDriverNeed = true;
//                            otherDriverLocation = new LatLng(location.latitude, location.longitude);
//
//                            Log.d(TAG, "onDataChangeroutes: " + k++);
////                                getRouteToMarker(otherDriverLocation);
////                            }
//
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });

                }


            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!isDriverFound) {
                    mDriverdistance++;
                    callNearestDriver();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    public String getStringLocation(Location location) {
        return String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude());
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

    private void getRouteToMarker(LatLng location) {

        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .key("AIzaSyAQOf62LKuJRYliJed5mNUw2e7E5VLQOpI")
                .waypoints(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), location)
                .build();
        routing.execute();


    }

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
                                Log.d(TAG, "onDataChange:load " + mlocalkey + " " + mkey.contains(mlocalkey));
                                if (!mkey.contains(mlocalkey)) {
                                    mkey.add(mlocalkey);
//                                    initRecyclerView(getView());
                                    Log.d(TAG, "onDataChange:s " + mkey);

                                }

                                FirebaseDatabase.getInstance().getReference("customerRequestInfo").child(key)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                try {
                                                    final CustomerRequest request = dataSnapshot.getValue(CustomerRequest.class);
                                                    Log.d(TAG, "onDataChangereq: " + request);
                                                    FirebaseDatabase.getInstance().getReference("CustomerCarInformation")
                                                            .child(request.getUserID()).child(request.getCarId())
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    CustomerCarInfo customerCarInfo = dataSnapshot.getValue(CustomerCarInfo.class);
                                                                    Log.d(TAG, "onDataChangereq: " + customerCarInfo.getCarlicenseplate());
                                                                    if (request.getTimeFrame().equals("180") ) {
                                                                        mMap.addMarker(new MarkerOptions().
                                                                                position(new LatLng(location.latitude, location.longitude))
                                                                                .flat(true)
                                                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                                                                                .title(customerCarInfo.getCarlicenseplate())
                                                                                .snippet(customerCarInfo.getCarmake()+" "+customerCarInfo.getCarmodel()));
                                                                    }
                                                                    if (request.getTimeFrame().equals("60"))  {
                                                                        mMap.addMarker(new MarkerOptions().
                                                                                position(new LatLng(location.latitude, location.longitude))
                                                                                .flat(true)
                                                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                                                                .title(customerCarInfo.getCarlicenseplate())
                                                                                .snippet(customerCarInfo.getCarmake()+" "+customerCarInfo.getCarmodel()));
                                                                    }
                                                                    if (request.getTimeFrame().equals("90")) {
                                                                        mMap.addMarker(new MarkerOptions().
                                                                                position(new LatLng(location.latitude, location.longitude))
                                                                                .flat(true)
                                                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                                                                .title(customerCarInfo.getCarlicenseplate())
                                                                                .snippet(customerCarInfo.getCarmake()+" "+customerCarInfo.getCarmodel()));
                                                                    }


                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });

                                                } catch (NullPointerException e) {

                                                }


                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


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
        j++;
        Log.d(TAG, "loadAllCustomers: " + j);
        if (j == 2) {
            Log.d(TAG, "loadAllCustomers:check " + initrecyclerview);
            initRecyclerView(getView());
            initrecyclerview = false;
        }


    }

    @Override
    public void onInfoWindowClick(Marker marker) {


    }

    @Override
    public void onUserClick(int position) {
        Log.d(TAG, "onUserClick: Selected a user" + list.get(position));

    }

    @Override
    public void onRoutingFailure(RouteException e) {
        if (e != null) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d(TAG, "onRoutingFailure: ");
        } else {
            Toast.makeText(getContext(), "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
//        try{
//            mRoute.clear();
//        }catch (Exception e){
//
//        }
        isNotify = true;

        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }


        mRoute = route;
        Log.d(TAG, "onRoutingSuccess: " + mRoute);
        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

//            Toast.makeText(getContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }
    }

//    private void addPolyLine(ArrayList<Route> route) {
//        route = mRoute;
//        for (int i = 0; i < route.size(); i++) {
//
//            //In case of more than 5 alternative routes
//            int colorIndex = i % COLORS.length;
//
//            PolylineOptions polyOptions = new PolylineOptions();
//            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
//            polyOptions.width(10 + i * 3);
//            polyOptions.addAll(route.get(i).getPoints());
//            Polyline polyline = mMap.addPolyline(polyOptions);
//            polylines.add(polyline);
//
//            Toast.makeText(getContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    public void onRoutingCancelled() {

    }

    private void erasePolylines() {
        for (Polyline line : polylines) {
            line.remove();
        }
        polylines.clear();

        isNotify = false;

    }
}
