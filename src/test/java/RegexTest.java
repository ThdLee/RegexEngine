import dfa.DFAConstructor;
import nfa.NFAConstructor;
import nfa.NFANodeManager;
import org.junit.Test;

public class RegexTest {

    @Test
    public void test() {
        String str = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
//        NFAConstructor nfa = new NFAConstructor();
//        nfa.constructNFAPair(str);
//        System.out.println(NFANodeManager.getInstance().allNodesToString());
        DFAConstructor constructor = new DFAConstructor();
        constructor.constructDFA(str);
        System.out.println(constructor.toTableString());
    }

}
