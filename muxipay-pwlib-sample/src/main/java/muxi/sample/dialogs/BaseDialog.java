package muxi.sample.dialogs;


import androidx.appcompat.app.AlertDialog;

public abstract class BaseDialog {

    public void createDialog(AlertDialog.Builder builder){
        builder.create();
    }
    public void showDialog(AlertDialog.Builder builder){
        builder.show();
    }
}
