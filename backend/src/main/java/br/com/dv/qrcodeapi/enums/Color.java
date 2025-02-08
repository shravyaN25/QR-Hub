package br.com.dv.qrcodeapi.enums;

import lombok.Getter;

@Getter
public enum Color {

    BLACK("#000000"),
    WHITE("#FFFFFF"),
    RED("#FF0000"),
    GREEN("#00FF00"),
    BLUE("#0000FF"),
    YELLOW("#FFFF00"),
    PURPLE("#800080"),
    GRAY("#808080");

    private final String hexCode;

    Color(String hexCode) {
        this.hexCode = hexCode;
    }

}
