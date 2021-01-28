package muxi.sample.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import muxi.payservices.sdk.service.IMPSManager;

public class InitTask extends AsyncTask<Void,Void,Void> {

    private ProgressDialog progressDialog;
    private String merchantId;
    private boolean defaultPinpadmsg;
    private IMPSManager mpsManager;
    private String token;
    private String message;


    public InitTask(ProgressDialog progressDialog,
                    String merchantId,
                    boolean defaultPinpadMsg,
                    IMPSManager mpsManager,
                    String token,
                    String message) {
        this.progressDialog = progressDialog;
        this.merchantId = merchantId;
        this.defaultPinpadmsg = defaultPinpadMsg;
        this.mpsManager = mpsManager;
        this.token = token;
        this.message = message;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        mpsManager.initialize(defaultPinpadmsg,merchantId,token);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();
    }

}
