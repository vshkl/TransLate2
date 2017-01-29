package by.vshkl.translate2.ui.activity;

import android.os.Bundle;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import by.vshkl.translate2.App;
import by.vshkl.translate2.mvp.presenter.SplashScreenPresenter;
import by.vshkl.translate2.mvp.view.SplashScreenView;
import by.vshkl.translate2.util.Navigation;

public class SplashScreenActivity extends MvpAppCompatActivity implements SplashScreenView {

    @InjectPresenter SplashScreenPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter.checkLoggedIn(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getRefWatcher(this).watch(this);
    }

    @Override
    public void onLoggedIn() {
        Navigation.navigateToMap(this);
        finish();
    }

    @Override
    public void onNotLoggedIn() {
        Navigation.navigateToLogin(this);
        finish();
    }
}
