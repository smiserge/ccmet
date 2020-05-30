/*
 * Extract subtitles from MET / Brightcove online stream.
 */
package ccmet;


import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

public class Main {

    public static void main(String[] args) {
        String vtt_url_sample = "https://bcbolt446c5271-a.akamaihd.net/media/v1/hls/v4/clear/2079714550001/c2a4a8b0-7572-4ff4-b3df-65ac2d6974c7/001b565c-4722-4312-a23f-f8bb6442d3a3/segment6.vtt?akamai_token=exp=1590889370~acl=/media/v1/hls/v4/clear/2079714550001/c2a4a8b0-7572-4ff4-b3df-65ac2d6974c7/001b565c-4722-4312-a23f-f8bb6442d3a3/*~hmac=3aed34d7420b3ac2d9f8dab5be56e0228d9b6008ab065d0fa90db3febf927d0e";
        //                       "https://bcbolt446c5271-a.akamaihd.net/media/v1/hls/v4/clear/2079714550001/8103d063-4f71-498b-a936-2e7345a71a8f/aa7b70ea-c18f-4de6-8426-aef7366d94b4/segment16.vtt?akamai_token=exp=1589176673~acl=/media/v1/hls/v4/clear/2079714550001/8103d063-4f71-498b-a936-2e7345a71a8f/aa7b70ea-c18f-4de6-8426-aef7366d94b4/*~hmac=1e743e7a5a118ff12ef4156bcd0d9ccd18ba21049084b0f5f96fd4997c534806";
        //                       "https://house-fastly-signed-us-east-1-prod.brightcovecdn.com/media/v1/hls/v4/clear/102076671001/ad37f3a9-0779-46ac-90d7-d5ea8922488e/4e56b328-0806-476d-aca6-16a5ecc8584f/segment15.vtt?fastly_token=NWU5OWE5OGVfZDZhZjE3ZTU0ZjgzYmVmN2Q5NzRiMjM4YTY3YmNiNDQ4YjliMTc1ZjAwOGM0ZWI0NDVjNDM3YmYxZTkyYzZkOF8vL2hvdXNlLWZhc3RseS1zaWduZWQtdXMtZWFzdC0xLXByb2QuYnJpZ2h0Y292ZWNkbi5jb20vbWVkaWEvdjEvaGxzL3Y0L2NsZWFyLzEwMjA3NjY3MTAwMS9hZDM3ZjNhOS0wNzc5LTQ2YWMtOTBkNy1kNWVhODkyMjQ4OGUvNGU1NmIzMjgtMDgwNi00NzZkLWFjYTYtMTZhNWVjYzg1ODRmLw%3D%3D";
        //Carmen           "https://house-fastly-signed-us-east-1-prod.brightcovecdn.com/media/v1/hls/v4/clear/102076671001/ae07c3f2-91ce-4e50-8970-b4a02fff7f30/4665e1ae-6299-4fd0-9d32-dad3db5d278c/segment13.vtt?fastly_token=NWU5ODY1MWFfMjkzZWExZmY0OGU2ODU2ZmIzNmQ0YzQxYmNkMTIwZmIxNzc2YjJlN2Q1YWZhZjk4Zjc2MDk4NzY3ZGEzYmYzNV8vL2hvdXNlLWZhc3RseS1zaWduZWQtdXMtZWFzdC0xLXByb2QuYnJpZ2h0Y292ZWNkbi5jb20vbWVkaWEvdjEvaGxzL3Y0L2NsZWFyLzEwMjA3NjY3MTAwMS9hZTA3YzNmMi05MWNlLTRlNTAtODk3MC1iNGEwMmZmZjdmMzAvNDY2NWUxYWUtNjI5OS00ZmQwLTlkMzItZGFkM2RiNWQyNzhjLw%3D%3D";
        // La Rondine      "https://house-fastly-signed-us-east-1-prod.brightcovecdn.com/media/v1/hls/v4/clear/102076671001/02578650-6f12-4805-b70b-6ccc373a25e4/3002bdd2-d589-429e-ac68-3befdad4ee4b/segment6.vtt?fastly_token=NWU5ODI1OWZfYjAwN2IzNzlhMzU0ZjY2NTJkZWM3NWExZmUzYzhiZWViZjg3OWFhMWZiZTBlZjM2MzAyYTRkODM4MWQ3ZWUzYl8vL2hvdXNlLWZhc3RseS1zaWduZWQtdXMtZWFzdC0xLXByb2QuYnJpZ2h0Y292ZWNkbi5jb20vbWVkaWEvdjEvaGxzL3Y0L2NsZWFyLzEwMjA3NjY3MTAwMS8wMjU3ODY1MC02ZjEyLTQ4MDUtYjcwYi02Y2NjMzczYTI1ZTQvMzAwMmJkZDItZDU4OS00MjllLWFjNjgtM2JlZmRhZDRlZTRiLw%3D%3D";
        // Boris Godunov   "https://house-fastly-signed-us-east-1-prod.brightcovecdn.com/media/v1/hls/v4/clear/102076671001/1ae14ed8-5faa-464d-9e54-499597176a1e/6ee9e975-a4dd-437d-8600-b2c2c8def140/segment8.vtt?fastly_token=NWU5NzE2ZmZfZWUzNjA3OTJmOGQ5YWEzYWQwYzZmNWI0OGNhN2IzZDFkMzM5MWY0YzMwZWU2MmVlZDdkZWYwZDE2ZjFlNDk2Ml8vL2hvdXNlLWZhc3RseS1zaWduZWQtdXMtZWFzdC0xLXByb2QuYnJpZ2h0Y292ZWNkbi5jb20vbWVkaWEvdjEvaGxzL3Y0L2NsZWFyLzEwMjA3NjY3MTAwMS8xYWUxNGVkOC01ZmFhLTQ2NGQtOWU1NC00OTk1OTcxNzZhMWUvNmVlOWU5NzUtYTRkZC00MzdkLTg2MDAtYjJjMmM4ZGVmMTQwLw%3D%3D";
        // rusalka         "https://house-fastly-signed-us-east-1-prod.brightcovecdn.com/media/v1/hls/v4/clear/102076671001/6a996323-3b0e-41aa-8561-beea53456a14/065763b9-f1d2-47d8-9478-5c1aae7f2d03/segment16.vtt?fastly_token=NWU5NWJjZGZfZGM2YWJmMjIxNTQxNjBkNjFmNTBhZWZhYWQ4MjZkY2Q3OWUzMDgwZDBlNTkxMGY3M2Y2ZmY5ZDU5NjdlOTAxZV8vL2hvdXNlLWZhc3RseS1zaWduZWQtdXMtZWFzdC0xLXByb2QuYnJpZ2h0Y292ZWNkbi5jb20vbWVkaWEvdjEvaGxzL3Y0L2NsZWFyLzEwMjA3NjY3MTAwMS82YTk5NjMyMy0zYjBlLTQxYWEtODU2MS1iZWVhNTM0NTZhMTQvMDY1NzYzYjktZjFkMi00N2Q4LTk0NzgtNWMxYWFlN2YyZDAzLw%3D%3D";
        // cosi fan tutte  "https://house-fastly-signed-us-east-1-prod.brightcovecdn.com/media/v1/hls/v4/clear/102076671001/569a8a2f-f6c7-453c-b252-c45d78a09753/63b94672-4757-40c0-ac5f-865c5fa2eae2/segment14.vtt?fastly_token=NWU5NDljMDVfNTI5Y2I4MGQ1NWUwOTY2YTU1NGUyYWM3NjJmMWU2ZjQxYjNkMTA3ZTFlMDZlMDg5YjFjYjc3NGY3ZDUxNjM1NF8vL2hvdXNlLWZhc3RseS1zaWduZWQtdXMtZWFzdC0xLXByb2QuYnJpZ2h0Y292ZWNkbi5jb20vbWVkaWEvdjEvaGxzL3Y0L2NsZWFyLzEwMjA3NjY3MTAwMS81NjlhOGEyZi1mNmM3LTQ1M2MtYjI1Mi1jNDVkNzhhMDk3NTMvNjNiOTQ2NzItNDc1Ny00MGMwLWFjNWYtODY1YzVmYTJlYWUyLw%3D%3D";
        // juliette        "https://house-fastly-signed-us-east-1-prod.brightcovecdn.com/media/v1/hls/v4/clear/102076671001/b5727a2b-c9d9-4c02-a8f2-6d98ed227d95/8d11c817-83e3-4f98-9925-0121c74260f2/segment19.vtt?fastly_token=NWU5MmU3NjlfZDVmNjM0NmMzYWVlZjlkNjk2NzM1ZWJhNmJlZWQ3ODE2OWUxMDVjYjg0NTU3MTRhMDIxMGYwYWYxZGM3ZTQ5Y18vL2hvdXNlLWZhc3RseS1zaWduZWQtdXMtZWFzdC0xLXByb2QuYnJpZ2h0Y292ZWNkbi5jb20vbWVkaWEvdjEvaGxzL3Y0L2NsZWFyLzEwMjA3NjY3MTAwMS9iNTcyN2EyYi1jOWQ5LTRjMDItYThmMi02ZDk4ZWQyMjdkOTUvOGQxMWM4MTctODNlMy00Zjk4LTk5MjUtMDEyMWM3NDI2MGYyLw%3D%3D";
        //falstaf          "https://house-fastly-signed-us-east-1-prod.brightcovecdn.com/media/v1/hls/v4/clear/102076671001/eca5256c-7d87-46df-bf2a-5212ff130a9b/d687d798-4bea-496e-8da8-ac5185a9709f/segment11.vtt?fastly_token=NWU5MTczNTFfN2U2MjkxNjU0OGFhYWU1MWQ2OGJiNDFhMWZmMTdmNWFkMDU5ZWZkODZjZWJmZjNjYTU5ZjJkYTQ4YzcxYjI3NV8vL2hvdXNlLWZhc3RseS1zaWduZWQtdXMtZWFzdC0xLXByb2QuYnJpZ2h0Y292ZWNkbi5jb20vbWVkaWEvdjEvaGxzL3Y0L2NsZWFyLzEwMjA3NjY3MTAwMS9lY2E1MjU2Yy03ZDg3LTQ2ZGYtYmYyYS01MjEyZmYxMzBhOWIvZDY4N2Q3OTgtNGJlYS00OTZlLThkYTgtYWM1MTg1YTk3MDlmLw%3D%3D";
        
        //String bcv_url_base  = "https://house-fastly-signed-us-east-1-prod.brightcovecdn.com/media/v1/hls/v4/clear/102076671001/ee4bc7bb-1f0e-4c8b-9408-4c143737ffe1/f6e8b417-b5e0-4d4e-9f77-02d2add13044/";
        //String bcv_url_token = "fastly_token=NWU5MDk3MTNfMzU2MThlZDcxMDEyYmZjOTNkNDljOTljZmJiMDI2NjZiYjNkNjUzMDFkMjdjNzBhYTkzZTBlNmRkYzAyMWQxY18vL2hvdXNlLWZhc3RseS1zaWduZWQtdXMtZWFzdC0xLXByb2QuYnJpZ2h0Y292ZWNkbi5jb20vbWVkaWEvdjEvaGxzL3Y0L2NsZWFyLzEwMjA3NjY3MTAwMS9lZTRiYzdiYi0xZjBlLTRjOGItOTQwOC00YzE0MzczN2ZmZTEvZjZlOGI0MTctYjVlMC00ZDRlLTlmNzctMDJkMmFkZDEzMDQ0Lw%3D%3D";

        // parse base URL and token
        int segment_index = vtt_url_sample.indexOf("segment");
        String vtt_url_base = vtt_url_sample.substring(0, segment_index);
        int qm_index = vtt_url_sample.indexOf("?");
        String session_token = vtt_url_sample.substring(qm_index+1);
        
        SegmentParser parser = new SegmentParser();
        FileWriter myWriter = null;
        try {
            myWriter = new FileWriter("/Users/serge/subtitles.srt");
        } catch (IOException ex) {
            //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        //URL for initial segment
        //int segment_id = 0;
        int segment_id = 6;
        int srt_id = 1; //per SRT spec its index starts at 1 
        String segment_name = "segment" + segment_id + ".vtt";
        String vtt_segment_url = vtt_url_base + segment_name + "?" + session_token;

        ArrayList srtRecords = new ArrayList<SrtRecord>();
        int rc = parser.getSrtRecords(vtt_segment_url, srtRecords);
        while (rc == 200) {
            //System.out.println(" *******************  Segment: " + segment_id + " RC = " + rc);
            //System.out.println(segment_id);
            for (int i = 0; i < srtRecords.size(); i++) {
                SrtRecord srt = (SrtRecord) srtRecords.get(i);
                try {
                    myWriter.write("" + srt_id + "\n");
                    myWriter.write(srt.toString() + "\n");
                } catch (IOException e) {
//                 System.out.println("An error occurred.");
//                 e.printStackTrace();
                }
                System.out.println(srt_id);
                System.out.println(srt.toString());
                srt_id++;
            }
            //System.out.println(" xxx ");

            // prepare & read next segment
            srtRecords = new ArrayList<SrtRecord>();
            segment_id++;
            segment_name = "segment" + segment_id + ".vtt";
            vtt_segment_url = vtt_url_base + segment_name + "?" + session_token;

            rc = parser.getSrtRecords(vtt_segment_url, srtRecords);
        }
        System.out.println(" *******************  finished reading. Last Segment: " + segment_id + " RC = " + rc);
        try {
            myWriter.close();
        } catch (IOException ex) {
            //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

