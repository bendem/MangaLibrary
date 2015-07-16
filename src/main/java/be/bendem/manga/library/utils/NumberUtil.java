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

    public static int compare(String str1, String str2) {
        return Integer.compare(getInt(str1), getInt(str2));
    }

}
