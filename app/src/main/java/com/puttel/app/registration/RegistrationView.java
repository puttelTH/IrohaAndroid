package com.puttel.app.registration;

public interface RegistrationView {
    void didRegistrationSuccess();

    void didRegistrationError(Throwable error);

    void showProgressDialog();

    void dismissProgressDialog();
}
