package be.bendem.manga.library.utils;

public class NumberUtil {

    public static int getInt(String str) {
        int i = 0;

        for(char c : str.toCharArray()) {
            if(c < '0' || c > '9') {
                break;
            }
            i = i * 10 + (c - '0');
        }


        return i;
    }

}
