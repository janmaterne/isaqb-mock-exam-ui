package org.isaqb.onlinetrainer.ui;

import org.springframework.ui.Model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AutoloadJS {

    public static final String ATTRIBUTE_NAME = "autoReloadJS";

    private final String htmlScriptTag;

    public void injectAutoReloadJS(Model model) {
        model.addAttribute(ATTRIBUTE_NAME, htmlScriptTag);
    }
}
