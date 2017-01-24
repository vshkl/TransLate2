package by.vshkl.translate2.mvp.presenter;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import by.vshkl.translate2.mvp.view.SplashScreenView;
import by.vshkl.translate2.util.CookieUtils;

@InjectViewState
public class SplashScreenPresenter extends MvpPresenter<SplashScreenView> {

    public void checkLoggedIn(Context context) {
        if (CookieUtils.hasCookie(context)) {
            getViewState().onLoggedIn();
        } else {
            getViewState().onNotLoggedIn();
        }
    }
}
