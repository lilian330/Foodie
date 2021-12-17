package com.comp4905.foodie.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.comp4905.foodie.Activities.MainActivity;
import com.comp4905.foodie.Adapters.PostAdapter;
import com.comp4905.foodie.Models.Post;
import com.comp4905.foodie.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tz.co.hosannahighertech.messagekit.commons.ImageLoader;
import tz.co.hosannahighertech.messagekit.dialogs.DialogsListAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView mName,mEmail;
    private ImageView mProfile;
    private FirebaseAuth mAuth;
    private FirebaseUser currUser;

    RecyclerView postRV;
    PostAdapter postAdapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<Post> posts;


    public MeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeFragment newInstance(String param1, String param2) {
        MeFragment fragment = new MeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_me, container, false);
        mName = fragmentView.findViewById(R.id.mName);
        mEmail = fragmentView.findViewById(R.id.mEmail);
        mProfile = fragmentView.findViewById(R.id.mProfile);

        mAuth = FirebaseAuth.getInstance();
        currUser = mAuth.getCurrentUser();
        if(currUser.getPhotoUrl()!=null){
            Glide.with(this.getContext()).load(currUser.getPhotoUrl().toString()).into(mProfile);
        }
        mName.setText(currUser.getDisplayName());
        mEmail.setText("Email: "+currUser.getEmail());
        postRV = fragmentView.findViewById(R.id.myPostRV);
        postRV.setLayoutManager(new LinearLayoutManager(getContext()));
        postRV.setHasFixedSize(true);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Posts");

        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                posts = new ArrayList<>();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Post p = ds.getValue(Post.class);
                    if(p.getUID().equals(currUser.getUid()))
                        posts.add(p);
                }
                Collections.reverse(posts);
                postAdapter = new PostAdapter(getActivity(), posts);

                postRV.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}