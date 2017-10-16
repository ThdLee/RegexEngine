import dfa.DFAConstructor;
import nfa.NFAConstructor;
import nfa.NFANodeManager;
import org.junit.Test;

public class RegexTest {

    @Test
    public void test() {
        String str = "\\d+";
//        NFAConstructor nfa = new NFAConstructor();
//        nfa.constructNFAPair(str);
//        System.out.println(NFANodeManager.getInstance().allNodesToString());
//        DFAConstructor constructor = new DFAConstructor();
//        constructor.constructDFA(str);
//        System.out.println(constructor.toTableString());

        Pattern pattern = RegexEngine.getInstance().createPattern(str);
        for (String s : pattern.matchAll("88a99a10")) {
            System.out.println(s);
        }

    }

}


