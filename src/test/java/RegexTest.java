import org.junit.Test;
import com.regex.Pattern;
import com.regex.RegexEngine;

public class RegexTest {

    @Test
    public void test() {
        String str = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
//        NFAConstructor com.regex.nfa = new NFAConstructor();
//        com.regex.nfa.constructNFAPair(str);
//        System.out.println(NFANodeManager.getInstance().allNodesToString());
//        DFAConstructor constructor = new DFAConstructor();
//        constructor.constructDFA(str);
//        System.out.println(constructor.toTableString());

        Pattern pattern = RegexEngine.createPattern(str);
        System.out.println(pattern.match("xxxxx@gmail.com"));

    }

}


