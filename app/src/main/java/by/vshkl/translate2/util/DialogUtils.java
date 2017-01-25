package by.vshkl.translate2.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import by.vshkl.translate2.R;
import permissions.dispatcher.PermissionRequest;

public class DialogUtils {

    public static void showLocationRationaleDialog(final Context context, final PermissionRequest request) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.map_permission_rationale_title)
                .setMessage(R.string.map_permission_rationale_message)
                .setPositiveButton(R.string.map_permission_rationale_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.map_permission_rationale_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show();
    }

    public static void showLocationTurnOnDialog(final Context context) {
        new AlertDialog.Builder(context)
                .setMessage(R.string.map_location_message)
                .setPositiveButton(R.string.map_location_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Navigation.navigateToLocationSettings(context);
                    }
                })
                .setNegativeButton(R.string.map_location_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }
}
