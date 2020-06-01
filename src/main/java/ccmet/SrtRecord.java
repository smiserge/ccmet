/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccmet;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author serge
 */
public class SrtRecord {

    private String time;
    private String text;
    private static String TIME_SEPARATOR = " --> ";
    private static String VTT = "WEBVTT";
    private static String TIMESTAMP = "X-TIMESTAMP";

    public SrtRecord(String time, String text) {
        this.time = time;
        this.text = text;
    }

    public static boolean isValidString(String input) {
        boolean isValid = false;
        if (input != null) {
            if (input.startsWith(VTT)) {
                isValid = false;
            } else if (input.startsWith(TIMESTAMP)) {
                isValid = false;
            } else if (input.isEmpty()) {
                isValid = false;
            } else {
                isValid = true;
            }
        }
        return isValid;
    }

    public static boolean isTime(String input) {
        boolean isTime = false;
        if (input != null) {
            if (input.indexOf(TIME_SEPARATOR) > -1) {
                isTime = true;
            }
        }
        return isTime;
    }

    /**
     * @param time the time where formatting needs to be corrected to comply
     * with SRT spec
     *
     * BCV time is formatted as: 59:31.205 --> 59:35.676 or 01:00:22.422 -->
     * 01:00:28.262 which needs to be converted to SRT format: 00:59:31,205 -->
     * 00:59:35.676 or 01:00:22,422 --> 01:00:28.262
     *
     *
     * @return corrected time, parameter is no modified
     */
    public static String fixTime(String time) {
        String result = "";
        String curr = time.replace('.', ',');
        int separator_index = curr.indexOf(TIME_SEPARATOR);
        if (separator_index > -1) {
            String left_time = curr.substring(0, separator_index);  // get left hand time
            if (left_time.indexOf(':') == left_time.lastIndexOf(':')) // only one ":" is present, so need to prepend 00
            {
                left_time = "00:" + left_time;
            }

            String right_time = curr.substring(separator_index + TIME_SEPARATOR.length()); // get right hand time
            if (right_time.indexOf(':') == right_time.lastIndexOf(':')) // only one ":" is present, so need to prepend 00
            {
                right_time = "00:" + right_time;
            }
            result = left_time + TIME_SEPARATOR + right_time;
        }

        return result;
    }

    /**
     * clean-up the string with subtitles text
     * Current implementation replaces all &nbsp; with " ".
     * @param rawString
     * @return 
     */
    public static String fixText(String rawString) {
        String result = null;
        if (rawString != null)
        {
           result = rawString.replaceAll("&nbsp;", " ");
        }
        return result;
    }
    
    public static SrtRecord parse(ArrayList<String> srtStrings) {
        SrtRecord srt = null;

        Iterator<String> iter = srtStrings.iterator();
         while (iter.hasNext()) {
            String curString = iter.next();

            //once time record is detected, create new SrtRecord object
            if (SrtRecord.isTime(curString)) {
                String fixedTime = SrtRecord.fixTime(curString);
                srt = new SrtRecord(fixedTime, "");
            } else {
                // ignore invalid strings
                // add valid strings if SrtRecord object already present
                if (SrtRecord.isValidString(curString)) {
                    if (srt != null) {
                        srt.addText(fixText(curString));
                    }
                }
            }
        }
        return srt;
    }

    /**
     * @return the time
     */
    public String getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @param text the text to append
     */
    public void addText(String text) {
        if (this.text != null) {
            if (this.text.isEmpty()) {
                this.text = text;
            } else {
                this.text = this.text + "\n" + text;
            }
        }
    }

    public String toString() {
        //String out = "Time: " + this.time + "\nText: " + this.text + "\n";
        String out = this.time + "\n" + this.text + "\n";
        return out;
    }

}
