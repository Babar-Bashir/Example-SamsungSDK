package com.akexorcist.knoxgeofencingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.akexorcist.knoxactivator.ActivationCallback;
import com.akexorcist.knoxactivator.KnoxActivationManager;
import com.akexorcist.knoxgeofencingapp.manager.DialogManager;
import com.akexorcist.knoxgeofencingapp.manager.SharedPreferenceManager;
import com.akexorcist.knoxgeofencingapp.manager.ToastManager;

public class ActivationActivity extends AppCompatActivity implements ActivationCallback {
    private final String LICENSE_KEY = "YOUR_KEY";

    private MaterialDialog dialogLoading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);
        checkDeviceAdminActivation();
    }

    @Override
    public void onStart() {
        super.onStart();
        KnoxActivationManager.getInstance().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        KnoxActivationManager.getInstance().unregister();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        KnoxActivationManager.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDeviceAdminActivated() {
        showDeviceAdminActivationSuccess();
        activateKnoxLicense();
    }

    @Override
    public void onDeviceAdminActivationCancelled() {
        showDeviceAdminActivationProblem();
    }

    @Override
    public void onDeviceAdminDeactivated() {
    }

    @Override
    public void onLicenseActivated() {
        hideLoadingDialog();
        saveLicenseActivationStateToSharedPreference();
        showLicenseActivationSuccess();
        goToRestrictionActivity();
    }

    @Override
    public void onLicenseActivateFailed(int errorType, String errorMessage) {
        hideLoadingDialog();
        showLicenseActivationProblem(errorType, errorMessage);
    }

    private void checkDeviceAdminActivation() {
        if (KnoxActivationManager.getInstance().isKnoxSdkSupported(this)) {
            activateDeviceAdmin();
        } else {
            showDeviceUnsupportedProblem();
        }
    }

    private void activateDeviceAdmin() {
        if (!KnoxActivationManager.getInstance().isDeviceAdminActivated(this)) {
            KnoxActivationManager.getInstance().activateDeviceAdmin(this);
        } else {
            onDeviceAdminActivated();
        }
    }

    private void activateKnoxLicense() {
        if (!SharedPreferenceManager.isLicenseActivated(this)) {
            showLoadingDialog();
            KnoxActivationManager.getInstance().activateLicense(this, LICENSE_KEY);
        } else {
            showLicenseActivationSuccess();
            goToRestrictionActivity();
        }
    }

    private void saveLicenseActivationStateToSharedPreference() {
        SharedPreferenceManager.setLicenseActivated(this);
    }

    private void goToRestrictionActivity() {
        startActivity(new Intent(this, DoSomethingActivity.class));
        finish();
    }

    private void showLicenseActivationSuccess() {
        ToastManager.showLicenseActivationSuccess(this);
    }

    private void showDeviceAdminActivationSuccess() {
        ToastManager.showDeviceAdminActivationSuccess(this);
    }

    private void showDeviceUnsupportedProblem() {
        DialogManager.showDeviceUnsupportedProblem(this, new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                finish();
            }
        });
    }

    private void showDeviceAdminActivationProblem() {
        DialogManager.showDeviceAdminActivationProblem(this, new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                if (which == DialogAction.POSITIVE) {
                    activateDeviceAdmin();
                } else if (which == DialogAction.NEGATIVE) {
                    finish();
                }
            }
        });
    }

    private void showLicenseActivationProblem(int errorType, String errorMessage) {
        DialogManager.showLicenseActivationProblem(this, errorType, errorMessage, new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                if (which == DialogAction.POSITIVE) {
                    activateKnoxLicense();
                } else if (which == DialogAction.NEGATIVE) {
                    finish();
                }
            }
        });
    }

    private void showLoadingDialog() {
        dialogLoading = DialogManager.showLicenseActivationLoading(this);
    }

    private void hideLoadingDialog() {
        if (dialogLoading != null) {
            dialogLoading.dismiss();
            dialogLoading = null;
        }
    }
}