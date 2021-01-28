package muxi.sample;

 public interface AppConstants {

    boolean DEFAULT_USE_PP = true;

    int RADIO_GROUP_CREDIT = 0;
    int RADIO_GROUP_DEBIT = 1;
    int RADIO_GROUP_VOUCHER= 2;

    int SET_MASK_CURRENCY_HELPER = 3 ;
    String REGEX_MATCHES = "^(\\d+\\.\\d{2})?$";
    String REGEX_REPLACE_ALL = "[^\\d]";

    enum TransactionState {
       payment,
       cancel
    }

    int RECEIPT_MARGIN_LEFT = 60;
    int RECEIPT_MARGIN_TOP = 15;
    int RECEIPT_MARGIN_RIGHT = 0;
    int RECEIPT_MARGIN_BOTTOM = 0;

    String DESENV_MERCHANT_ID = "9876";

    String PP_SELECT_DEVICE = "Selecione o pinpad";
    String PP_MUXI_EMV = "Muxi-EMV";
    String INTER_PP_NAME = "Pinpad interno";
}