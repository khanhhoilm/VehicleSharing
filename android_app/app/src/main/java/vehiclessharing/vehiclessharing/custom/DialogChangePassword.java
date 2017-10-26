/*
package vehiclessharing.vehiclessharing.custom;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;

import vehiclessharing.vehiclessharing.R;


*/
/**
 * Created by Tuan on 15/05/2017.
 *//*


public class DialogChangePassword extends Dialog implements View.OnClickListener{
    public Activity c;
    public Dialog d;
    public  Button btnCancelChangePW;
    public Button btnSubmitChangePW;
    public EditText edCurrentPW;
    public EditText edNewPW;
    EditText edComfirmPW;
    private ProgressDialog progressDialog;

    public DialogChangePassword(Activity c){
        super(c);
        this.c = c;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.change_password_dialog);

        addControlls();
        addEvents();

    }

    private void addControlls() {
        edCurrentPW = (EditText) findViewById(R.id.ed_current_password);
        edNewPW = (EditText) findViewById(R.id.ed_new_password);
        edComfirmPW = (EditText) findViewById(R.id.ed_comfirm_password);
        btnCancelChangePW = (Button) findViewById(R.id.btn_cancel);
        btnSubmitChangePW = (Button) findViewById(R.id.btn_submit);

        //[Start] Setup for progress
        progressDialog = new ProgressDialog(c);
        progressDialog.setTitle(Utils.ChangePassword);
        progressDialog.setMessage(Utils.PleaseWait);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        //[End] Setup for progress
    }

    private void addEvents() {
        btnCancelChangePW.setOnClickListener(this);
        btnSubmitChangePW.setOnClickListener(this);
    }

    */
/**
     * Check valid for current + new + comfirm password
     * @return
     *//*

    private boolean checkValid(){
        Validation validation = null;
        if(edCurrentPW.getText().toString().isEmpty())
            Toast.makeText(c,"Field current password not empty",Toast.LENGTH_SHORT).show();
        else {
            if(edNewPW.getText().toString().isEmpty())
                Toast.makeText(c,"Field new password not empty",Toast.LENGTH_SHORT).show();
            else {
                if(edComfirmPW.getText().toString().isEmpty())
                    Toast.makeText(c,"Field comfirm password not empty",Toast.LENGTH_SHORT).show();
                else {
                    validation= Validation.checkValidPassword(edNewPW.getText().toString());
                    if(!validation.getIsValid())
                        Toast.makeText(c,validation.getMessageValid(),Toast.LENGTH_SHORT).show();
                    else {
                        validation = Validation.checkValidConfirmPassword(edNewPW.getText().toString(),edComfirmPW.getText().toString());
                        if(!validation.getIsValid())
                            Toast.makeText(c,validation.getMessageValid(),Toast.LENGTH_SHORT).show();
                        else return true;
                    }
                }

            }

        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_cancel: {
                dismiss();
                break;
            }
            case R.id.btn_submit: {
                if(checkValid()){
                    progressDialog.show();
                    // Get auth credentials from the user for re-authentication. The example below shows
                    // email and password credentials but there are multiple possible providers,
                    // such as GoogleAuthProvider or FacebookAuthProvider.
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(MainActivity.mUser.getEmail(), String.valueOf(edCurrentPW.getText().toString()));

                    Log.d("authcre_aaaaaa",String.valueOf(credential));

                    // Prompt the user to re-provide their sign-in credentials
                    MainActivity.mUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {

                                        MainActivity.mUser.updatePassword(edComfirmPW.getText().toString())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            dismiss();
                                                            Toast.makeText(c,"User password updated.",Toast.LENGTH_SHORT).show();
                                                        } else
                                                            Toast.makeText(c,String.valueOf(task.getException().getMessage()),Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();

                                                    }
                                                });
                                    }
                                    else {
                                        progressDialog.dismiss();
                                        Toast.makeText(c,String.valueOf(task.getException().getMessage()),Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });

                }
                break;
            }
        }

    }
}
*/
