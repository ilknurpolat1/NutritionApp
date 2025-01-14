package com.example.nutritionapp.Register;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.nutritionapp.Activity.LoginActivity;
import com.example.nutritionapp.R;

import com.example.nutritionapp.databinding.FragmentEmailBinding;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class FragmentEmail extends Fragment {
    private FragmentEmailBinding binding;
    RegisterMain registerMain;
    EditText textEmail, textMobile, textName;
    ExtendedFloatingActionButton next, back;
    TextInputLayout nameL, emailL, mobileL;
    ProgressBar progressBar;
    final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEmailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        textEmail = root.findViewById(R.id.text_email);
        textMobile = root.findViewById(R.id.text_mobile);
        textName = root.findViewById(R.id.text_name);
        next = root.findViewById(R.id.email_next);
        back = root.findViewById(R.id.email_back);
        nameL = root.findViewById(R.id.nameLayout);
        emailL = root.findViewById(R.id.emailLayout);
        mobileL = root.findViewById(R.id.mobileLayout);


        progressBar = root.findViewById(R.id.next_progress_bar);
        // to get data and set the texts when came from passwordFragment
        String passwd = "", active = "light";
        boolean selectedMale = false;
        boolean selectedFemale = false;
        int weight = 50, age = 18, height = 180;
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String name = bundle.getString("name", "");
            String email = bundle.getString("email", "");
            String mobileNo = bundle.getString("mobile", "");
            passwd = bundle.getString("pass", "");
            selectedMale = bundle.getBoolean("selectedMale", false);
            selectedFemale = bundle.getBoolean("selectedFemale", false);
            weight = bundle.getInt("weight", 50);
            age = bundle.getInt("age", 18);
            height = bundle.getInt("height", 180);
            active = bundle.getString("active", "light");
            textName.setText(name);
            textEmail.setText(email);
            textMobile.setText(mobileNo);
        }
        registerMain = (RegisterMain) getActivity();

        String finalPasswd = passwd;
        boolean finalSelectedFemale = selectedFemale;
        boolean finalSelectedMale = selectedMale;
        int finalWeight = weight;
        int finalAge = age;
        int finalHeight = height;
        String finalActive = active;
        next.setOnClickListener(view -> {
            if(textEmail.getText().toString() != null & textMobile.getText().toString() != null) {
                registerMain.myEdit.putString("email", textEmail.getText().toString());
                registerMain.myEdit.putString("mobile", textMobile.getText().toString());
                registerMain.myEdit.commit();
            }

            boolean valid = true;
            if (!validateEmail()) {
                valid = false;
            }

            if (textMobile.length() == 0 || textMobile.length() < 10 || textMobile.length() > 10) {
                mobileL.setError("Please Enter a Valid Mobile Number");
                textMobile.setError(null);
                valid = false;

            }
            if (textName.length() == 0) {
                nameL.setError("Name is required");
                textName.setError(null);
                valid = false;
            }
            if (valid) {
                FragmentPassword fragmentPassword = new FragmentPassword();
                sendInfo(fragmentPassword, finalPasswd, finalSelectedMale, finalSelectedFemale, finalAge, finalWeight, finalHeight, finalActive);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.register_container, fragmentPassword)
                        .commit();
                registerMain.myEdit.putString("name", textName.getText().toString());
                registerMain.myEdit.commit();
            }

        });
        back.setOnClickListener(view -> {

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });
        textName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nameL.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        textMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mobileL.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        textEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailL.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return root;

    }

    private boolean validateEmail() {
        String email = textEmail.getText().toString().trim();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (email.isEmpty()) {
            emailL.setError("Please Enter an Email-ID");
            textEmail.requestFocus();
            return false;
        }
        if (email.matches(EMAIL_PATTERN)) {
            //check email already exist or not.
            final boolean[] isNewUser = new boolean[1];
            firebaseAuth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            isNewUser[0] =task.getResult().getSignInMethods().isEmpty();
                            if (isNewUser[0]) {
                                isNewUser[0] = false;
                                Log.e("TAG", "Is New User!");
                            }
                            else {
                                Log.e("TAG", "Is Old User!");
                                emailL.setError("User already exists");
                                textEmail.requestFocus();
                            }
                        }
                    });
            return !isNewUser[0];
        }
        else {
            emailL.setError("Please Enter a valid Email-ID");
            textEmail.requestFocus();
            return false;
        }
    }
    private void sendInfo(Fragment fragment, String passwd, boolean selectedMale, boolean selectedFemale, int age, int weight, int height, String active){

        // get data from SharedPreferences if next/back is pressed
        /** data  only gets stored in shared preferences if next is clicked
         * other wise take data from editText
         * */
        // add data to bundle
        Bundle bundle = new Bundle();
        bundle.putString("pass", passwd);

        bundle.putBoolean("selectedMale", selectedMale);
        bundle.putBoolean("selectedFemale", selectedFemale);
        bundle.putInt("age", age);
        bundle.putInt("weight", weight);
        bundle.putInt("height", height);
        bundle.putString("active", active);
        fragment.setArguments(bundle);
    }
}