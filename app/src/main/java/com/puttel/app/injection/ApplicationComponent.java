package com.puttel.app.injection;

import javax.inject.Singleton;

import com.puttel.app.main.MainActivity;
import com.puttel.app.registration.RegistrationActivity;
import dagger.Component;
import dagger.android.AndroidInjector;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent extends AndroidInjector{
    void inject(MainActivity mainActivity);
    void inject(RegistrationActivity registrationActivity);
}
