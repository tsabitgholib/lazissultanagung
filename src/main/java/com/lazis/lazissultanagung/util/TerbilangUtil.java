package com.lazis.lazissultanagung.util;

import java.math.BigDecimal;

public class TerbilangUtil {

    private static final String[] SATUAN = {"", "Satu", "Dua", "Tiga", "Empat", "Lima", "Enam", "Tujuh", "Delapan", "Sembilan", "Sepuluh", "Sebelas"};

    public static String terbilang(Double number) {
        if (number == null) return "";
        BigDecimal bd = new BigDecimal(number);
        long longVal = bd.longValue();
        return (terbilang(longVal) + " Rupiah").trim();
    }

    private static String terbilang(long number) {
        if (number < 12) {
            return SATUAN[(int) number];
        } else if (number < 20) {
            return terbilang(number - 10) + " Belas";
        } else if (number < 100) {
            return terbilang(number / 10) + " Puluh " + terbilang(number % 10);
        } else if (number < 200) {
            return "Seratus " + terbilang(number - 100);
        } else if (number < 1000) {
            return terbilang(number / 100) + " Ratus " + terbilang(number % 100);
        } else if (number < 2000) {
            return "Seribu " + terbilang(number - 1000);
        } else if (number < 1000000) {
            return terbilang(number / 1000) + " Ribu " + terbilang(number % 1000);
        } else if (number < 1000000000) {
            return terbilang(number / 1000000) + " Juta " + terbilang(number % 1000000);
        } else if (number < 1000000000000L) {
            return terbilang(number / 1000000000) + " Miliar " + terbilang(number % 1000000000);
        } else if (number < 1000000000000000L) {
            return terbilang(number / 1000000000000L) + " Triliun " + terbilang(number % 1000000000000L);
        }
        return "";
    }
}
