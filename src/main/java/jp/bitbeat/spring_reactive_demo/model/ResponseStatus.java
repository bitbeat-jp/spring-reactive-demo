package jp.bitbeat.spring_reactive_demo.model;

public enum ResponseStatus {
    SUCCESS("success"),
    ERROR("error");

    private String value;

    ResponseStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
