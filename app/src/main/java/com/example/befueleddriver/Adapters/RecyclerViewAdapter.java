package com.example.befueleddriver.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.befueleddriver.Models.CustomerCarInfo;
import com.example.befueleddriver.Models.CustomerRequest;
import com.example.befueleddriver.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.Viewholder> {
    private static final String TAG = "RecyclerViewAdapter";
    FirebaseAuth mAuth;
    DatabaseReference myRef;
    DatabaseReference customerRequestRef;
    private String userID = null;
    private ArrayList<CustomerRequest> customerRequests;
    private Context mContext;
    private FragmentManager supportFragmentManager;

    public RecyclerViewAdapter(Context mContext, ArrayList<CustomerRequest> customerRequests, FragmentManager supportFragmentManager) {
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        this.customerRequests = customerRequests;

        this.supportFragmentManager = supportFragmentManager;
        this.mContext = mContext;
    }


    @Override
    public Viewholder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem, viewGroup, false);
        RecyclerView.ViewHolder holder = new Viewholder(view);
        Log.d(TAG, "onCreateViewHolder: ");
        return (Viewholder) holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder viewholder, final int i) {
//        viewholder.carName.setText(customerCarInfos.get(i).getCarmake());
//        viewholder.carModel.setText(customerCarInfos.get(i).getCarmodel());
//        viewholder.carColor.setText(customerCarInfos.get(i).getCarcolor());

        if (customerRequests.get(i).isIsorderplaced()) {
            customerRequestRef = FirebaseDatabase.getInstance().getReference().child("CustomerCarInformation")
                    .child(customerRequests.get(i).getUserID())
                    .child(customerRequests.get(i).getCarId());
//

            customerRequestRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    CustomerCarInfo info = dataSnapshot.getValue(CustomerCarInfo.class);


                    viewholder.carName.setText(info.getCarmake());
                    viewholder.carModel.setText(info.getCarmodel());
                    viewholder.carColor.setText(info.getCarcolor());
                    viewholder.carLicense.setText(info.getCarlicenseplate());


                }

                //
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            viewholder.view_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    FrameLayout frameLayout = v.findViewById(R.id.frameLayout_next_fragment);
//                    frameLayout.setVisibility(View.VISIBLE);
                }
            });
//        viewholder.parentLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//
//            }
//        });

        }
    }

    private void removevalue(int i) {
//        String carID = customerCarInfos.get(i).getUserid();
//        Log.d(TAG, "removevalue: " + carID);
//
//
//        myRef = FirebaseDatabase.getInstance().getReference().child("CustomerCarInformation").child(userID).child(carID);
//        myRef.removeValue(null);
    }

    private void editoption(int i) {
//        AddVehicleFragment addVehicleFragment = new AddVehicleFragment();
//        Log.d(TAG, "onClick: clicked On" + customerCarInfos.get(i).getCarmake());
//        Log.d(TAG, "onClick: clicked On" + customerCarInfos.get(i).getCarmodel());
//        Log.d(TAG, "onClick: clicked On" + customerCarInfos.get(i).getCarcolor());
//        Log.d(TAG, "onClick: userID" + customerCarInfos.get(i).getUserid());
//        Bundle bundle = new Bundle();
////        bundle.putString("userId", customerCarInfos.get(i).getUserid());
////        bundle.putString("carmake", customerCarInfos.get(i).getCarmake());
////        bundle.putString("carmodel", customerCarInfos.get(i).getCarmodel());
////        bundle.putString("caryear", customerCarInfos.get(i).getCaryear());
////        bundle.putString("carnote", customerCarInfos.get(i).getCaraddnote());
////        bundle.putString("carcolor", customerCarInfos.get(i).getCarcolor());
////        bundle.putString("licenseplate", customerCarInfos.get(i).getCarlicenseplate());
//        ArrayList<CustomerCarInfo>carinfo = new ArrayList<>();
//        carinfo.add(customerCarInfos.get(i));
//        bundle.putParcelableArrayList("customerCarInfolist", carinfo);
//        Log.d(TAG, "editoption: " + carinfo);
//        addVehicleFragment.setArguments(bundle);
//
//
//        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
//        transaction.replace(R.id.fragment_container, addVehicleFragment);
//        transaction.addToBackStack("fragment_container");
//        transaction.commit();

    }

    @Override
    public int getItemCount() {

        return customerRequests.size();
    }


    public class Viewholder extends RecyclerView.ViewHolder {
        private static final String TAG = "Viewholder";
        TextView carName;
        TextView carModel;
        TextView carColor;
        TextView carLicense;
        ImageView view_next;
        RelativeLayout parentLayout;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            carLicense = itemView.findViewById(R.id.text_license_plate);
            carName = itemView.findViewById(R.id.text_car_names);
            carColor = itemView.findViewById(R.id.text_car_color);
            carModel = itemView.findViewById(R.id.text_car_models);
            view_next = itemView.findViewById(R.id.btn_next_fragment);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            Log.d(TAG, "Viewholder: " + itemView);
        }
    }
}
