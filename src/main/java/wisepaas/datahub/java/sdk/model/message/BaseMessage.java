package wisepaas.datahub.java.sdk.model.message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import wisepaas.datahub.java.sdk.common.Const;

public class BaseMessage {
    public String ts;

    public BaseMessage() {
        DateFormat df = new SimpleDateFormat(Const.TimeFormat);
        df.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        ts = df.format(new Date());
    }
}