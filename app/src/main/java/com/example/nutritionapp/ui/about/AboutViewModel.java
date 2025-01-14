package com.example.nutritionapp.ui.about;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AboutViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public AboutViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("NutriTrack : Healthy Living Key is a mobile application that helps users track their daily food consumption and achieve healthy eating goals. This app provides users with a tool to address personal health and wellness goals by providing nutritional content, calorie calculations, and nutritional recommendations.\n" +
                "\n" +
                "The application is designed in accordance with dietitian principles and healthy living standards. Users can record their daily food intake, monitor these records, and develop healthier eating habits with the recommendations provided by the application.\n");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
