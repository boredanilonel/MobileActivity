package com.example.zhuk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;

public class FragmentRules extends Fragment {

    private WebView webViewRules;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rules, container, false);

        webViewRules = view.findViewById(R.id.webViewRules);
        setupWebView();
        loadHtmlRules();

        return view;
    }

    private void setupWebView() {
        webViewRules.getSettings().setJavaScriptEnabled(true);

        // Отключаем масштабирование
        webViewRules.getSettings().setSupportZoom(false);
        webViewRules.getSettings().setBuiltInZoomControls(false);
        webViewRules.getSettings().setDisplayZoomControls(false);

        // Настраиваем клиент для обработки ссылок внутри WebView
        webViewRules.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
    }

    private void loadHtmlRules() {
        try {
            webViewRules.loadUrl("file:///android_res/raw/game_rules.html");

        } catch (Exception e) {
            loadHtmlFromAssets();
        }
    }

    private void loadHtmlFromAssets() {
        try {
            // Альтернативный способ: создаем файл в assets folder
            String htmlContent = getHtmlContent();
            webViewRules.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);

        } catch (Exception e) {
            // Если все способы не работают, показываем простой текст
            showErrorContent();
        }
    }

    private String getHtmlContent() {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='utf-8'><style>body{font-family:Arial;padding:20px;}</style></head>" +
                "<body><h1>Правила игры 'Тараканьи бега'</h1>" +
                "<h2>Цель игры</h2><p>Соберите максимальное количество очков, управляя тараканами.</p>" +
                "<h2>Управление</h2><p>• Касание экрана - движение<br>• Свайп - смена направления</p>" +
                "<h2>Бонусы</h2><p>• Красный: +10 очков<br>• Зеленый: +20 очков<br>• Золотой: x2 множитель</p>" +
                "</body></html>";
    }

    private void showErrorContent() {
        webViewRules.loadData(
                "<h1 style='text-align:center;color:red;'>Ошибка загрузки правил</h1>" +
                        "<p style='text-align:center;'>Попробуйте перезагрузить приложение</p>",
                "text/html",
                "UTF-8"
        );
    }
}