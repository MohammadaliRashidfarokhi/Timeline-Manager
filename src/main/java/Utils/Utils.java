package Utils;

public class Utils {
    static public String getFirstLastName(String input) {
        if(input!=null) {
            String[] parts = input.split(" ");
            if(parts.length>1) {
                return parts[0].substring(0, 1).toUpperCase() + parts[0].substring(1).toLowerCase() + " " + parts[parts.length-1].substring(0, 1).toUpperCase() + parts[parts.length-1].substring(1).toLowerCase();
            } else {
                return input;
            }
        } else {
            return "";
        }
    }
}
