package com.puttel.app.registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import com.jakewharton.rxbinding2.view.RxView;
import com.puttel.app.InApp;
import com.puttel.app.R;
import com.puttel.app.databinding.ActivityRegistrationBinding;
import com.puttel.app.main.MainActivity;

import javax.inject.Inject;
import java.net.ConnectException;

public class RegistrationActivity extends AppCompatActivity implements RegistrationView{
    private ActivityRegistrationBinding binding;

    @Inject
    RegistrationPresenter registrationPresenter;

    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration);
        InApp.instance.applicationComponent.inject(this);

        //registrationPresenter.setView(this);
        if (registrationPresenter.isRegistered()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        createProgressDialog();
        RxView.clicks(binding.registerButton)
                .subscribe(view -> {
                    showProgressDialog();
                    registrationPresenter.createAccount(binding.username.getText().toString().trim());
                });

    }
    private void createProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.please_wait));
    }
    @Override
    public void didRegistrationSuccess() {
        dismissProgressDialog();
        Intent intent = new Intent(this, MainActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, binding.logoImage, "profile");
        startActivity(intent, options.toBundle());
        finish();
    }
    @Override
    public void didRegistrationError(Throwable error) {
        dismissProgressDialog();
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.error_dialog_title))
                .setMessage(error.getCause() instanceof ConnectException ?
                        getString(R.string.general_error) :
                        error.getLocalizedMessage())
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    if (error.getCause() instanceof ConnectException) {
                        finish();
                    }
                })
                .create();
        alertDialog.show();
    }
    @Override
    public void onStop() {
        super.onStop();
        registrationPresenter.onStop();
    }

    @Override
    public void showProgressDialog() {
        dialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        dialog.dismiss();
    }
}
