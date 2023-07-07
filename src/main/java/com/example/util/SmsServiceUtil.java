package com.example.util;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class SmsServiceUtil {

    public static void sendSmsCode(String phone, String smsCode) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType.parse("text/plain");
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("mobile_phone", phone)
                    .addFormDataPart("message", "OOO FON |BOT \n\n" +
                            "Tasdiqlash kodi :" + smsCode)
                    .addFormDataPart("from", "4546")
                    .addFormDataPart("callback_url", "https://0000.uz/test.php")
                    .build();
            Request request = new Request.Builder().url("https:notify.eskiz.uz/api/message/sms/send")
                    .addHeader("Authorization", "Bearer " + getToken())
                    .method("POST", body)
                    .build();
            client.newCall(request).execute();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void sendSmsOrder(String phone, String message) {

        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType.parse("text/plain");
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("mobile_phone", phone)
                    .addFormDataPart("message", message)
                    .addFormDataPart("from", "4546")
                    .addFormDataPart("callback_url", "https://0000.uz/test.php")
                    .build();
            Request request = new Request.Builder().url("https:notify.eskiz.uz/api/message/sms/send")
                    .addHeader("Authorization", "Bearer " + getToken())
                    .method("POST", body)
                    .build();
            client.newCall(request).execute();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static String getToken() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("email", "ooo-fon@mail.ru")
                .addFormDataPart("password", "IiQYmytWbuBZTa9AfyWj2JkYR0KMGDwVw4tzynVK")
                .build();
        Request request = new Request.Builder()
                .url("https://notify.eskiz.uz/api/auth/login")
                .method("POST", body)
                .build();
        Response response = null;

        try {
            response = client.newCall(request).execute();
            return new JSONObject((Objects.requireNonNull(response.body()).string()))
                    .getJSONObject("data")
                    .getString("token");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static String removePlusSign(String phoneNumber) {
        return phoneNumber.startsWith("+") ? phoneNumber.substring(1) : phoneNumber;
    }


}
