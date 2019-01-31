package com.puttel.app.registration;

import android.support.annotation.VisibleForTesting;
import com.puttel.app.InApp;
import com.puttel.app.PreferencesUtil;
import com.puttel.app.R;
import com.puttel.app.interactor.CreateAccountInteractor;
import com.puttel.app.interactor.GetAccountInteractor;
import com.puttel.app.iroha.IrohaAPI;
import com.puttel.app.iroha.Query;
import iroha.protocol.QryResponses;
import iroha.protocol.Queries;
import jp.co.soramitsu.crypto.ed25519.Ed25519Sha3;
import lombok.Setter;

import javax.inject.Inject;
import java.security.KeyPair;

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
            getAccountInteractor.execute(username, account -> {
                System.out.println(account.getAccountId()+"----<<<<<");
                if (account.getAccountId().isEmpty()){

                }else{
                    didRegistrationError(new Throwable("Exit"));
                }

            }, this::didRegistrationError);

        }else {
            didRegistrationError(new Throwable("Not null"));
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
