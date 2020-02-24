package com.example.ticcattoe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

public class SingleMode extends AppCompatActivity implements View.OnClickListener {

    Dialog waitingDialog, exitingDialog;
    Button stayInActivity, returnToHomePageBtn;
    Toolbar mainToolbar, waitingDialogToolbar, exitingDialogToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_mode);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        mainToolbar = (Toolbar)findViewById(R.id.toolbar);
        mainToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_50dp);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createExitDialog();
            }
        });
        createWaitingDialog();
    }

    private void createExitDialog() {
        if(this.waitingDialog != null && this.waitingDialog.isShowing()) {
            this.waitingDialog.cancel();
        }
        this.exitingDialog = new Dialog(this);
        this.exitingDialog.setContentView(R.layout.exiting_dialog);
        exitingDialogToolbar = (Toolbar)this.exitingDialog.findViewById(R.id.exitDialogToolbar);
        exitingDialogToolbar.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        this.stayInActivity = (Button)this.exitingDialog.findViewById(R.id.positiveBtn);
        this.returnToHomePageBtn = (Button)this.exitingDialog.findViewById(R.id.negativeBtn);
        this.returnToHomePageBtn.setOnClickListener(this);
        this.stayInActivity.setOnClickListener(this);
        exitingDialog.setCancelable(false);
        exitingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        exitingDialog.show();

    }
    private void createWaitingDialog() {
        this.waitingDialog = new Dialog(this);
        waitingDialog.setContentView(R.layout.waiting_dialog);
        waitingDialogToolbar = (Toolbar)this.waitingDialog.findViewById(R.id.waitingDialogToolbar);
        waitingDialogToolbar.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        waitingDialogToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_50dp);
        waitingDialog.setCancelable(false);
        waitingDialogToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createExitDialog();
            }
        });
        waitingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        waitingDialog.show();
    }


    @Override
    public void onClick(View view) {
        if(view == stayInActivity ) {
            createWaitingDialog();
        }else if(view == returnToHomePageBtn){
            Intent intent = new Intent();
            setResult(RESULT_OK,intent);
            finish();
        }
    }
}
