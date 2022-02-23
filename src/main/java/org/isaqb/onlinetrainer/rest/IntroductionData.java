package org.isaqb.onlinetrainer.rest;

public record IntroductionData(
    String introduction,
    String howToUse,
    String appVersion,
    String buildTStamp
) {
}
