package com.hrishistudio.vnit.connectedvehicle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Iterator;

public class VehicleList extends AppCompatActivity {

    private RecyclerView vehicleList;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        vehicleList = (RecyclerView)findViewById(R.id.vehicle_list);
        mRef = FirebaseDatabase.getInstance().getReference().child("users").child(mUser.getUid()).child("vehicles");

        RecyclerView.LayoutManager manager = new LinearLayoutManager(VehicleList.this, RecyclerView.VERTICAL, false);
        vehicleList.setLayoutManager(manager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final ArrayList<String> refs = new ArrayList<>();
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
                    Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                    while (iterator.hasNext()){
                        refs.add(iterator.next().getValue().toString());
                    }
                    setList(refs);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setList(final ArrayList<String> refs) {
        RecyclerView.Adapter<VehicleViewHolder> adapter = new RecyclerView.Adapter<VehicleViewHolder>() {

            @NonNull
            @Override
            public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = getLayoutInflater().inflate(R.layout.vehicle_card, parent, false);
                return new VehicleViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
                holder.setView(refs.get(position));
            }

            @Override
            public int getItemCount() {
                return refs.size();
            }
        };
        vehicleList.setAdapter(adapter);
    }

    public static class VehicleViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView modelNameView;
        private TextView registrationNoView;
        private TextView registrationDateView;
        private TextView engineView;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            modelNameView = mView.findViewById(R.id.vehicle_card_model);
            registrationNoView = mView.findViewById(R.id.vehicle_card_registration);
            registrationDateView = mView.findViewById(R.id.vehicle_card_reg_date);
            engineView = mView.findViewById(R.id.vehicle_card_engine);
        }

        public void setView(String ref){
            FirebaseDatabase.getInstance().getReference().child("vehicles").child(ref).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null){
                        modelNameView.setText(snapshot.child("model_name").getValue().toString());
                        registrationNoView.setText(snapshot.child("registration_number").getValue().toString());
                        registrationDateView.setText(snapshot.child("registration_date").getValue().toString());
                        engineView.setText(snapshot.child("engine_number").getValue().toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void goBack(View view){
        finish();
    }

    public void addVehicle(View view){
        startActivity(new Intent(VehicleList.this, NewVehicle.class));
    }
}