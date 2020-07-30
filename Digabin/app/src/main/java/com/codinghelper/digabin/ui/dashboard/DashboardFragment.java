package com.codinghelper.digabin.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.codinghelper.digabin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DashboardFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference reference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView email=(TextView)root.findViewById(R.id.userEmail);
        final ImageView imageView=(ImageView)root.findViewById(R.id.userProfile);
        final TextView name=(TextView)root.findViewById(R.id.userName);
        final TextView status=(TextView)root.findViewById(R.id.userBio);


        firebaseAuth = FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference().child("User").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userEmail =String.valueOf(snapshot.child("Email id").getValue());
                email.setText(userEmail);
                String Simg =String.valueOf(snapshot.child("imageUrl").getValue());
                Picasso.get().load(Simg).fit().centerCrop().noFade().into(imageView);
                String Sname =String.valueOf(snapshot.child("userName").getValue());
                name.setText(Sname);
                String Sstatus =String.valueOf(snapshot.child("userstatus").getValue());
                status.setText(Sstatus);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return root;
    }
}
