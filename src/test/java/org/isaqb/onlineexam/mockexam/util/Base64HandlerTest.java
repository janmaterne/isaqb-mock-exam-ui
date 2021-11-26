package org.isaqb.onlineexam.mockexam.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class Base64HandlerTest {

    @ParameterizedTest
    @ValueSource(strings = { "test", "äöüß", "{\"key\":\"value\"}" })
    public void encode(String in) {
        var handler = new Base64Handler();
        assertEquals(in, handler.decode(handler.encode(in)));
    }

}
