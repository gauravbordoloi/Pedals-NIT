package com.gmonetix.pedals.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.gmonetix.pedals.model.User;

/**
 * @author Gmonetix
 */

public class SharedPref {

    private SharedPreferences custom_prefence;
    private SharedPreferences default_prefence;

    private static final String NAME = "name";
    private static final String PHONE = "phone";
    private static final String EMAIL ="email";
    private static final String IMAGE_LINK = "image_link";
    private static final String PASSWORD = "password";
    private static final String NIT_REG ="nit_reg";

    private static final String FIRST_TIME ="first_time";
    private static final String LOGIN ="login";

    private static final String ADMIN_EMAIL ="admin_email";
    private static final String ADMIN_PHONE ="admin_phone";

    private static final String PRICE_GEAR ="price_gear";
    private static final String PRICE_GEARLESS ="price_gearless";

    public SharedPref(Context context) {
        custom_prefence = context.getSharedPreferences("MAIN_PREF", Context.MODE_PRIVATE);
        default_prefence = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setLogin(boolean val) {
        default_prefence.edit().putBoolean(LOGIN,val).apply();
    }

    public boolean isLogin() {
        return default_prefence.getBoolean(LOGIN,false);
    }

    public void setName(String name) {
        custom_prefence.edit().putString(NAME, name).apply();
    }

    public String getName() {
        return custom_prefence.getString(NAME,"");
    }

    public void setPhone(String phone) {
        custom_prefence.edit().putString(PHONE, phone).apply();
    }

    public String getPhone() {
        return custom_prefence.getString(PHONE,"");
    }

    public void setEmail(String email) {
        custom_prefence.edit().putString(EMAIL, email).apply();
    }

    public String getEmail() {
        return custom_prefence.getString(EMAIL,"");
    }

    public void setImageLink(String link) {
        custom_prefence.edit().putString(IMAGE_LINK, link).apply();
    }

    public String getImageLink() {
        return custom_prefence.getString(IMAGE_LINK,"");
    }

    public void setPassword(String password) {
        custom_prefence.edit().putString(PASSWORD, password).apply();
    }

    public String getPassword() {
        return custom_prefence.getString(PASSWORD,"");
    }

    public void setNitReg(String nit_reg) {
        custom_prefence.edit().putString(NIT_REG, nit_reg).apply();
    }

    public String getNitReg() {
        return custom_prefence.getString(NIT_REG,"");
    }

    public void clearPref() {
        default_prefence.edit().clear().apply();
        custom_prefence.edit().clear().apply();
    }

    public User getUser() {
        return new User(getName(),getEmail(),getPhone(),getNitReg(),getImageLink());
    }

    public void setAdminPhone(String phone) {
        custom_prefence.edit().putString(ADMIN_PHONE, phone).apply();
    }

    public String getAdminPhone() {
        return custom_prefence.getString(ADMIN_PHONE,"8404017253");
    }

    public void setAdminEmail(String email) {
        custom_prefence.edit().putString(ADMIN_EMAIL, email).apply();
    }

    public String getAdminEmail() {
        return custom_prefence.getString(ADMIN_EMAIL,"gmonetix@gmail.com");
    }

    public void setPriceGear(int priceGear) {
        custom_prefence.edit().putInt(PRICE_GEAR, priceGear).apply();
    }

    public int getPriceGear() {
        return custom_prefence.getInt(PRICE_GEAR,0);
    }

    public void setPriceGearless(int priceGearless) {
        custom_prefence.edit().putInt(PRICE_GEARLESS, priceGearless).apply();
    }

    public int getPriceGearless() {
        return custom_prefence.getInt(PRICE_GEARLESS,0);
    }

}
