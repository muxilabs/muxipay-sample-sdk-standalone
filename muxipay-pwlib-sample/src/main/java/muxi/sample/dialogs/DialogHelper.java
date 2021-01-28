package muxi.sample.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import muxi.sample.AppConstants;
import muxi.sample.R;
import muxi.payservices.sdk.data.MPSTransaction;


public class DialogHelper extends BaseDialog {

    private static final int ADMIN_RATE = 1;

    private static final String TAG = DialogHelper.class.getSimpleName();
    private Context context;
    private DialogCallback dialogCallback;

    private static final boolean DEFAULT_RATE_ADMIN = false;
    private static final int DEFAULT_INSTALMENTS = 1;

    private int VISTA = 0;
    private int MINIMUM_INSTALLMENTS = 2;

    private SharedPreferences.Editor editor;

    public DialogHelper(Context context, DialogCallback dialogCallback){
        this.context = context;
        this.dialogCallback = dialogCallback;
    }

    @Override
    public void createDialog(AlertDialog.Builder builder) {
        super.createDialog(builder);
    }

    @Override
    public void showDialog(AlertDialog.Builder builder) {
        super.showDialog(builder);
    }


    public void showVersionsDialog(String applicationVersion, String poswebVersion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater factory = LayoutInflater.from(context);
        final View view = factory.inflate(R.layout.dialog_versions,null);
        builder.setView(view);

        TextView tvApplicationVer = view.findViewById(R.id.tv_application_ver);
        tvApplicationVer.setText(applicationVersion);

        TextView tvPoswebVer = view.findViewById(R.id.tv_posweb_ver);
        tvPoswebVer.setText(poswebVersion);

        builder.setPositiveButton(android.R.string.ok, (dialogInterface, i) ->
                dialogInterface.dismiss());

        createDialog(builder);
        showDialog(builder);
    }

    public void showTransactionDialog(String textTitle, boolean status, final String clientReceipt,
                                      final String establishmentReceipt) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if(status) {
            ImageView imageView = new ImageView(context);
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_payment_ok));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setMaxHeight(40);
            imageView.setMaxWidth(40);
            imageView.setPadding(10,30,10,30);

            builder.setView(imageView);

            // Set Custom Title
            TextView title = new TextView(context);
            // Title Properties
            title.setText(textTitle);
            title.setPadding(10, 20, 10, 10);   // Set Position
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.BLACK);
            title.setTextSize(24);
            builder.setCustomTitle(title);

            builder.setPositiveButton(context.getResources().getString(R.string.dialog_ok), (dialog, which) -> dialog.dismiss());
            builder.setNeutralButton(context.getResources().getString(R.string.client_receipt), (dialog, which) ->
                    showReceiptDialog(clientReceipt, establishmentReceipt, true));
            builder.setNegativeButton(context.getResources().getString(R.string.establishment_receipt), (dialog, which) ->
                    showReceiptDialog(clientReceipt, establishmentReceipt, false));
        } else {

            ImageView imageView = new ImageView(context);
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_payment_nok));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setMaxHeight(40);
            imageView.setMaxWidth(40);
            imageView.setPadding(10,30,10,30);

            builder.setView(imageView);

            // Set Custom Title
            TextView title = new TextView(context);
            // Title Properties
            title.setText(textTitle);
            title.setPadding(10, 20, 10, 10);   // Set Position
            title.setGravity(Gravity.CENTER);
            title.setTextColor(Color.BLACK);
            title.setTextSize(24);
            builder.setCustomTitle(title);

            builder.setPositiveButton(context.getResources().getString(R.string.dialog_ok), (dialog, which) -> dialog.dismiss());
        }
        createDialog(builder);
        showDialog(builder);

    }

    public void showVoidAnyDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater factory = LayoutInflater.from(context);
        final View view = factory.inflate(R.layout.dialog_cancel,null);
        builder.setMessage(R.string.btn_cancel);
        builder.setView(view);

        final EditText etCv =  view.findViewById(R.id.et_cv);
        final EditText etAut =  view.findViewById(R.id.et_aut);
        final RadioGroup mTypeRadioGroupCancel = view.findViewById(R.id.radioGroupCancel);
        mTypeRadioGroupCancel.check(R.id.cancelRadioButton_credit);
        builder.setPositiveButton(R.string.dialog_ok,
                (dialogInterface, i) -> {
                    String cv = etCv.getText().toString();
                    String aut = etAut.getText().toString();
                    if(cv.isEmpty()){
                        Toast.makeText(context, context.getString(R.string.empty_cv), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(aut.isEmpty()){
                        Toast.makeText(context, context.getString(R.string.empty_aut), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dialogCallback.onClickVoidAny(mTypeRadioGroupCancel,etCv.getText().toString(), etAut.getText().toString());
                });
        builder.setNegativeButton(R.string.back, (dialogInterface, i) -> dialogInterface.dismiss());

        createDialog(builder);
        showDialog(builder);
    }

    @SuppressLint("NewApi")
    public void showReceiptDialog(final String clientReceipt, final String establishmentReceipt, final boolean isClientReceipt) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LinearLayout layout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(AppConstants.RECEIPT_MARGIN_LEFT,AppConstants.RECEIPT_MARGIN_TOP,
                AppConstants.RECEIPT_MARGIN_RIGHT,AppConstants.RECEIPT_MARGIN_BOTTOM);
        layout.setOrientation(LinearLayout.VERTICAL);

        String title = context.getString(R.string.establishment_receipt);
        String receipt = establishmentReceipt;
        String anotherReceipt = context.getString(R.string.client_receipt);
        if(isClientReceipt){
            title = context.getString(R.string.client_receipt);
            receipt = clientReceipt;
            anotherReceipt = context.getString(R.string.establishment_receipt);
            //TODO adicionar um botao para imprimir
            dialogCallback.onClickPrintCustomer();
        }
        builder.setTitle(title);

        TextView tv_receipt  = new TextView(context);
        tv_receipt.setTypeface(Typeface.MONOSPACE);
        tv_receipt.setTextSize(12);
        tv_receipt.setText(receipt);
        tv_receipt.setTextColor(context.getResources().getColor(R.color.black));
        layout.addView(tv_receipt,layoutParams);

        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(layout);
        builder.setView(scrollView);

        builder.setPositiveButton(context.getResources().getString(R.string.dialog_ok), (dialog, whichButton) -> dialog.dismiss());

        builder.setNeutralButton(anotherReceipt, (dialog, which) ->
                showReceiptDialog(clientReceipt,establishmentReceipt,!isClientReceipt));
        builder.setNegativeButton(context.getResources().getString(R.string.send_email), (dialog, which) -> {
            dialog.dismiss();
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType(context.getResources().getString(R.string.email_type));
            i.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.client_receipt));
            i.putExtra(Intent.EXTRA_TEXT   , establishmentReceipt);
            try {
                context.startActivity(Intent.createChooser(i, context.getResources().getString(R.string.send_email)));
            } catch (android.content.ActivityNotFoundException ex) {
                Log.e(TAG,"There are no email clients installed.");
            }
        });

        createDialog(builder);
        showDialog(builder);
    }

    public void showReprintDialog(){
        final int[] indexChecked = {-1};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.title_reprint_dialog));
        LayoutInflater factory = LayoutInflater.from(context);
        final View view = factory.inflate(R.layout.dialog_rate,null);
        builder.setView(view);
        final RadioGroup mTypeRadioGroupCancel = view.findViewById(R.id.radioGroupRate);
        RadioButton rbCustomer = view.findViewById(R.id.radioButton_loja);
        RadioButton rbEstab = view.findViewById(R.id.radioButton_adm);
        rbCustomer.setText("Cliente");
        rbEstab.setText("Estabelecimento");
        mTypeRadioGroupCancel.check(R.id.radioButton_loja);
        builder.setPositiveButton(context.getResources().getString(android.R.string.ok),
                (dialogInterface, i) -> {
                    int radioButtonID = mTypeRadioGroupCancel.getCheckedRadioButtonId();
                    View radioButton = mTypeRadioGroupCancel.findViewById(radioButtonID);
                    indexChecked[0] = mTypeRadioGroupCancel.indexOfChild(radioButton);
                    boolean isEstablishment = indexChecked[0] != 0;
                    dialogCallback.onClickReprint(isEstablishment);
                });
        builder.setCancelable(true);
        createDialog(builder);
        showDialog(builder);

    }
    public void showRateDialog(final MPSTransaction.TransactionMode transactionMode, final int installmentsNumber) {
        final int[] indexChecked = {-1};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.title_rate_dialog));
        LayoutInflater factory = LayoutInflater.from(context);
        final View view = factory.inflate(R.layout.dialog_rate,null);
        builder.setView(view);

        final RadioGroup mTypeRadioGroupCancel = view.findViewById(R.id.radioGroupRate);
        mTypeRadioGroupCancel.check(R.id.radioButton_loja);

        builder.setPositiveButton(context.getResources().getString(android.R.string.ok),
                (dialogInterface, i) -> {
                    int radioButtonID = mTypeRadioGroupCancel.getCheckedRadioButtonId();
                    View radioButton = mTypeRadioGroupCancel.findViewById(radioButtonID);
                    indexChecked[0] = mTypeRadioGroupCancel.indexOfChild(radioButton);
                    boolean rate = false;
                    if(indexChecked[0] == ADMIN_RATE)
                        rate = true;
                    dialogCallback.onClickPay(transactionMode,installmentsNumber, rate);
                });
        builder.setCancelable(true);
        createDialog(builder);
        showDialog(builder);
    }


    public void showTransactionTypeDialog(final MPSTransaction.TransactionMode transactionMode){

        LayoutInflater factory = LayoutInflater.from(context);

        View view = factory.inflate(R.layout.radio_btn_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.title_vista_install));
        builder.setView(view);
        final RadioGroup rg = view.findViewById(R.id.rg_dialog);
        rg.check(R.id.rb_vista);
        builder.setCancelable(true);
        builder.setPositiveButton(context.getResources().getString(R.string.dialog_ok),
                (dialog, which) -> {
                    int radioButtonID = rg.getCheckedRadioButtonId();
                    View radioButton = rg.findViewById(radioButtonID);
                    int indexChecked = rg.indexOfChild(radioButton);
                    if(indexChecked == VISTA){
                        dialogCallback.onClickPay(transactionMode,DEFAULT_INSTALMENTS,DEFAULT_RATE_ADMIN);
                    }else{
                        showInstallmentsDialog(transactionMode);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showInstallmentsDialog(final MPSTransaction.TransactionMode transactionMode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.title_installments_dialog));
        final EditText installments = new EditText(context);
        installments.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        installments.setInputType(InputType.TYPE_CLASS_NUMBER);
        installments.setFilters(new InputFilter[] {new InputFilter.LengthFilter(3)});
        builder.setView(installments);
        builder.setCancelable(true);
        builder.setPositiveButton(context.getResources().getString(R.string.dialog_ok),null);
        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(dialog -> {
            Button buttonPositive = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
            buttonPositive.setOnClickListener(v -> {
                int install = Integer.parseInt(installments.getText().toString());
                if(install < MINIMUM_INSTALLMENTS){
                    installments.setError("Choose at least 2 installments");
                }else{
                    showRateDialog(transactionMode,install);
                    alertDialog.dismiss();
                }
            });
        });
        alertDialog.show();
    }

    public void showEstablishmentDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.type_merchant_id);

        final EditText input = new EditText(context);
        input.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        builder.setView(input);
        builder.setPositiveButton(R.string.modify,
                (dialogInterface, i) -> {
                    dialogCallback.onClickEstablishment(input.getText().toString());
                });
        builder.setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss());

        createDialog(builder);
        showDialog(builder);
    }

    public void showGenericCommandDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.navdrawer_genericCommand);

        LinearLayout baseLayout = new LinearLayout(context);
        baseLayout.setOrientation(LinearLayout.VERTICAL);

        final TextView commandText = new TextView(context);
        commandText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        commandText.setText(R.string.command);

        final EditText commandInput = new EditText(context);
        commandInput.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        commandInput.setInputType(InputType.TYPE_CLASS_TEXT);
        commandInput.setHint(R.string.hint_command_exemple);

        final TextView paramsText = new TextView(context);
        paramsText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        paramsText.setText(R.string.params);

        final EditText paramsInput = new EditText(context);
        paramsInput.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        paramsInput.setInputType(InputType.TYPE_CLASS_TEXT);
        paramsInput.setHint(R.string.hint_params_exemple);

        baseLayout.addView(commandText);
        baseLayout.addView(commandInput);
        baseLayout.addView(paramsText);
        baseLayout.addView(paramsInput);
        builder.setView(baseLayout);


        builder.setPositiveButton(R.string.dialog_ok,
                (dialogInterface, i) -> {
                    dialogCallback.onClickGenericCommand(commandInput.getText().toString(), paramsInput.getText().toString());
                });
        builder.setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss());

        createDialog(builder);
        showDialog(builder);
    }

}
