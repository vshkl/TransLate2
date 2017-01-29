package by.vshkl.translate2.ui.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import by.vshkl.translate2.App;
import by.vshkl.translate2.R;
import by.vshkl.translate2.mvp.presenter.LoginPresenter;
import by.vshkl.translate2.mvp.view.LoginView;

public class LoginActivity extends MvpAppCompatActivity implements LoginView {

    private static final String URL_LOGGED = "http://www.minsktrans.by/lookout_yard/";
    private static final String URL_LOGIN = "http://www.minsktrans.by/lookout_yard/Account/Login";

    @BindView(R.id.wv_login) WebView wvLogin;

    @InjectPresenter LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(LoginActivity.this);
        initializeWebView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getRefWatcher(this).watch(this);
    }

    //------------------------------------------------------------------------------------------------------------------

    public static Intent newIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    private void initializeWebView() {
        wvLogin.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.equals(URL_LOGGED)) {
                    presenter.saveCookies(getApplicationContext(), CookieManager.getInstance().getCookie(url));
                    startActivity(MapActivity.newIntent(LoginActivity.this));
                    finish();
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (request.getUrl().toString().equals(URL_LOGGED)) {
                    presenter.saveCookies(getApplicationContext(),
                            CookieManager.getInstance().getCookie(request.getUrl().toString()));
                    startActivity(MapActivity.newIntent(LoginActivity.this));
                    finish();
                }
                return super.shouldOverrideUrlLoading(view, request);
            }
        });
        wvLogin.setWebChromeClient(new WebChromeClient());
        wvLogin.loadUrl(URL_LOGIN);
    }
}
