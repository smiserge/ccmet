/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccmet;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.io.*;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

public class HttpsClient {

    public int getContent(String https_url, ArrayList segments) {
        int rc = 0;
        URL url;
        try {

            url = new URL(https_url);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            //dumpl all cert info
            //print_https_cert(con);
            //dump all the content
            rc = getSrtRecords(con, segments);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return rc;
    }

    private int getSrtRecords(HttpsURLConnection con, ArrayList segments) {
        int rc = 0;
        if ((con != null) & (segments != null)) {

            try {

                rc = con.getResponseCode();
                if (rc == 200) {
                    BufferedReader br
                            = new BufferedReader(
                                    new InputStreamReader(con.getInputStream()));

                    String input;
                    SrtRecord srt = new SrtRecord("", "");
                    while ((input = br.readLine()) != null) {
                        if (SrtRecord.isValidString(input)) {
                            if (SrtRecord.isTime(input))
                            {
                                String fixedTime = SrtRecord.fixTime(input);
                                srt = new SrtRecord(fixedTime, "");
                                segments.add(srt);
                            }
                            else  // anything else is subtitle text, append it.
                            {
                                srt.addText(input);
                            }
                        }
                        //System.out.println(input);
                    }
                    br.close();
                }

            } catch (IOException e) {
                //e.printStackTrace();
            }

        }
        return rc;
    }

}
