package by.vshkl.translate2.ui.activity;

import android.os.Bundle;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import by.vshkl.translate2.mvp.presenter.SplashScreenPresenter;
import by.vshkl.translate2.mvp.view.SplashScreenView;

public class SplashScreenActivity extends MvpAppCompatActivity implements SplashScreenView {

    @InjectPresenter SplashScreenPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter.checkLoggedIn(getApplicationContext());
    }

    @Override
    public void onLoggedIn() {
        startActivity(MainActivity.newIntent(this));
        finish();
    }

    @Override
    public void onNotLoggedIn() {
        startActivity(LoginActivity.newIntent(this));
        finish();
    }
}
