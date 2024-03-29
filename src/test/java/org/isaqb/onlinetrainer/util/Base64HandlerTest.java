package org.isaqb.onlinetrainer.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class Base64HandlerTest {

    @ParameterizedTest
    @ValueSource(strings = {"test", "äöüß", "{\"key\":\"value\"}"})
    void encode(String in) {
        var handler = new Base64Handler();
        assertEquals(in, handler.decode(handler.encode(in)));
    }

}
