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

/*
 * 
 */
public class SegmentParser {

    public int getSrtRecords(String https_url, ArrayList srtRecords) {
        int rc = 0;
        URL url;
        try {

            url = new URL(https_url);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            if ((con != null) & (srtRecords != null)) {

                try {

                    rc = con.getResponseCode();
                    if (rc == 200) {
                        BufferedReader br
                                = new BufferedReader(
                                        new InputStreamReader(con.getInputStream()));

                        //dumpl all cert info
                        //print_https_cert(con);
                        //dump all the content
                        parseStreamOfStrings(br, srtRecords);
                        br.close();
                    }

                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return rc;
    }

    /**
     * parseStreamOfStrings
     *
     * @param br
     * @param srtRecords
     *
     * VTT records are coming as a stream of strings similar to:
     * -------------------- WEBVTT X-TIMESTAMP-MAP=LOCAL:00:00:00.000,MPEGTS:0
     *
     * 2
     * 03:30.000 --> 03:32.067 The nights are brighter here than daytime
     * anywhere else!
     *
     * 3
     * 03:32.133 --> 03:39.667 Lovely Venice! Abode of every pleasure!
     * ----------------------
     *
     * So we process it as follows: 
     * 1. Split the stream into separate groups using empty string as a separator 
     * 2. Parse each group into an SrtRecord object 
     * 3. If valid SrtRecord was possible to create, add it to the collection of SrtRecrods 
     * 4. Bail out when no more strings are in the stream
     *
     */
    private void parseStreamOfStrings(BufferedReader br, ArrayList srtRecords) {
        if ((br != null) & (srtRecords != null)) {

            try {
                // accumulator of strings in current group
                ArrayList srtStrings = new ArrayList<String>();

                String input = br.readLine();
                while (input != null) {
                    while (!input.isEmpty()) //accumulate until empty string
                    {
                        srtStrings.add(input);
                        input = br.readLine();
                    }
                    // empty string found 
                    // create SRT Record if there are any accumulated strings
                    if (!srtStrings.isEmpty()) {
                        SrtRecord srt = SrtRecord.parse(srtStrings);
                        if (srt != null) // add it if parsing was successful
                        {
                            srtRecords.add(srt);
                        }
                        // clear the accumulator
                        srtStrings = new ArrayList<String>();
                    }

                    input = br.readLine();
                    //System.out.println(input);
                }

            } catch (IOException e) {
                //e.printStackTrace();
            }

        }
    }

}
