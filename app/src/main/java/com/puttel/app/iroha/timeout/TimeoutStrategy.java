package com.puttel.app.iroha.timeout;

public interface TimeoutStrategy {

    /**
     * Execute next timeout.
     *
     * @return true, if you want to continue re-subscription loop, false otherwise
     */
    boolean nextTimeout() throws InterruptedException;
}