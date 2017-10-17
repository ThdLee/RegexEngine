import regex.Pattern;
import regex.RegexEngine;

public class Client {
    public static void main(String[] args) {
        String regex = null;
        String content = null;
        boolean all = false;
        if (args.length == 2) {
            regex = args[0];
            content = args[1];
        } else if (args.length == 3 && args[0].equals("-all")) {
            regex = args[1];
            content = args[2];
            all = true;
        } else {
            System.out.println("Usage: [-all] regex content");
            System.exit(1);
        }

        Pattern pattern = RegexEngine.createPattern(regex);
        if (all) {
            for (String str : pattern.matchAll(content)) {
                System.out.println(str);
            }
        } else {
            System.out.println(pattern.match(content));
        }
    }
}
