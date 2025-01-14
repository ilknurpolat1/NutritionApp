package com.example.nutritionapp.ui.home;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nutritionapp.Activity.DisplayFood;
import com.example.nutritionapp.Food.FoodViewModel;
import com.example.nutritionapp.R;
import com.example.nutritionapp.adapters.DisplayFoodAdapter;
import com.example.nutritionapp.databinding.FragmentHomeBinding;
import com.example.nutritionapp.ui.Dashboard.DashboardActivity;
import com.example.nutritionapp.ui.search.SearchActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    CardView cardToday;
    FoodViewModel foodViewModel;
    ImageButton breakfastAdd, lunchAdd, snacksAdd, dinnerAdd;
    CardView cardBreakfast,cardLunch,cardSnacks,cardDinner;
    RelativeLayout fixedBreakfast,fixedLunch,fixedSnacks,fixedDinner;
    LinearLayout hiddenBreakfast,hiddenLunch,hiddenSnacks,hiddenDinner;
    RecyclerView recyclerBreakfast,recyclerLunch,recyclerSnacks,recyclerDinner;
    TextView homeBreakfast,homeLunch,homeSnacks,homeDinner,breakfastCalories,lunchCalories,snacksCalories,dinnerCalories,totalCalories,todayInsights;
    ProgressBar progressBar;
    Date currentDate;
    DatabaseReference databaseReference;
    SimpleDateFormat dateFormat;
    String dateOnly;
    float calorieGoal;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        breakfastAdd= root.findViewById(R.id.home_breakfast_add);
        lunchAdd= root.findViewById(R.id.home_lunch_add);
        snacksAdd= root.findViewById(R.id.home_snacks_add);
        dinnerAdd= root.findViewById(R.id.home_dinner_add);

        foodViewModel = new ViewModelProvider(this).get(FoodViewModel.class);

        currentDate = new Date();
        dateFormat=  new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault());
        dateOnly = dateFormat.format(currentDate);
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("USERS");
        if(user !=null){
            databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Map<String, String> map= (Map<String, String>) snapshot.getValue();
                    String Calories= map.get("calories");
                    calorieGoal=Float.parseFloat(Calories);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        //To intent to search activity

        breakfastAdd.setOnClickListener(view -> {
            Intent intent= new Intent(root.getContext(), SearchActivity.class);
            intent.putExtra("title", "Breakfast");
            startActivity(intent);
        });

        lunchAdd.setOnClickListener(view -> {
            Intent intent= new Intent(root.getContext(), SearchActivity.class);
            intent.putExtra("title", "Lunch");
            startActivity(intent);
        });

        snacksAdd.setOnClickListener(view -> {
            Intent intent= new Intent(root.getContext(), SearchActivity.class);
            intent.putExtra("title", "Snacks");
            startActivity(intent);
        });

        dinnerAdd.setOnClickListener(view -> {
            Intent intent= new Intent(root.getContext(), SearchActivity.class);
            intent.putExtra("title", "Dinner");
            startActivity(intent);
        });


        //To display all food list & calories
        totalCalories = root.findViewById(R.id.total_calories);
        foodViewModel.getTotalTodayCalories(dateOnly).observe(getViewLifecycleOwner(), this::setTotalCalories);

        breakfastCalories = root.findViewById(R.id.home_breakfast_calories);
        foodViewModel.getTotalBreakfastCalories(dateOnly).observe(getViewLifecycleOwner(), this::setBreakfastCalories);

        lunchCalories = root.findViewById(R.id.home_lunch_calories);
        foodViewModel.getTotalLunchCalories(dateOnly).observe(getViewLifecycleOwner(), this::setLunchCalories);

        snacksCalories = root.findViewById(R.id.home_snacks_calories);
        foodViewModel.getTotalSnacksCalories(dateOnly).observe(getViewLifecycleOwner(), this::setSnacksCalories);

        dinnerCalories = root.findViewById(R.id.home_dinner_calories);
        foodViewModel.getTotalDinnerCalories(dateOnly).observe(getViewLifecycleOwner(), this::setDinnerCalories);

        progressBar = (ProgressBar) root.findViewById(R.id.progress_bar);
        foodViewModel.getTotalTodayCalories(dateOnly).observe(getViewLifecycleOwner(), this::updateProgressBar);

        cardToday= root.findViewById(R.id.card_today_home);
        cardToday.setOnClickListener(view -> {
            Intent intent= new Intent(root.getContext(), DisplayFood.class);
            startActivity(intent);
        });

        //To display breakfast list using cardView
        cardBreakfast= root.findViewById(R.id.base_breakfast);
        fixedBreakfast= root.findViewById(R.id.fixed_breakfast);
        hiddenBreakfast= root.findViewById(R.id.hidden_breakfast);
        homeBreakfast= root.findViewById(R.id.home_breakfast);

        recyclerBreakfast = root.findViewById(R.id.recycler_breakfast);
        recyclerBreakfast.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerBreakfast.setHasFixedSize(true);


        // TODO: add a toast for meal which are not yet tracked
        // i.e. eg. lunch without food then say no foods added yet, click on add to track :)
        final DisplayFoodAdapter adapterBreakfast = new DisplayFoodAdapter();
        recyclerBreakfast.setAdapter(adapterBreakfast);
        foodViewModel.getAllBreakfast(dateOnly).observe(getViewLifecycleOwner(), adapterBreakfast::setFoods);
        fixedBreakfast.setOnClickListener(view -> {
            if(breakfastCalories.getText().toString().equals(""))
                Toast.makeText(getContext(), "No food added yet", Toast.LENGTH_SHORT).show();
            if (hiddenBreakfast.getVisibility() == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(cardBreakfast,
                        new AutoTransition());
                hiddenBreakfast.setVisibility(View.GONE);
            }
            else {
                TransitionManager.beginDelayedTransition(cardBreakfast,
                        new AutoTransition());
                hiddenBreakfast.setVisibility(View.VISIBLE);

            }
        });

        //To display Lunch list using cardView

        cardLunch= root.findViewById(R.id.base_lunch);
        fixedLunch= root.findViewById(R.id.fixed_lunch);
        hiddenLunch= root.findViewById(R.id.hidden_lunch);
        homeLunch= root.findViewById(R.id.home_lunch);

        recyclerLunch = root.findViewById(R.id.recycler_lunch);
        recyclerLunch.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerLunch.setHasFixedSize(true);


        final DisplayFoodAdapter adapterLunch = new DisplayFoodAdapter();
        recyclerLunch.setAdapter(adapterLunch);
        foodViewModel.getAllLunch(dateOnly).observe(getViewLifecycleOwner(), adapterLunch::setFoods);
        fixedLunch.setOnClickListener(view -> {
            if(breakfastCalories.getText().toString().equals(""))
                Toast.makeText(getContext(), "No food added yet", Toast.LENGTH_SHORT).show();
            if (hiddenLunch.getVisibility() == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(cardLunch,
                        new AutoTransition());
                hiddenLunch.setVisibility(View.GONE);
            }
            else {
                TransitionManager.beginDelayedTransition(cardLunch,
                        new AutoTransition());
                hiddenLunch.setVisibility(View.VISIBLE);
            }
        });

        //To display Snacks list using cardView

        cardSnacks= root.findViewById(R.id.base_snacks);
        fixedSnacks= root.findViewById(R.id.fixed_snacks);
        hiddenSnacks= root.findViewById(R.id.hidden_snacks);
        homeSnacks= root.findViewById(R.id.home_snacks);

        recyclerSnacks = root.findViewById(R.id.recycler_snacks);
        recyclerSnacks.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerSnacks.setHasFixedSize(true);


        final DisplayFoodAdapter adapterSnacks = new DisplayFoodAdapter();
        recyclerSnacks.setAdapter(adapterSnacks);
        foodViewModel.getAllSnacks(dateOnly).observe(getViewLifecycleOwner(), adapterSnacks::setFoods);
        fixedSnacks.setOnClickListener(view -> {
            if(breakfastCalories.getText().toString().equals(""))
                Toast.makeText(getContext(), "No food added yet", Toast.LENGTH_SHORT).show();
            if (hiddenSnacks.getVisibility() == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(cardSnacks,
                        new AutoTransition());
                hiddenSnacks.setVisibility(View.GONE);
            }
            else {
                TransitionManager.beginDelayedTransition(cardSnacks,
                        new AutoTransition());
                hiddenSnacks.setVisibility(View.VISIBLE);
            }
        });

        //To display Dinner list using cardView

        cardDinner= root.findViewById(R.id.base_dinner);
        fixedDinner= root.findViewById(R.id.fixed_dinner);
        hiddenDinner= root.findViewById(R.id.hidden_dinner);
        homeDinner= root.findViewById(R.id.home_dinner);

        recyclerDinner = root.findViewById(R.id.recycler_dinner);
        recyclerDinner.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerDinner.setHasFixedSize(true);


        final DisplayFoodAdapter adapterDinner = new DisplayFoodAdapter();
        recyclerDinner.setAdapter(adapterDinner);
        foodViewModel.getAllDinner(dateOnly).observe(getViewLifecycleOwner(), adapterDinner::setFoods);
        fixedDinner.setOnClickListener(view -> {
            if(breakfastCalories.getText().toString().equals(""))
                Toast.makeText(getContext(), "No food added yet", Toast.LENGTH_SHORT).show();
            if (hiddenDinner.getVisibility() == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(cardDinner,
                        new AutoTransition());
                hiddenDinner.setVisibility(View.GONE);
            }
            else {
                TransitionManager.beginDelayedTransition(cardDinner,
                        new AutoTransition());
                hiddenDinner.setVisibility(View.VISIBLE);
            }
        });

        todayInsights = root.findViewById(R.id.today_insights);

        todayInsights.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), DashboardActivity.class);
            startActivity(intent);
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                foodViewModel.delete(adapterBreakfast.getFoodAt(viewHolder.getAdapterPosition()));
                Toast.makeText(getContext(), "Food Deleted", Toast.LENGTH_SHORT).show();

            }
        }).attachToRecyclerView(recyclerBreakfast);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                foodViewModel.delete(adapterDinner.getFoodAt(viewHolder.getAdapterPosition()));
                Toast.makeText(getContext(), "Food Deleted", Toast.LENGTH_SHORT).show();

            }
        }).attachToRecyclerView(recyclerDinner);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                foodViewModel.delete(adapterLunch.getFoodAt(viewHolder.getAdapterPosition()));
                Toast.makeText(getContext(), "Food Deleted", Toast.LENGTH_SHORT).show();

            }
        }).attachToRecyclerView(recyclerLunch);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                foodViewModel.delete(adapterSnacks.getFoodAt(viewHolder.getAdapterPosition()));
                Toast.makeText(getContext(), "Food Deleted", Toast.LENGTH_SHORT).show();

            }
        }).attachToRecyclerView(recyclerSnacks);

        return root;
    }

    private void setDinnerCalories(Float calories) {
        if(calories!= null)
            dinnerCalories.setText(String.format("%sCal", calories.toString()));
        else
            dinnerCalories.setText("");

    }

    private void setSnacksCalories(Float calories){
        if(calories!= null)
            snacksCalories.setText(String.format("%sCal", String.format("%.2f", calories)));
        else
            snacksCalories.setText("");

    }

    private void setLunchCalories(Float calories) {
        if(calories!= null)
            lunchCalories.setText(String.format("%sCal", String.format("%.2f", calories)));
        else
            lunchCalories.setText("");

    }

    private void setBreakfastCalories(Float calories) {
        if(calories!= null)
            breakfastCalories.setText(String.format("%sCal", String.format("%.2f", calories)));
        else
            breakfastCalories.setText("");

    }

    private void setTotalCalories(Object calories) {
        if(calories!= null)
            totalCalories.setText(String.format("%s \nCal", String.format("%.1f", calories)));
        }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateProgressBar(Object calories) {
        if (calories != null) {
            float totalCaloriesEaten = (Float) calories;


            if (calorieGoal > totalCaloriesEaten)
                progressBar.setProgress((int) ((totalCaloriesEaten / calorieGoal) * 100));
            else
                progressBar.setProgress(100);
        }
        else{
            progressBar.setProgress(0);
            totalCalories.setText("0Cal");
        }
    }
    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}