package by.vshkl.translate2.mvp.presenter;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import by.vshkl.translate2.mvp.view.LoginView;
import by.vshkl.translate2.util.CookieUtils;

@InjectViewState
public class LoginPresenter extends MvpPresenter<LoginView> {

    public void saveCookies(Context context, String cookies) {
        CookieUtils.putCookie(context, cookies);
    }
}
