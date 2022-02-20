package org.isaqb.onlineexam.rest;

public record IntroductionData(
    String introduction,
    String howToUse,
    String appVersion,
    String buildTStamp
) {
}
