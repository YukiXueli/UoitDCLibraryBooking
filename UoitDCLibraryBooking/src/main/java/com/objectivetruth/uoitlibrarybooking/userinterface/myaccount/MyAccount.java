package com.objectivetruth.uoitlibrarybooking.userinterface.myaccount;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.objectivetruth.uoitlibrarybooking.R;
import com.objectivetruth.uoitlibrarybooking.app.UOITLibraryBookingApp;
import com.objectivetruth.uoitlibrarybooking.data.models.UserModel;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserCredentials;
import com.objectivetruth.uoitlibrarybooking.data.models.usermodel.UserData;
import com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.login.LoginFragment;
import com.objectivetruth.uoitlibrarybooking.userinterface.myaccount.myaccountloaded.MyAccountLoaded;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import javax.inject.Inject;

public class MyAccount extends Fragment {
    private PublishSubject<UserCredentials> signInClickSubject;
    private PublishSubject<String> loginErrorSubject;
    private PublishSubject<Object> logoutClickedSubject;
    @Inject UserModel userModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((UOITLibraryBookingApp) getActivity().getApplication()).getComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_account, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ImageButton logoutButton = (ImageButton) view.findViewById(R.id.my_account_logout);

        if(userModel.isUserSignedIn()) {
            Timber.i("User is already signed in, showing his details");
            _showMyAccountLoadedFragment();
        }else {
            Timber.i("User isn't signed in, showing login screen");
            _showLoginFragment(_getSignInClickSubject());
        }
    }

    private void _subscribeToSignInClickSubject(PublishSubject<UserCredentials> signInClickSubject) {
        signInClickSubject
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())

                .flatMap(new Func1<UserCredentials, Observable<Pair<UserData, UserCredentials>>>() {
                    @Override
                    public Observable<Pair<UserData, UserCredentials>> call(UserCredentials userCredentials) {
                        return userModel.signIn(userCredentials);
                        }
                    })
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.computation())

                .subscribe(new Observer<Pair<UserData, UserCredentials>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Pair<UserData, UserCredentials> userDataUserCredentialsPair) {
                if(userDataUserCredentialsPair.first.errorMessage != null) {
                    Timber.d("Received Error Message from Parser, publishing to Subject");
                    _sendErrorMessageToLoginErrorSubject(userDataUserCredentialsPair.first.errorMessage,
                            _getLoginErrorSubject());
                }else {
                    Timber.i("Received UserData");
                    Timber.v(userDataUserCredentialsPair.first.toString());
                    _showMyAccountLoadedFragment();
                }
            }
        });
    }

    private static void _sendErrorMessageToLoginErrorSubject(String errorMessage,
                                                            PublishSubject<String> loginErrorSubject) {
        loginErrorSubject.onNext(errorMessage);
    }

    public static MyAccount newInstance() {
        return new MyAccount();
    }

    private void _showMyAccountLoadedFragment() {
        String MY_ACCOUNT_LOADED_FRAGMENT_TAG = "SINGLETON_MY_ACCOUNT_LOADED_FRAGMENT_TAG";
        getFragmentManager().beginTransaction()
                .replace(R.id.my_account_content_frame,
                        MyAccountLoaded.newInstance(userModel.getUserDataFromCache(), _getLogoutClickedSubject()),
                        MY_ACCOUNT_LOADED_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    private void _showLoginFragment(PublishSubject<UserCredentials> signInClickSubject) {
        String MY_ACCOUNT_LOGIN_FRAGMENT_TAG = "SINGLETON_MY_ACCOUNT_LOGIN_FRAGMENT_TAG";
/*        Fragment mLoginFragment = getFragmentManager()
                .findFragmentByTag(MY_ACCOUNT_LOGIN_FRAGMENT_TAG);
        if(mLoginFragment == null){
            Timber.d("Fragment with tag: " + MY_ACCOUNT_LOGIN_FRAGMENT_TAG + " not found, instantiating a new one");
            mLoginFragment = LoginFragment.newInstance();
        }else {
            Timber.d("Fragment with tag: " + MY_ACCOUNT_LOGIN_FRAGMENT_TAG +
                    " found. retrieving it without creating a new one");
        }*/
        _subscribeToSignInClickSubject(signInClickSubject);
        getFragmentManager().beginTransaction()
                .replace(R.id.my_account_content_frame,
                        LoginFragment.newInstance(signInClickSubject, _getLoginErrorSubject()),
                        MY_ACCOUNT_LOGIN_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Replaces the my_account_content_frame with the AccountInfoFragment which loads its info from the database
     */
/*    private void _showAccountInfoFragment(){
        String MY_ACCOUNT_PAGER_ADAPTER_FRAGMENT_TAG = "SINGLETON_MY_ACCOUNT_PAGER_ADAPTER_FRAGMENT_TAG";
        ViewPagerFragment mViewPagerFragment = (ViewPagerFragment) getFragmentManager()
                .findFragmentByTag(MY_ACCOUNT_PAGER_ADAPTER_FRAGMENT_TAG);
        if(mViewPagerFragment ==null ){
            mViewPagerFragment = ViewPagerFragment.newInstance(new CustomViewPagerAdapter(getFragmentManager()));
            getFragmentManager().beginTransaction().
                    replace(R.id.my_account_movement_frame, mViewPagerFragment, MY_ACCOUNT_PAGER_ADAPTER_FRAGMENT_TAG)
                    .commit();
        }
        else{
            //Do nothing Each Tab is registered to Auto to receive an update success callback

        }
    }*/

    private PublishSubject<UserCredentials> _getSignInClickSubject() {
        if(signInClickSubject == null) {
            Timber.d("Current singInClickSubject is NULL, making new one");
            return signInClickSubject = PublishSubject.create();
        }else if (signInClickSubject.hasCompleted()) {
            Timber.d("Current singInClickSubject has completed already, making new one");
            return signInClickSubject = PublishSubject.create();
        }else {
            Timber.d("Current singInClickSubject is still valid, passing it back");
            return signInClickSubject;
        }
    }

    private PublishSubject<String> _getLoginErrorSubject() {
        if(loginErrorSubject == null) {
            Timber.d("Current loginErrorSubject is NULL, making new one");
            return loginErrorSubject = PublishSubject.create();
        }else if (loginErrorSubject.hasCompleted()) {
            Timber.d("Current loginErrorSubject hasCompleted, making new one");
            return loginErrorSubject = PublishSubject.create();
        }else {
            Timber.d("Current loginErrorSubject is still valid, passing it back");
            return loginErrorSubject;
        }
    }

    private PublishSubject<Object> _getLogoutClickedSubject() {
        if(logoutClickedSubject == null) {
            Timber.d("Current logoutClickedSubject is NULL, making new one");
            logoutClickedSubject = PublishSubject.create();
            userModel.setLogoutClickSubject(logoutClickedSubject);
            _bindLogoutClickSubjectToSwitchingToLoginFragment(logoutClickedSubject);
            return logoutClickedSubject;
        }else if (logoutClickedSubject.hasCompleted()) {
            Timber.d("Current logoutClickedSubject hasCompleted, making new one");
            logoutClickedSubject = PublishSubject.create();
            userModel.setLogoutClickSubject(logoutClickedSubject);
            _bindLogoutClickSubjectToSwitchingToLoginFragment(logoutClickedSubject);
            return logoutClickedSubject;
        }else {
            Timber.d("Current logoutClickedSubject is still valid, passing it back");
            return logoutClickedSubject;
        }
    }

    private void _bindLogoutClickSubjectToSwitchingToLoginFragment(PublishSubject<Object> logoutClickedSubject){
        logoutClickedSubject.subscribe(new Observer<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {
                _showLoginFragment(_getSignInClickSubject());
            }
        });
    }

    @Override
    public void onPause() {
        _getSignInClickSubject().onCompleted();
        _getLoginErrorSubject().onCompleted();
        _getLogoutClickedSubject().onCompleted();
        super.onPause();
    }
}
