package com.puttel.app.registration;

import android.support.annotation.VisibleForTesting;
import com.puttel.app.InApp;
import com.puttel.app.PreferencesUtil;
import com.puttel.app.R;
import com.puttel.app.interactor.CreateAccountInteractor;
import com.puttel.app.interactor.GetAccountInteractor;
import com.puttel.app.iroha.IrohaAPI;
import lombok.Setter;

import javax.inject.Inject;

public class RegistrationPresenter {
    @Setter
    private RegistrationView view;
    private final PreferencesUtil preferencesUtil;
    private final CreateAccountInteractor createAccountInteractor;
    private final GetAccountInteractor getAccountInteractor;

    @VisibleForTesting
    public boolean isRequestFinished;

    @Inject
    public RegistrationPresenter(PreferencesUtil preferencesUtil,
                                 CreateAccountInteractor createAccountInteractor,
                                 GetAccountInteractor getAccountInteractor){
        this.preferencesUtil = preferencesUtil;
        this.createAccountInteractor = createAccountInteractor;
        this.getAccountInteractor = getAccountInteractor;
    }
    void createAccount(String username) {
        isRequestFinished = false;

        if (!username.isEmpty()) {
            System.out.println(username+"----<<<<<");
            IrohaAPI api = new IrohaAPI();
            getAccountInteractor.execute(username, account -> {

            }, this::didRegistrationError);

        }else {
            didRegistrationError(new Throwable(InApp.instance.getString(R.string.username_empty_error_dialog_message)));
        }
    }
    private void didRegistrationError(Throwable throwable) {
        isRequestFinished = true;
        preferencesUtil.clear();
        view.didRegistrationError(throwable);
    }
    boolean isRegistered() {
        return !preferencesUtil.retrieveUsername().isEmpty();
    }
    void onStop() {
        view = null;
    }
}
