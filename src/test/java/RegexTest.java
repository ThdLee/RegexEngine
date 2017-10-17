import org.junit.Test;
import regex.Pattern;
import regex.RegexEngine;

public class RegexTest {

    @Test
    public void test() {
        String str = "\\d+";
//        NFAConstructor regex.nfa = new NFAConstructor();
//        regex.nfa.constructNFAPair(str);
//        System.out.println(NFANodeManager.getInstance().allNodesToString());
//        DFAConstructor constructor = new DFAConstructor();
//        constructor.constructDFA(str);
//        System.out.println(constructor.toTableString());

        Pattern pattern = RegexEngine.createPattern(str);
        for (String s : pattern.matchAll("88a99a10")) {
            System.out.println(s);
        }

    }

}


