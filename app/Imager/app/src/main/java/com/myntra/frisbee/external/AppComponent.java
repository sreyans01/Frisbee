package com.myntra.frisbee.external;

import com.myntra.frisbee.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {AppModule.class, UtilsModule.class})
@Singleton
public interface AppComponent {

    void doInjection(MainActivity mainActivity);

}
