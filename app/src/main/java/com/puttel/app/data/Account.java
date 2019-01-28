package com.puttel.app.data;

import iroha.protocol.QryResponses;
import lombok.Getter;
import lombok.Setter;

public class Account {
    @Setter
    @Getter
    private QryResponses.Account irohaAccount;

    @Setter
    @Getter
    private long balance;

    public Account(QryResponses.Account account, long balance) {
        this.irohaAccount = account;
        this.balance = balance;
    }
}
