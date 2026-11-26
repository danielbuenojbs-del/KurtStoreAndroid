package com.kurtstore.app;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {
    // O link de monetização é o ponto inicial; o redirecionamento leva ao site público.
    private static final String ENTRY_URL = "https://monetiza.link/KurtStore";
    private static final String STORE_HOST = "kurt-vault-apps.base44.app";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true); // Necessário para a loja Base44 e barra de pesquisa integrada.
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setSupportZoom(false);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                String scheme = uri.getScheme() == null ? "" : uri.getScheme();
                if ("https".equalsIgnoreCase(scheme) &&
                    (STORE_HOST.equalsIgnoreCase(uri.getHost()) ||
                     "monetiza.link".equalsIgnoreCase(uri.getHost()))) {
                    return false;
                }
                if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    } catch (ActivityNotFoundException ignored) {}
                    return true;
                }
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } catch (Exception ignored) {
                    Toast.makeText(MainActivity.this, "Não foi possível abrir este link.", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url != null && url.contains(STORE_HOST)) {
                    injectKurtBottomBar();
                }
            }
        });

        webView.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
            try {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
                request.setTitle(fileName);
                request.setDescription("Baixando pela Kurt Store");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                request.setAllowedOverMetered(true);
                request.setAllowedOverRoaming(true);
                String cookies = CookieManager.getInstance().getCookie(url);
                if (cookies != null) request.addRequestHeader("Cookie", cookies);
                if (userAgent != null) request.addRequestHeader("User-Agent", userAgent);
                ((DownloadManager) getSystemService(DOWNLOAD_SERVICE)).enqueue(request);
                Toast.makeText(this, "Download iniciado.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Não foi possível iniciar o download.", Toast.LENGTH_LONG).show();
            }
        });

        if (savedInstanceState != null) webView.restoreState(savedInstanceState);
        else webView.loadUrl(ENTRY_URL);
    }

    private void injectKurtBottomBar() {
        String js = "(function(){"
          + "if(document.getElementById('kurt-native-bottom'))return;"
          + "var style=document.createElement('style');"
          + "style.id='kurt-native-style';"
          + "style.textContent='body{padding-bottom:78px!important}#kurt-native-bottom{position:fixed;left:0;right:0;bottom:0;z-index:2147483647;height:66px;box-sizing:border-box;padding:8px 12px calc(8px + env(safe-area-inset-bottom));display:flex;align-items:center;gap:10px;background:rgba(7,18,43,.98);border-top:1px solid #42d9ee;box-shadow:0 -5px 24px rgba(36,194,255,.24);font-family:Arial,sans-serif}#kurt-native-bottom .kurt-brand{width:42px;height:42px;flex:0 0 42px;border-radius:13px;background:linear-gradient(135deg,#22d8e8,#9b55ff 65%,#ed4acb);display:flex;align-items:center;justify-content:center;color:#06142d;font-weight:900;font-size:19px;text-decoration:none}#kurt-native-bottom input{min-width:0;flex:1;height:42px;border:1px solid #4b83a9;border-radius:22px;background:#122b4a;color:#fff;padding:0 14px;font-size:14px;outline:none}#kurt-native-bottom input::placeholder{color:#a9c5dd}#kurt-native-bottom button{height:42px;min-width:48px;border:0;border-radius:22px;padding:0 14px;background:linear-gradient(120deg,#22d8e8,#9b55ff);color:#06142d;font-weight:800;font-size:13px}';"
          + "document.head.appendChild(style);"
          + "var bar=document.createElement('div');bar.id='kurt-native-bottom';"
          + "var brand=document.createElement('a');brand.className='kurt-brand';brand.href='https://kurt-vault-apps.base44.app/';brand.textContent='K';brand.setAttribute('aria-label','Início Kurt Store');"
          + "var input=document.createElement('input');input.type='search';input.placeholder='Pesquisar aplicativos...';input.setAttribute('aria-label','Pesquisar aplicativos na Kurt Store');"
          + "var btn=document.createElement('button');btn.type='button';btn.textContent='BUSCAR';"
          + "function doSearch(){var q=input.value.trim();if(!q)return;"
          + "var nativeInput=document.querySelector('input[placeholder*=\"Buscar\"],input[placeholder*=\"buscar\"],input[placeholder*=\"nome ou descrição\"]');"
          + "if(nativeInput){var setter=Object.getOwnPropertyDescriptor(HTMLInputElement.prototype,'value').set;setter.call(nativeInput,q);nativeInput.dispatchEvent(new Event('input',{bubbles:true}));nativeInput.dispatchEvent(new Event('change',{bubbles:true}));"
          + "var buttons=Array.from(document.querySelectorAll('button'));var searchBtn=buttons.find(function(b){return /buscar/i.test(b.innerText||b.textContent||'')});if(searchBtn)searchBtn.click();else nativeInput.dispatchEvent(new KeyboardEvent('keydown',{key:'Enter',bubbles:true}));"
          + "}else{location.href='https://kurt-vault-apps.base44.app/?q='+encodeURIComponent(q);}}"
          + "btn.addEventListener('click',doSearch);input.addEventListener('keydown',function(e){if(e.key==='Enter')doSearch()});"
          + "bar.appendChild(brand);bar.appendChild(input);bar.appendChild(btn);document.body.appendChild(bar);"
          + "})();";
        webView.evaluateJavascript(js, null);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (webView != null) webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
