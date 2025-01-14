package com.example.nutritionapp.Register;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.nutritionapp.Activity.LoginActivity;
import com.example.nutritionapp.R;
import com.example.nutritionapp.databinding.FragmentCalorieBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class FragmentCalorie extends Fragment {
    FragmentCalorieBinding binding;
    Button register;
    FirebaseAuth mAuth;
    Double a, mcalorie;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCalorieBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        register= rootView.findViewById(R.id.button_register);


        register.setOnClickListener(view -> {
            //Here register function will come use get functions for taking data eg: for gender do getGender()
            registerUser();

        });

        return rootView;
    }

    private void registerUser(){
        SharedPreferences sh = this.getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String email= sh.getString("email", "");
        String mobile= sh.getString("mobile", "");
        String pass= sh.getString("pass", "");
        String name= sh.getString("name", "");
        String age= sh.getString("age", "");
        String gender= sh.getString("gender", "");
        String height= sh.getString("height", "");
        String weight= sh.getString("weight", "");
        String active= sh.getString("active", "");

        if(active.equals("Light"))
            a= 1.2;
        else if(active.equals("Moderate"))
            a= 1.55;
        else
            a= 1.9;
        if(gender.equals("Male"))
            mcalorie= (66.5 + 13.8 * Integer.parseInt(weight) + 5* Integer.parseInt(height) ) - (6.8 * Integer.parseInt(age)) * a;
        else
            mcalorie= (655.1 + 9.5 * Integer.parseInt(weight) + 1.8* Integer.parseInt(height) ) - (4.6* Integer.parseInt(age)) * a;



        mAuth= FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email.trim(), pass.trim())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser use=mAuth.getCurrentUser();
                        use.sendEmailVerification().addOnSuccessListener(unused -> Toast.makeText(getContext(),"Verification Link Sent",Toast.LENGTH_SHORT).show()).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(),"failed",Toast.LENGTH_SHORT).show();
                            }
                        });
                        HashMap<String,Object> map=new HashMap<>();
                        map.put("name",name.trim());
                        map.put("email",email.trim());
                        map.put("mobile",mobile.trim());
                        map.put("id",mAuth.getCurrentUser().getUid());
                        map.put("height",height.trim());
                        map.put("weight",weight.trim());
                        map.put("age",age.trim());
                        map.put("gender", gender.trim());
                        map.put("activity",active.trim());
                        map.put("calories", mcalorie.toString().trim());

                        FirebaseDatabase.getInstance().getReference("USERS")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(map).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(getContext(), "You have successfully Registered", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getContext(), "Failed to Register", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        Intent i=new Intent(getActivity(), LoginActivity.class);
                        startActivity(i);

                    }
                    else {
                        Toast.makeText(getContext(), "Failed to Register", Toast.LENGTH_SHORT).show();
                    }
                });
    }



}