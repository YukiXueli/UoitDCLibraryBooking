package com.objectivetruth.uoitlibrarybooking.app;

import com.objectivetruth.uoitlibrarybooking.ActivityRoomInteraction;
import com.objectivetruth.uoitlibrarybooking.Calendar_Generic_Page_Fragment;
import com.objectivetruth.uoitlibrarybooking.MainActivity;
import com.objectivetruth.uoitlibrarybooking.data.DataModule;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserWebService;
import com.objectivetruth.uoitlibrarybooking.userinterface.calendar.Calendar;
import com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.MyAccount;
import com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.login.LoginFragment;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AppModule.class, DataModule.class})
public interface AppComponent {
    void inject(UOITLibraryBookingApp app);
    void inject(MainActivity mainActivity);
    void inject(Calendar_Generic_Page_Fragment.RoomFragmentDialog roomFragmentDialog);
    void inject(ActivityRoomInteraction activityRoomInteraction);
    void inject(Calendar calendar);
    void inject(MyAccount myAccount);
    void inject(LoginFragment loginFragment);
    void inject(UserWebService userWebService);
}
