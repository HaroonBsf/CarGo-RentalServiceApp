package com.example.fyp.constant;

import com.example.fyp.models.GetAddModel;
import com.example.fyp.models.PrivateAdsModel;
import com.example.fyp.models.ReadWriteUserDetails;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Constant {
    public static ReadWriteUserDetails userData;
    public static GetAddModel itemGetModel = new GetAddModel();
    public static PrivateAdsModel privateItemModel = new PrivateAdsModel();
    public static String itemClicked = "";
    public static double latitude;
    public static double longitude;
    public static String city = "";
    public static String homeCarType = "";

}
