package com.foresthouse.dynamiccrawler.utils.compiler;

import android.os.AsyncTask;
import android.util.Log;

import com.foresthouse.dynamiccrawler.MainActivity;
import com.icpa.dynamiccrawler.R;
import com.foresthouse.dynamiccrawler.utils.Generator;
import com.foresthouse.dynamiccrawler.utils.Reflectable;

import java.util.ArrayList;

public class DCCompiler extends AsyncTask<Void, Integer, Void> { // TODO : Reflectable 객체 받아서 프로그레스 표시
    private static final String TAG = "[ DCCompiler ]";

    private final String originalCode;
    Reflectable reflectable;
    ArrayList<String> compiledCode = new ArrayList<>();
    int currentLineNumber = 0;
    String errorMsg;

    public DCCompiler(Reflectable reflectable) { this("", reflectable); }
    public DCCompiler(String code, Reflectable reflectable) {
        this.originalCode = code;
        this.reflectable = reflectable;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            for (String line : originalCode.split("\n")) {
            currentLineNumber += 1;
            line = line.split("//")[0].toString().trim(); // Remove useless white spaces and comments
            if (checkCodeSyntax(line, currentLineNumber)) {
                compiledCode.add(line);
            } else {
               throw new DCCodeSyntaxError(currentLineNumber);
            }
        }
        } catch (Exception e) {
            errorMsg = e.getMessage();
            e.printStackTrace();
            compiledCode = null;
            publishProgress(-1);
        }
        return null;
    }
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (values[0] != -1) {
            reflectable.reflectData(String.valueOf(values[0]));
        } else {
            Generator.makeYNDialog(MainActivity.ApplicationContext, "Compile Error", errorMsg,
                                   MainActivity.ApplicationContext.getString(R.string.str_confirm),
                                   null, null, null, null, null, null);
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }

    private static boolean checkCodeSyntax(String code, int currentLineNumber) throws UnknownCommandException {
        /*
          >CODE
          내부 API 명령어

          JS Code
          WebView Console 로 보낼 JS Selector 등의 명령어
         */
        //TODO 실제 문법분석 코드 만들기
        if (code.startsWith(">")) { //Inner Command
            Log.d(TAG, "checkCodeSyntax: Inner Command");
        }
        else if (code.equals("") || code.startsWith("//")) {
            Log.d(TAG, "checkCodeSyntax: Empty Line");
            return true;
        }
        else if (code.startsWith(":")) {
            Log.d(TAG, "checkCodeSyntax: JS Command");
        }
        else {
            Log.d(TAG, "checkCodeSyntax: Unable to know code type");
            throw new UnknownCommandException(currentLineNumber);
        }
        return false;
    }
}

/*

package com.foresthouse.icpa;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.material.navigation.NavigationView;

public class FragmentRiro extends Fragment {

    protected static WebView RiroViewer;
    static ViewGroup rootView;
    static NavigationView SideBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_riro, container, false);
        SideBar = rootView.findViewById(R.id.SideBar);
        SideBar.setVisibility(View.INVISIBLE);
        WebLoad getRiroViewerSetting = new WebLoad();
        getRiroViewerSetting.loadWeb();
        return rootView;
    }



}//End of Class

class WebLoad extends WebViewClient {
    //Blocking nth-child lists
    final static String ALL_NOTICE = "3";
    final static String HOME_NOTICE = "4";
    final static String ASSESSMENT = "5";
    final static String PROTFOLIO = "6";
    final static String CONTEST = "7";
    final static String SCORE_ANALYZE = "8";
    final static String AFTERSCHOOL = "9";
    final static String SELF_STUDY = "10";
    final static String UNIV_INFO = "11";
    final static String MEDIA_CLIP = "12";
    final static String ONLINE_STUDY = "13";



    public void loadWeb() {

        //Set WebView
        FragmentRiro.RiroViewer = (WebView) FragmentRiro.rootView.findViewById(R.id.riroViewer);
        FragmentRiro.RiroViewer.getSettings().setJavaScriptEnabled(true);
        FragmentRiro.RiroViewer.getSettings().setUseWideViewPort(true);
        FragmentRiro.RiroViewer.getSettings().setDomStorageEnabled(true);
        FragmentRiro.RiroViewer.getSettings().setAppCacheEnabled(true);
        FragmentRiro.RiroViewer.getSettings().setSupportZoom(true);
        FragmentRiro.RiroViewer.getSettings().setBuiltInZoomControls(true);
        FragmentRiro.RiroViewer.getSettings().setDisplayZoomControls(false);
        FragmentRiro.RiroViewer.getSettings().setSaveFormData(true);
        FragmentRiro.RiroViewer.loadUrl("https://icpa.riroschool.kr/home.php");
        FragmentRiro.RiroViewer.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            }

            @Override
            public void onLoadResource(WebView view, String url) {
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                FragmentRiro.RiroViewer.loadUrl("javascript:(function() { document.getElementsByClassName('sidebar')[0].style.display='none'; } )()"); //Side Bar
                FragmentRiro.RiroViewer.loadUrl("javascript:(function() { document.getElementsByClassName('re_main_img')[0].style.display='none'; } )()"); //Main Image
                FragmentRiro.RiroViewer.loadUrl("javascript:(function() { document.querySelector('body > div.re-main-panel.main-min-default > div.sliderContainer.animated > div').style.display='none'; } )()"); //Slide Notice 1
                FragmentRiro.RiroViewer.loadUrl("javascript:(function() { document.querySelector('body > div.re-main-panel.main-min-default > div.sliderContainer.animated > div > div:nth-child(2)').style.display='none'; } )()"); //Slide Notice 2
//                FragmentRiro.RiroViewer.loadUrl("javascript:(function() { document.querySelector('body > div.re-main-panel > div.sliderContainer.animated.rubberBand').style.display='none'; } )()"); //Slide Notice 3
//                FragmentRiro.RiroViewer.loadUrl("javascript:(function() { document.querySelector('#slide1 > li:nth-child(5) > div').style.display='none'; } )()"); //Slide Notice 4
//                FragmentRiro.RiroViewer.loadUrl("javascript:(function() { document.querySelector('body > div.re-main-panel > div.sliderContainer.animated > div').style.display='none'; } )()"); //Slide Notice 5
                FragmentRiro.RiroViewer.loadUrl("javascript:(function() { document.querySelector('body > div.re-main-panel > div.sliderContainer.animated').style.display='none'; } )()"); //Slide Notice 6
                FragmentRiro.RiroViewer.loadUrl("javascript:(function() { document.querySelector('body > div.re-main-panel.main-min-default > div.sliderContainer.animated').style.display='none'; } )()"); //Slide Notice DIV
                FragmentRiro.RiroViewer.loadUrl("javascript:(function() { document.querySelector('body > div.re-main-panel.main-min-default > div.sliderContainer.animated.rubberBand').style.display='none'; } )()"); //Container Div
                FragmentRiro.RiroViewer.loadUrl("javascript:(function() { document.querySelector('#main_content > a').style.display='none'; } )()"); //Unknown Element a
                FragmentRiro.RiroViewer.loadUrl("javascript:(function() { document.querySelector('#main_content > a > ul').style.display='none'; } )()"); //School Homepage
                FragmentRiro.RiroViewer.loadUrl("javascript:(function() { document.querySelector('#main_content > div > div > a').style.display='none'; } )()"); //Adiga
                try {
                    removeMainContents(DataManagement.getRiroViewerSetup());// Need to connect with DB - Setup
                }catch (NullPointerException e) {
                    //Ignore
                }
            }
        });



    }

    public void removeMainContents(String item) {
        removeMainContents(new String[]{item});
    }
    public void removeMainContents(String list[]) {
        for ( String num : list) {
            FragmentRiro.RiroViewer.loadUrl("javascript:(function() { document.querySelector('#main_content > ul:nth-child("+ num +")').style.display='none'; } )()"); //SideBar
        }
    }



}//End of Class




*/