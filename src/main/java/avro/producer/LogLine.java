package avro.producer;

import lombok.Data;

import java.io.Serializable;

@Data
public class LogLine implements Serializable {

    private String ip;
    private Long timestamp;
    private String url;
    private String referrer;
    private String useragent;
}
