package com.appice.appicemfppush;

import android.content.Context;

import androidx.annotation.NonNull;
import com.google.firebase.messaging.RemoteMessage;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushIntentService;
import com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPInternalPushMessage;
import org.json.JSONObject;

import java.util.Map;

import semusi.activitysdk.ContextSdk;

public class AppICEMFPPush extends MFPPushIntentService {

    @Override
    public void onMessageReceived(RemoteMessage message) {
        if (message != null)
            onMessageReceived(message, getApplicationContext());
    }

    public void onMessageReceived(RemoteMessage message, Context context){
        Map<String, String> data = message.getData();
        System.out.println("notification mapdata : "+data);

        MFPPushIntentService mfpPushIntentService = new MFPPushIntentService();
        boolean isAppICEPayload = ContextSdk.isAppICENotification(message);
        int collapseId = -1;
        JSONObject payload = null;

        if (isAppICEPayload) {
            boolean isSilentPush = ContextSdk.isSilentPush(message);
            if (!isSilentPush) {
                ContextSdk.handleAppICEPush(message, context);
            }
        } else {
            try {
                JSONObject dataPayload = new JSONObject(data);
                System.out.println("notification payload : "+dataPayload);
                if (dataPayload != null) {
                    MFPInternalPushMessage recMessage = new MFPInternalPushMessage(dataPayload);
                    payload = new JSONObject(recMessage.getPayload());
                    if (payload != null && payload.has("collapseId")) {
                        collapseId = payload.optInt("collapseId");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (collapseId != -1) {
                    mfpPushIntentService.onNotificationReceived(data, collapseId);
                } else {
                    super.onMessageReceived(message);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }
}
