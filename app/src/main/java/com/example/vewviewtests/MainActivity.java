package com.example.vewviewtests;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private WebView myWebView;
    private MainActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(MainActivityViewModel.class);

//        myWebView = findViewById(R.id.webview);
//        myWebView.setVisibility(View.GONE);
//        myWebView.addJavascriptInterface(new ScrapingJavaScriptInterface(), "HTMLOUT");
//
//        myWebView.getSettings().setJavaScriptEnabled(true);
//
//        myWebView.setWebViewClient(new WebViewClient() {
//            int timesLoaded = 0;
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                Log.d(TAG, "onFinished");
//
//                if (timesLoaded < 5) {
//                    myWebView.evaluateJavascript("javascript:document.getElementsByClassName('btn grey')[0].click();",
//                            new ValueCallback<String>() {
//                                @Override
//                                public void onReceiveValue(String s) {
//                                    Log.d("LogName", "TESTAS"); // Log is written, but s is always null
//                                }
//                            });
//
//                    timesLoaded++;
//                } else {
//                    myWebView.evaluateJavascript("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');", new ValueCallback<String>() {
//                        @Override
//                        public void onReceiveValue(String value) {
//                            Log.d(TAG, value);
//                        }
//                    });
////                    myWebView.evaluateJavascript("javascript:document.getElementsByTagName('html')[0].innerHTML;", new ValueCallback<String>() {
////                        @Override
////                        public void onReceiveValue(String value) {
////                            new ScrapingJavaScriptInterface().processHTML("<head>"+value+"</head>");
////                        }
////                    });
//                }
//            }
//        });
//
//        myWebView.loadUrl("https://www.maxima.lt/akcijos");

    }


    public void simulateClick(View view) {
//        myWebView.evaluateJavascript("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');", new ValueCallback<String>() {
//            @Override
//            public void onReceiveValue(String value) {
//
//            }
//        });
        initScraping();
    }

    private void initScraping() {
        viewModel.initScraping();
    }

}
