package muxi.sample.dialogs;


import android.widget.RadioGroup;

import muxi.payservices.sdk.data.MPSTransaction;


public interface DialogCallback {
    void onClickPay(MPSTransaction.TransactionMode transactionMode,
                    int installmentsNumber, boolean isAdmRate);
    void onClickEstablishment(String numberOfEstablishment);
    void onClickVoidAny(RadioGroup typePaymentChecked,String cv,String auth);
    void onClickReprint(boolean isEstablishmentReceipt);
    void onClickGenericCommand(String command, String params);

    void onClickPrintCustomer();
}


