package org.isaqb.onlinetrainer.util;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import org.springframework.stereotype.Component;

@Component
public class Base64Handler {

    private Encoder encoder = Base64.getEncoder();
    private Decoder decoder = Base64.getDecoder();

    public String encode(String string) {
        return encoder.encodeToString(string.getBytes());
    }

    public String decode(String base64) {
        return new String(decoder.decode(base64));
    }
}
