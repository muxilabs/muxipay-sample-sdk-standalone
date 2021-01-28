package muxi.sample;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import java.util.ArrayList;
import java.util.Set;

import muxi.payservices.sdk.SharedConstants;
import muxi.sample.model.PinpadDevicePojo;

import static android.content.Context.INPUT_METHOD_SERVICE;

class BluetoothList {
    private Context context;
    private Spinner bluetoothSpiner;
    private ArrayList<PinpadDevicePojo> pinpadDevices = new ArrayList<>();
    private PinpadSelectionListener pinpadSelectionListener;
    private PinpadDevicePojo mCurrentDevice;

    public ArrayList<String> getListPairedNames() {
        return listPairedNames;
    }

    private ArrayList<String> listPairedNames = new ArrayList<>();

    BluetoothList(Context context,
                  Spinner bluetoothSpiner){
        this.context = context;
        this.bluetoothSpiner = bluetoothSpiner;
    }

    void setBtLinester(PinpadSelectionListener pinpadSelectionListener){
        this.pinpadSelectionListener = pinpadSelectionListener;
    }

    void updateItemsOnSpinner(final ArrayAdapter<String> adapter) {
        if(checkBluetoothOn()){

            updateAvailableDevices();

            adapter.setDropDownViewResource(R.layout.simple_dropdown_item);
            bluetoothSpiner.setAdapter(adapter);
            bluetoothSpiner.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        hideKeyboard(v);
                        if(checkBluetoothOn()){
                            //Check if exists one or more listPairedNames bonded
                            if(!checkPairedDevices()){
                                openBluetooth(context.getApplicationContext());
                            }
                        } else {
                            enableBluetooth();
                        }
                    }
                    return false;
                }
            });


            bluetoothSpiner.setSelected(false);
            bluetoothSpiner.setSelection(0,true);
            bluetoothSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    String spinnerSelection = bluetoothSpiner.getSelectedItem().toString();

                    if (spinnerSelection.equals(getPPNoSelectedMsg())){
                        return;
                    }

                    if (isSelectedSameAsBefore(spinnerSelection)){
                        return;
                    }

                    mCurrentDevice = findDevice(spinnerSelection);
                    bluetoothSpiner.setSelection(position,true);

                    pinpadSelectionListener.onPinpadSelected(mCurrentDevice);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }else {
            enableBluetooth();
            updateItemsOnSpinner(adapter);
        }
    }

    private PinpadDevicePojo findDevice(String deviceDame){
        if (pinpadDevices!=null){
            for (PinpadDevicePojo pinpadDevice : pinpadDevices) {
                if (deviceDame.equals(pinpadDevice.getDeviceName())){
                    return pinpadDevice;
                }
            }
        }
        return null;
    }

    private void hideKeyboard(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private boolean isSelectedSameAsBefore(String spinerSelecion){
        if (mCurrentDevice !=null && mCurrentDevice.getDeviceName() != null){
            return mCurrentDevice.getDeviceName().equals(spinerSelecion);
        }
        return false;
    }

    public interface PinpadSelectionListener {
        void onPinpadSelected(PinpadDevicePojo pinpadDevice);
    }

    ArrayAdapter<String> createAdapter(){
        return new ArrayAdapter<String>(context, R.layout.simple_list_item,R.id.tv_spinner, listPairedNames){
            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {

                View view = super.getDropDownView(position, null, parent);
                ImageView imageView = view.findViewById(R.id.iv_bluetooth_icon);
                // If this is the selected item position
                if (position == 1 ){
                    imageView.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.ic_nfc_black_18dp));
                } else if(position == 2 ) {
                    imageView.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.ic_developer_board_black_18dp));
                } else if (position > 0) {
                    imageView.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.ic_bluetooth_black_18dp));
                }
                return view;
            }
        };
    }

    private void updateAvailableDevices(){

        pinpadDevices.clear();
        listPairedNames.clear();

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        pinpadDevices.add(
                new PinpadDevicePojo(AppConstants.PP_SELECT_DEVICE, SharedConstants.SET_PP_TYPE_PARAM_MUXI_EMV)
        );

        pinpadDevices.add(
                new PinpadDevicePojo(AppConstants.PP_MUXI_EMV, SharedConstants.SET_PP_TYPE_PARAM_MUXI_EMV)
        );
        pinpadDevices.add(
                new PinpadDevicePojo(AppConstants.INTER_PP_NAME, SharedConstants.SET_PP_TYPE_PARAM_MUXI_EMV)
        );


        for(BluetoothDevice bt : pairedDevices){
            pinpadDevices.add(new PinpadDevicePojo(bt.getName(),bt.getAddress()));
        }

        for (PinpadDevicePojo pd :pinpadDevices) {
            listPairedNames.add(pd.getDeviceName());
        }

    }

    void setWhenDeconfigure(){
        mCurrentDevice = null;
    }

    private boolean checkPairedDevices(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        return pairedDevices.size() > 0;
    }


    private boolean checkBluetoothOn(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            return mBluetoothAdapter.isEnabled();
        } else {
            return false;
        }
    }

    private void openBluetooth(Context context){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void enableBluetooth(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }
        }
    }

    private String getPPNoSelectedMsg(){
        return context.getResources().getString(R.string.no_pinpad_selected);
    }
}
