package com.puttel.app.iroha.timeout;

public class OneSecondTimeout implements TimeoutStrategy {

    @Override
    public boolean nextTimeout() throws InterruptedException {
        Thread.sleep(1000);
        return true;
    }
}
