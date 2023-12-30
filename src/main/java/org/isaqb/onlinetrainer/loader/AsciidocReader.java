package org.isaqb.onlinetrainer.loader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.isaqb.onlinetrainer.model.I18NText;
import org.isaqb.onlinetrainer.model.Language;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class AsciidocReader {

    Asciidoctor doctor = Asciidoctor.Factory.create();

    public I18NText parse(String adoc) {
        I18NText text = new I18NText();

        for(Language lang : Language.values()) {
            String languageSpecificADoc = filter(adoc, lang);
            String html = toHtml(languageSpecificADoc);
            text.addText(lang, html);
        }

        return text;
    }

    public I18NText parse(Resource resource) {
        try {
            String adoc = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
            return parse(adoc);
        } catch (IOException e) {
            return new I18NText();
        }
    }



    public String toHtml(String languageSpecificADoc) {
        return languageSpecificADoc == null ? null : doctor.convert(languageSpecificADoc, Options.builder()
            .backend("html5")
            .compact(true)
            .toFile(false)
            .build());
    }



    /**
     * Filter the complete asciidoc document by its tags.
     * @param adoc
     * @param language
     * @return
     */
    protected String filter(String adoc, Language language) {
        String startPattern = "//\\s+tag::" + language.toString() + "\\[.*";
        String endPattern = "//\\s+end::" + language.toString() + ".*";
        StringBuilder rv = new StringBuilder();

        boolean inLanguageBlock = false;
        for(String line : adoc.split("\n")) {
            if (line.trim().matches(startPattern)) {
                inLanguageBlock = true;
            }
            if (line.trim().matches(endPattern)) {
                inLanguageBlock = false;
            }
            if (inLanguageBlock) {
                rv.append(line).append(System.lineSeparator());
            }
        }

        return rv.toString();
    }
}
