package muxi.sample.model;

import android.content.Context;
import android.content.SharedPreferences;

import muxi.sample.R;

import static muxi.sample.AppConstants.DESENV_MERCHANT_ID;

public class Utils {

    public static SharedPreferences getSharedPrefs(Context context) {
        return context.getSharedPreferences(context.getString(R.string.pref_key), Context.MODE_PRIVATE);
    }

    public static void cacheDevice(Context context, PinpadDevicePojo pinpadDevicePojo){

        SharedPreferences.Editor editor = getSharedPrefs(context).edit();

        editor.putString(context.getString(R.string.pref_key_pinpad_device_name), pinpadDevicePojo.getDeviceName());
        editor.putString(context.getString(R.string.pref_key_pinpad_device_address), pinpadDevicePojo.getDeviceAdress());
        editor.apply();
    }

    public static PinpadDevicePojo getCachedPinpadDevice(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.pref_key), Context.MODE_PRIVATE);

        String cachedDeviceName = sharedPreferences.getString(context.getString(R.string.pref_key_pinpad_device_name), null);
        String cachedDeviceAddress = null;

        if (cachedDeviceName != null){
            cachedDeviceAddress = sharedPreferences.getString(context.getString(R.string.pref_key_pinpad_device_address), null);
            if(cachedDeviceAddress != null){
                return new PinpadDevicePojo(cachedDeviceName,cachedDeviceAddress);
            }
        }
        return null;
    }

    public static String getMerchantId(Context context){
        return getSharedPrefs(context).getString(context.getString(R.string.pref_merchant_id_key),DESENV_MERCHANT_ID);
    }

    public static void cacheMerchantId(Context context, String mechantIdS){
        getSharedPrefs(context).edit().putString(context.getString(R.string.pref_merchant_id_key),mechantIdS).apply();
    }
}
