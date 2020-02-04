package com.example.ticcattoe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

public class HomePage extends AppCompatActivity implements View.OnClickListener {

    Dialog exitingDialog;
    CatsShow catsShow;
    ViewGroup rootLayout;
    Button stayInApp, exitApp;
    Toolbar toolbar, exitingDialogToolbar;
    Boolean catsShowUp;
    Button btnOnline, btnSingleMode, btnScoreBoard, btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        rootLayout = (ViewGroup) findViewById(R.id.main_frame_layout);
        toolbar = (Toolbar)findViewById(R.id.homePageToolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_50dp);

        btnOnline = (Button) findViewById(R.id.btn_online);
        btnSingleMode = (Button) findViewById(R.id.btn_singleMode);
        btnScoreBoard = (Button) findViewById(R.id.btn_scoreBoard);
        btnSettings = (Button) findViewById(R.id.btn_settings);

        btnSettings.setOnClickListener(this);
        btnScoreBoard.setOnClickListener(this);
        btnOnline.setOnClickListener(this);
        btnSingleMode.setOnClickListener(this);
        this.catsShow = new CatsShow(this, rootLayout);
        this.catsShowUp = false;

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCatsShow();
            }
        });
    }

    private void createExitDialog() {
        this.exitingDialog = new Dialog(this);
        this.exitingDialog.setContentView(R.layout.exiting_dialog);
        exitingDialogToolbar = (Toolbar)this.exitingDialog.findViewById(R.id.exitDialogToolbar);
        exitingDialogToolbar.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        this.stayInApp = (Button)this.exitingDialog.findViewById(R.id.positiveBtn);
        this.exitApp = (Button)this.exitingDialog.findViewById(R.id.negativeBtn);
        this.exitApp.setOnClickListener(this);
        this.stayInApp.setOnClickListener(this);
        exitingDialog.setCancelable(true);
        exitingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        exitingDialog.show();

    }
    private void startCatsShow() {
        if(!this.catsShowUp) {
            this.catsShow.start();
            this.catsShowUp = true;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(this.catsShowUp) this.catsShow.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(this.catsShowUp) this.catsShow.resume();
    }

    @Override
    protected void onDestroy() {
        Log.d("ariel", "onDestroy");
        super.onDestroy();
        this.catsShow.killCats();
        //Toast.makeText(this, "on destroy", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.stopCatsShowBtn) {
            this.catsShow.stop();
            this.catsShowUp = false;
        }
        else if(view == btnOnline && !this.catsShowUp) {
            Intent intent = new Intent(this, Online.class);
            startActivityForResult(intent,0);
        }else if(view == exitApp){
                finish();
        }

    }

    @Override
    public void onBackPressed() {
        if (this.catsShowUp) {
           this.catsShow.stop();
            this.catsShowUp = false;
        } else
        {
            createExitDialog(); //super.onBackPressed();
        }
    }


}
