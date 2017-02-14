package com.andzilla.smarttune.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;

import com.andzilla.smarttune.HomeActivity;
import com.andzilla.smarttune.MainActivity;
import com.andzilla.smarttune.R;

public abstract class BaseFragment extends Fragment {
    public  final String TAG = getClass().getSimpleName();

    @Override
    public void onStop() {
        System.gc();

        super.onStop();
    }

    public void replaceFragment(Context context, Fragment fragment) {
        ((HomeActivity) context).getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, fragment).commit();
    }

    public void replaceFragment(Context context, Fragment fragment, Bundle bundle) {

        if(bundle!=null){
            fragment.setArguments(bundle);
            this.replaceFragment(context,fragment);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP
                        && keyCode == KeyEvent.KEYCODE_BACK) {
                    backPressed();
                    return true;
                }
                return false;
            }
        });
    }

    protected abstract void backPressed();

    public void setWebViewClient(Context context, WebView web) {
      /*  web.setWebViewClient(new MyWebViewClien(context));
        MyWebViewClient.enableWebViewSettings(web);*/
    }


}
