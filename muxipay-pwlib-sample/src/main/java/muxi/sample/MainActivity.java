package muxi.sample;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import muxi.pay.sdk.MuxiPayManager;
import muxi.payservices.sdk.SharedConstants;
import muxi.payservices.sdk.data.MPSResult;
import muxi.payservices.sdk.data.MPSTransaction;
import muxi.payservices.sdk.data.ReceiptType;
import muxi.payservices.sdk.service.CallbackAnswer;
import muxi.sample.dialogs.DialogCallback;
import muxi.sample.dialogs.DialogHelper;
import muxi.sample.model.PinpadDevicePojo;
import muxi.sample.model.Utils;
import muxi.sample.tasks.GenericTask;
import muxi.sample.tasks.InitTask;
import muxi.sample.tasks.TransactionTask;
import muxi.sample.utils.FormatUtils;

import static muxi.payservices.sdk.SharedConstants.GENERIC_CMD_GET_VERSIONS;
import static muxi.payservices.sdk.SharedConstants.SET_PP_TYPE_PARAM_INTERN;
import static muxi.payservices.sdk.SharedConstants.SET_PP_TYPE_PARAM_MUXI_EMV;
import static muxi.sample.AppConstants.DEFAULT_USE_PP;
import static muxi.sample.AppConstants.DESENV_MERCHANT_ID;
import static muxi.sample.AppConstants.PP_MUXI_EMV;
import static muxi.sample.AppConstants.RADIO_GROUP_CREDIT;
import static muxi.sample.AppConstants.RADIO_GROUP_DEBIT;
import static muxi.sample.AppConstants.RADIO_GROUP_VOUCHER;


public class MainActivity extends AppCompatActivity implements BluetoothList.PinpadSelectionListener, DialogCallback {

    private static final String TAG = MainActivity.class.getSimpleName();

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static final boolean DEFAULT_PP_MESSAGE = true;


    private int DEFAULT_POSITION = 4;

    private ActionBarDrawerToggle mDrawerToggle;


    @BindView(R.id.et_value)
    EditText mTextValue;
    @BindView(R.id.radioGroup)
    RadioGroup mTypeRadioGroup;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.my_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.tv_last_transaction)
    TextView mLastTransaction;
    @BindView(R.id.tv_value_last_transaction)
    TextView mValueLastTransaction;
    @BindView(R.id.tv_date_time_last_transaction)
    TextView mDateTimeLastTransaction;

    private DialogHelper dialogHelper;
    private ProgressDialog mProgressDialog;
    @BindView(R.id.spinner_pinpad_name)
    Spinner pinpadDeviceSpinner;
    @BindView(R.id.iv_paired_status)
    ImageView mImageBtStatus;

    private MuxiPayManager mpsManager;
    private MPSResult resultOfCall = new MPSResult();

    private BluetoothList bluetoothList;

    private boolean ppConfigured = false;

    String merchantId = DESENV_MERCHANT_ID;

    String clientReceipt = "";
    String establishmentReceipt = "";

    boolean rate = false;



    @Override
    public void onPinpadSelected(PinpadDevicePojo pinpadDevice) {
        configurePinpad(pinpadDevice);
    }

    private void configurePinpad(PinpadDevicePojo pinpadDevice) {
        if(pinpadDevice.getDeviceName().isEmpty() || pinpadDevice.getDeviceName().equals(AppConstants.PP_SELECT_DEVICE)){
            mImageBtStatus.setBackground(getResources().getDrawable(R.drawable.circle_off));
        } else {
            Utils.cacheDevice(this, pinpadDevice);
            mImageBtStatus.setBackground(getResources().getDrawable(R.drawable.circle_on));
            if (mpsManager != null) {
                if (pinpadDevice.getDeviceName().equals(AppConstants.INTER_PP_NAME)) {
                    mpsManager.setCurrentBluetoothDevice(SET_PP_TYPE_PARAM_INTERN);
                } else if (pinpadDevice.getDeviceName().equals(PP_MUXI_EMV)){
                    mpsManager.setCurrentBluetoothDevice(SET_PP_TYPE_PARAM_MUXI_EMV);
                } else {
                    Log.d(TAG, "Saving current bluetooth mac address " + pinpadDevice.getDeviceAdress());
                    mpsManager.setCurrentBluetoothDevice(pinpadDevice.getDeviceAdress());
                }
                createToast("Pinpad configured");
            } else {
                Log.e(TAG, "setPinpadDevice mpsManager == null");
            }
        }
    }

    private int defaultInstalments = 0;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setup();
    }

    private void setup() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mImageBtStatus.setBackground(getResources().getDrawable(R.drawable.circle_off));
        if (bluetoothList == null){
            bluetoothList = new BluetoothList(this, pinpadDeviceSpinner);
            adapter = bluetoothList.createAdapter();
            bluetoothList.updateItemsOnSpinner(adapter);
        }
        bluetoothList.setBtLinester(this);

        merchantId = Utils.getMerchantId(this);

        mProgressDialog = new ProgressDialog(this);
        mTextValue.addTextChangedListener(MaskWatcher.mask(mTextValue));
        mTypeRadioGroup.check(R.id.radioButton_credit);
        mTextValue.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(mTextValue, InputMethodManager.SHOW_IMPLICIT);
        }
        //Instantiate Dialogs
        dialogHelper = new DialogHelper(this,this);

        setSupportActionBar(mToolbar);
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar,R.string.open_drawer,R.string.close_drawer);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }
        setupNavMenu();
        updateMerchantId(merchantId);


    }

    @Override
    public void onBackPressed(){}

    private void setupNavMenu(){
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        switch (item.getItemId()) {
                            case R.id.action_start:
                                callInit();
                                return true;
                            case R.id.action_deconfigure:
                                mpsManager.deconfigure(true);
                                return true;
                            case R.id.action_establishment:
                                dialogHelper.showEstablishmentDialog();
                                return true;
                            case R.id.action_cancelTransaction:
                                dialogHelper.showVoidAnyDialog();
                                return true;
                            case R.id.action_getVersion:
                                callgetVerVersion();
                                return true;
                            case R.id.action_stopService:
                                mpsManager.stopService(getApplicationContext());
                                return true;
                            case R.id.action_genericCommand:
                                dialogHelper.showGenericCommandDialog();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
    }

    private void callgetVerVersion() {
        final GenericTask taskVersions =
                new GenericTask(mProgressDialog,
                        SharedConstants.GENERIC_CMD_GET_VERSIONS,
                        null,
                        mpsManager,
                        getResources().getString(R.string.loading));
        taskVersions.execute();
    }

    private void callInit() {
        final InitTask taskInit =
                new InitTask(mProgressDialog,
                        merchantId,
                        DEFAULT_PP_MESSAGE,
                        mpsManager,
                        BuildConfig.API_KEY,
                        getResources().getString(R.string.loading));
        taskInit.execute();
    }

    private void updateMerchantId(String merchantId) {
        this.merchantId = merchantId;
        View header = mNavigationView.getHeaderView(0);
        TextView mMerchantId= header.findViewById(R.id.tv_merchantIdHeader);
        String text = "MerchantId " + merchantId;
        mMerchantId.setText(text);
    }

    private void setLastTransactionData(String titleLastTransaction, String typePayment,
                                        String valueLastTransaction,
                                        String dateTimeLastTransaction, String valueDefault,
                                        String clientReceipt, String establishmentReceipt) {
        if(typePayment.equals(MPSTransaction.TransactionMode.CREDIT.name())){
            typePayment = getResources().getString(R.string.credit)+" | ";
        }
        else{
            if(typePayment.equals(MPSTransaction.TransactionMode.DEBIT.name())){

                typePayment = getResources().getString(R.string.debit)+" | ";
            }
            else{
                if(typePayment.equals(MPSTransaction.TransactionMode.VOUCHER.name())) {
                    typePayment = getResources().getString(R.string.voucher) + " | ";
                }
            }
        }
        mLastTransaction.setText(titleLastTransaction);
        mValueLastTransaction.setText(valueLastTransaction);
        String dateTimeandType = typePayment+dateTimeLastTransaction;
        mDateTimeLastTransaction.setText(dateTimeandType);
        mTextValue.setText(valueDefault);
        this.clientReceipt = clientReceipt;
        this.establishmentReceipt = establishmentReceipt;
    }



    MPSTransaction currentMpsTransaction = null;
    @OnClick(R.id.btn_cancel)
    public void onBtnCancel()
    {
        //TODO add a protection for sending null in transactionMode for cancel last transaction.
        //Actually this field is ignored, but still necessary to avoid crash
        currentMpsTransaction = createTransaction("", MPSTransaction.TransactionMode.CREDIT,"","", 0,false);
        callTransact(currentMpsTransaction, AppConstants.TransactionState.cancel);

    }

    @OnClick(R.id.btn_reprint)
    public void onBtnReprint(){
        if(clientReceipt.equals("")){
            Toast.makeText(this, getResources().getString(R.string.empty_receipt), Toast.LENGTH_SHORT).show();
        }else {
            dialogHelper.showReprintDialog();
        }
    }
    MPSTransaction.TransactionMode transactionMode;

    @OnClick(R.id.btn_pay)
    public void onBtnPay(){
        if (DEFAULT_USE_PP) {
            transactionMode = getSelectedType(mTypeRadioGroup);
        }
        if(transactionMode.equals(MPSTransaction.TransactionMode.CREDIT)){
            dialogHelper.showTransactionTypeDialog(transactionMode);
        }
        else{
            makePayment(transactionMode,defaultInstalments);
        }
    }


    private MPSTransaction.TransactionMode getSelectedType(RadioGroup mTypeRadioGroup) {
        int radioButtonID = mTypeRadioGroup.getCheckedRadioButtonId();
        View radioButton = mTypeRadioGroup.findViewById(radioButtonID);
        int indexChecked = mTypeRadioGroup.indexOfChild(radioButton);

        MPSTransaction.TransactionMode transactionMode = null;
        switch (indexChecked) {
            case RADIO_GROUP_CREDIT:
                transactionMode = MPSTransaction.TransactionMode.CREDIT;
                break;
            case RADIO_GROUP_DEBIT:
                transactionMode = MPSTransaction.TransactionMode.DEBIT;
                break;
            case RADIO_GROUP_VOUCHER:
                transactionMode = MPSTransaction.TransactionMode.VOUCHER;
                break;
        }
        return transactionMode;
    }


    private void createToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static String currentValue = "";
    private static MPSTransaction.TransactionMode typePayment;
    private String currentNumericValue = "";

    private void makePayment(MPSTransaction.TransactionMode type, int installmentsNumber) {
        currentNumericValue = mTextValue.getText().toString();
        String value = FormatUtils.getValueReplaced(currentNumericValue);
        currentValue = value;
        typePayment = type;

        if (value.equals("") || value.equals("000")) {
            createToast(getString(R.string.empty_value));
        }else{
            Log.d(TAG,"Make payment value = "+value);

            MPSTransaction transaction = createTransaction(currentValue,typePayment,"", "", installmentsNumber,rate);
            callTransact(transaction, AppConstants.TransactionState.payment);
        }
    }

    private MPSTransaction createTransaction(String value, MPSTransaction.TransactionMode type,
                                             String cv, String aut,int installmentsNumber, boolean rate) {
        MPSTransaction transaction = new MPSTransaction();
        transaction.setAmount(value);
        transaction.setCurrency(MPSTransaction.CurrencyType.BRL);
        transaction.setInstallments(installmentsNumber);
        transaction.setTransactionMode(type);
        transaction.setCv(cv);
        transaction.setAuth(aut);
        transaction.setRate(rate);
        transaction.setMerchantId(merchantId);

        return transaction;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mpsManager == null){
            Log.d(TAG, "Instantiating  mpsManager ");
            mpsManager = new MuxiPayManager(this.getApplicationContext());
        }
        mpsManager.setMpsManagerCallback(new CallbackAnswer(){
            @Override
            public void onInitAnswer(final MPSResult mpsResult) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String text;
                        if(mpsResult.getStatus() == MPSResult.Status.SUCCESS) {
                            text = getResources().getString(R.string.initialize_success);
                        }else{
                            text = mpsResult.getDescriptionError();
                        }
                        createToast(text);

                    }
                });
            }

            @Override
            public void onTransactionAnswer(final MPSResult mpsResult) {
                resultOfCall = mpsResult;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                        mTextValue.setSelection(DEFAULT_POSITION);
                        if(resultOfCall != null){
                            if (resultOfCall.getStatus() == MPSResult.Status.SUCCESS){
                                Log.d(TAG,"MAIN RECEIPT " + resultOfCall.getClientReceipt());
                                dialogHelper.showTransactionDialog(getResources().getString(R.string.payment_approved),
                                        true, resultOfCall.getClientReceipt(),
                                        resultOfCall.getEstablishmentReceipt());
                                String date = FormatUtils.getCurrentDate();
                                String time = FormatUtils.getCurrentTime(false);
                                String dateTime = date+" "+time;
                                String valueLastTransaction = getResources().getString(R.string.prefix_currency)
                                        + currentNumericValue;
                                setLastTransactionData(getResources().getString(R.string.tv_last_transaction),typePayment.name(),
                                        valueLastTransaction,
                                        dateTime,
                                        getResources().getString(R.string.value_default), resultOfCall.getClientReceipt(),
                                        resultOfCall.getEstablishmentReceipt());
                            }else{
                                Log.d(TAG,"onError " + resultOfCall.getDescriptionError());
                                String descriptionError = resultOfCall.getDescriptionError();
                                dialogHelper.showTransactionDialog(descriptionError, false,descriptionError,"");
                            }
                        }else{
                            createToast(getString(R.string.generic_error));
                            Log.d(TAG,"Result of call null");
                        }
                    }
                });

            }

            @Override
            public void onCancelAnswer(MPSResult mpsResult) {
                resultOfCall = mpsResult;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                        if (resultOfCall != null) {
                            if (resultOfCall.getStatus() == MPSResult.Status.SUCCESS) {
                                Log.d(TAG, "MAIN RECEIPT " + resultOfCall.getClientReceipt());
                                dialogHelper.showTransactionDialog(getResources().getString(R.string.cancel_approved), true,
                                        resultOfCall.getClientReceipt(), resultOfCall.getEstablishmentReceipt());
                                setLastTransactionData(getResources().getString(R.string.tv_no_last_transaction), "",
                                        "",
                                        "",
                                        getResources().getString(R.string.value_default), resultOfCall.getClientReceipt(), resultOfCall.getEstablishmentReceipt());
                            } else {
                                String descriptionError = resultOfCall.getDescriptionError();
                                Log.d(TAG, "onError " + descriptionError);
                                dialogHelper.showTransactionDialog(descriptionError, false, descriptionError, "");
                            }
                        }else{
                            createToast(getResources().getString(R.string.generic_error));
                            Log.d(TAG,"Result of call null");
                        }
                    }
                });
            }

            @Override
            public void onDeconfigureAnswer(MPSResult mpsResult) {
                if(mpsResult.getStatus() == MPSResult.Status.SUCCESS){
                    bluetoothList.setWhenDeconfigure();
                    bluetoothList.updateItemsOnSpinner(adapter);
                    setLastTransactionData(getResources().getString(R.string.tv_no_last_transaction),"",
                            "",
                            "",
                            getResources().getString(R.string.value_default),"", "");
                    createToast(getResources().getString(R.string.deconfig_success));
                }else{
                    createToast(getResources().getString(R.string.deconfig_error));
                }
            }

            @Override
            public void onGenericCommand(MPSResult mpsResult) {
                resultOfCall = mpsResult;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                        if(resultOfCall != null) {
                            if (resultOfCall.getStatus() == MPSResult.Status.SUCCESS){
                                if (resultOfCall.getGenericMessage() != null ){
                                    if (resultOfCall.getGenericMessage().contains(GENERIC_CMD_GET_VERSIONS)){
                                        String applicationVersion = resultOfCall.getApplicationVersion();
                                        String poswebVersion = resultOfCall.getPoswebVersion();
                                        dialogHelper.showVersionsDialog(applicationVersion, poswebVersion);
                                    }
                                }

                            } else {
                                String descriptionError = resultOfCall.getDescriptionError();
                                Log.d(TAG, "onError " + descriptionError);
                                createToast(descriptionError);
                            }
                        } else {
                            createToast(getString(R.string.generic_error));
                            Log.d(TAG,"Result of call null");
                        }
                    }
                });
            }

            @Override
            public void onPrintAnswer(MPSResult mpsResult) {
                super.onPrintAnswer(mpsResult);
            }


            @Override
            public void onServiceDisconnected() {
                Log.d(TAG,"onServiceDisconnected sample");
                createToast(getResources().getString(R.string.service_not_initialized));
            }

            @Override
            public void onServiceConnected() {
                super.onServiceConnected();
            }
        });

        PinpadDevicePojo pinpadDevice = Utils.getCachedPinpadDevice(this);
        if (!ppConfigured && pinpadDevice != null){
            ArrayList<String> btlist = bluetoothList.getListPairedNames();
            if (btlist != null && btlist.size() > 0){
                for ( int i=0; i<btlist.size(); i++){
                    String deviceName = btlist.get(i);
                    if (pinpadDevice.getDeviceName().equals(deviceName)){
                        pinpadDeviceSpinner.setSelection(i);
                        ppConfigured = true;
                        break;
                    }
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        mpsManager.stopService(getApplicationContext());
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onClickPay(MPSTransaction.TransactionMode transactionMode, int installmentsNumber, boolean isAdmRate) {
        this.rate = isAdmRate;
        this.transactionMode = transactionMode;
        this.defaultInstalments = installmentsNumber;
        makePayment(transactionMode,installmentsNumber);
    }

    @Override
    public void onClickEstablishment(String numberOfEstablishment) {
        Utils.cacheMerchantId(this, numberOfEstablishment);
        createToast(getResources().getString(R.string.changed_merchant_id));
        updateMerchantId(numberOfEstablishment);
    }

    @Override
    public void onClickVoidAny(RadioGroup typePaymentChecked, String cv, String auth) {
        MPSTransaction.TransactionMode transactionMode;
        transactionMode = getSelectedType(typePaymentChecked);
        Log.d(TAG,"Transaction type for cancel "+ transactionMode.name());
        currentMpsTransaction = createTransaction("", transactionMode,
                cv, auth, defaultInstalments,rate);

        callTransact(currentMpsTransaction, AppConstants.TransactionState.cancel);
    }

    private void callTransact(MPSTransaction currentMpsTransaction, AppConstants.TransactionState type) {
        TransactionTask transactionTask = new TransactionTask(mProgressDialog,
                mpsManager, currentMpsTransaction, type);
        transactionTask.execute();
    }

    @Override
    public void onClickReprint(boolean isEstablishmentReceipt) {
        ReceiptType receiptType = isEstablishmentReceipt ? ReceiptType.ESTABLISHMENT:ReceiptType.CUSTOMER;
        mpsManager.reprintLastTransaction(receiptType);
    }

    @Override
    public void onClickPrintCustomer() {
        mpsManager.printCustomerReceipt();
    }

    @Override
    public void onClickGenericCommand(String command, String params) {
        mpsManager.genericCommand(command, params);
    }
}
