package org.onwelo;


import com.google.gson.Gson;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Run {

    public static long myGETRequest() throws IOException {
        URL url = new URL("https://model.simplysign.webnotarius.pl/cas/api/time");
        String readLine = null;
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();

        long number = 0;

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuffer respone = new StringBuffer();
            while ((readLine = in.readLine()) != null) {
                respone.append(readLine);
            }
            in.close();
            String jsonText = respone.toString();
            Gson gson = new Gson();
            ActualDate date1 = gson.fromJson(jsonText, ActualDate.class);
             number = date1.currentTime.getTime();

        } else {
            System.out.println("Not worked");
        }
        return number /= 1000;
    }

    private static byte[] hmac_sha(String crypto, byte[] keyBytes, byte[] text) {
        try {
            Mac hmac;
            hmac = Mac.getInstance(crypto);
            SecretKeySpec macKey =
                    new SecretKeySpec(keyBytes, "RAW");
            hmac.init(macKey);
            return hmac.doFinal(text);
        } catch (GeneralSecurityException gse) {
            throw new UndeclaredThrowableException(gse);
        }
    }

    private static final int[] DIGITS_POWER
            // 0 1  2   3    4     5      6       7        8
            = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000};




    public static void main(String[] args) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));


        String seed = "344D4B574242594F54564B4A374133594E4A575A375935565235594A4F56514F485A584842585056474C5250574E47584E4E4E513D3D3D3D";
        String time = null;
        try {
            time = Long.toString(myGETRequest()/30);
            System.out.println(time);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String digits = "6";

        String utcTime = null;
        try {
            utcTime = df.format(new Date(myGETRequest() * 1000));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(utcTime);
        System.out.println(TOTP.generateTOTP(seed, time, digits,"HmacSHA256"));

    }

}
