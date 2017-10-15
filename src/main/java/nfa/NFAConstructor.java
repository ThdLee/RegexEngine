package nfa;

import token.*;

import java.util.ArrayList;
import java.util.Stack;

public class NFAConstructor {
    private Lexer lexer;

    public NFAConstructor() {
        this.lexer = new Lexer();
    }

    public NFAConstructor(Lexer lexer) {
        this.lexer = lexer;
    }

    public NFAPair constructNFAPair(String regex) {
        NFANodeManager.getInstance().clear();
        ArrayList<Token> suffixList = lexer.buildSuffixTokenList(regex);
        Stack<NFAPair> pairStack = new Stack<>();
        
        for (Token token : suffixList) {
            if (token instanceof CharToken) {

                pairStack.push(constructForChar((CharToken) token));
            } else if (token instanceof SetToken) {

                pairStack.push(constructForSet((SetToken) token));
            } else if (token instanceof SignToken) {
                if (token.equals(SignToken.CONNECT)) {
                    if (pairStack.size() < 2) throw new NFAException("Error: lack of token to connect");

                    NFAPair pair2 = pairStack.pop();
                    NFAPair pair1 = pairStack.pop();

                    pairStack.push(handleConnect(pair1, pair2));
                } else if (token.equals(SignToken.OR)) {
                    if (pairStack.size() < 2) throw new NFAException("Error: lack of token to or");

                    NFAPair pair2 = pairStack.pop();
                    NFAPair pair1 = pairStack.pop();

                    pairStack.push(handleOr(pair1, pair2));
                } else {
                    throw new NFAException("Error: wrong " +  token);
                }
            }  else if (token instanceof RangeToken) {
                if (pairStack.empty()) throw new NFAException("Error: lack of token to build closure");

                NFAPair pair = pairStack.pop();
                constructForRange((RangeToken) token, pair);
                pairStack.push(pair);
            }
        }
        
        if (pairStack.size() != 1) throw new NFAException("Error: more than one NFA Graph");
        NFAPair pair = pairStack.pop();
        pair.startNode.setState(NFANode.STATE.START);
        pair.endNode.setState(NFANode.STATE.END);

        return pair;
    }

    private NFAPair handleConnect(NFAPair pair1, NFAPair pair2) {
        pair1.endNode.addEdge(NFAEdgeFactory.createEpsilonEdge(pair2.startNode));
        pair1.endNode = pair2.endNode;

        pair1.set.addAll(pair2.set);
        pair1.getLabelSet().addAll(pair2.getLabelSet());
        pair2.deprecated();
        return pair1;
    }
    
    private NFAPair handleOr(NFAPair pair1, NFAPair pair2) {
        NFAPair pair;
        if (pair1.or) {
            pair1.startNode.addEdge(NFAEdgeFactory.createEpsilonEdge(pair2.startNode));
            pair2.endNode.addEdge(NFAEdgeFactory.createEpsilonEdge(pair1.endNode));

            pair1.set.addAll(pair2.set);
            pair1.getLabelSet().addAll(pair2.getLabelSet());
            pair2.deprecated();
            pair = pair1;
        } else {
            pair = new NFAPair();
            pair.startNode = NFANodeManager.getInstance().createNode();
            pair.endNode = NFANodeManager.getInstance().createNode();

            pair.set.add(pair.startNode);
            pair.set.add(pair.endNode);

            pair.or = true;

            pair.startNode.addEdge(NFAEdgeFactory.createEpsilonEdge(pair1.startNode));
            pair.startNode.addEdge(NFAEdgeFactory.createEpsilonEdge(pair2.startNode));

            pair1.endNode.addEdge(NFAEdgeFactory.createEpsilonEdge(pair.endNode));
            pair2.endNode.addEdge(NFAEdgeFactory.createEpsilonEdge(pair.endNode));

            pair.set.addAll(pair1.set);
            pair.set.addAll(pair2.set);

            pair.getLabelSet().addAll(pair1.getLabelSet());
            pair.getLabelSet().addAll(pair2.getLabelSet());

            pair1.deprecated();
            pair2.deprecated();
        }
        return pair;
    }
    
    private NFAPair constructForChar(CharToken token) {
        NFAPair pair = new NFAPair();
        pair.startNode = NFANodeManager.getInstance().createNode();
        pair.endNode = NFANodeManager.getInstance().createNode();

        pair.set.add(pair.startNode);
        pair.set.add(pair.endNode);

        NFAEdge edge = NFAEdgeFactory.createCharEdge(pair.endNode, token.getChar());
        pair.startNode.addEdge(edge);
        pair.labelSet.add(edge.getLabel());
        return pair;
    }

    private NFAPair constructForSet(SetToken token) {
        NFAPair pair = new NFAPair();
        pair.startNode = NFANodeManager.getInstance().createNode();
        pair.endNode = NFANodeManager.getInstance().createNode();

        pair.set.add(pair.startNode);
        pair.set.add(pair.endNode);

        NFAEdge edge = NFAEdgeFactory.createSetEdge(pair.endNode, token.getSet());
        pair.startNode.addEdge(edge);
        pair.labelSet.add(edge.getLabel());
        return pair;
    }

    private void constructForRange(RangeToken token, NFAPair pair) {
        switch (token.getState()) {
            case PLUS:
                constructForPlusClosure(pair);
                break;
            case STAR:
                constructForStarClosure(pair);
                break;
            case OPTION:
                constructForOptionClosure(pair);
                break;
            case MANY:
                constructForManyClosure(token, pair);
                break;
            case MORE:
                constructForMoreClosure(token, pair);
                break;
            case RANGE:
                constructForRangeClosure(token, pair);
                break;
        }
    }

    private void constructForStarClosure(NFAPair pair) {
        NFANode start, end;

        start = NFANodeManager.getInstance().createNode();
        end = NFANodeManager.getInstance().createNode();

        start.addEdge(NFAEdgeFactory.createEpsilonEdge(pair.startNode));
        pair.endNode.addEdge(NFAEdgeFactory.createEpsilonEdge(end));

        start.addEdge(NFAEdgeFactory.createEpsilonEdge(end));
        pair.endNode.addEdge(NFAEdgeFactory.createEpsilonEdge(pair.startNode));

        pair.startNode = start;
        pair.endNode = end;
    }

    private void constructForPlusClosure(NFAPair pair) {
        NFANode start, end;

        start = NFANodeManager.getInstance().createNode();
        end = NFANodeManager.getInstance().createNode();

        start.addEdge(NFAEdgeFactory.createEpsilonEdge(pair.startNode));
        pair.endNode.addEdge(NFAEdgeFactory.createEpsilonEdge(end));
        pair.endNode.addEdge(NFAEdgeFactory.createEpsilonEdge(pair.startNode));

        pair.startNode = start;
        pair.endNode = end;
    }

    private void constructForOptionClosure(NFAPair pair) {
        NFANode start, end;

        start = NFANodeManager.getInstance().createNode();
        end = NFANodeManager.getInstance().createNode();

        start.addEdge(NFAEdgeFactory.createEpsilonEdge(pair.startNode));
        pair.endNode.addEdge(NFAEdgeFactory.createEpsilonEdge(end));

        start.addEdge(NFAEdgeFactory.createEpsilonEdge(end));

        pair.startNode = start;
        pair.endNode = end;
    }

    private void constructForManyClosure(RangeToken token, NFAPair pair) {
        NFAPair p = pair;
        for (int i = 1; i < token.getLow(); i++) {
            p = p.clone();
            pair.endNode.addEdge(NFAEdgeFactory.createEpsilonEdge(p.startNode));
            pair.endNode = p.endNode;
        }
    }


    private void constructForMoreClosure(RangeToken token, NFAPair pair) {
        NFAPair p = pair;
        for (int i = 1; i < token.getLow(); i++) {
            p = p.clone();
            pair.endNode.addEdge(NFAEdgeFactory.createEpsilonEdge(p.startNode));
            pair.endNode = p.endNode;
        }
        pair.endNode.addEdge(NFAEdgeFactory.createEpsilonEdge(p.startNode));
    }

    private void constructForRangeClosure(RangeToken token, NFAPair pair) {
        NFANode end = NFANodeManager.getInstance().createNode();

        NFAPair p = pair;
        for (int i = 1; i < token.getHigh(); i++) {
            p = p.clone();
            pair.endNode.addEdge(NFAEdgeFactory.createEpsilonEdge(p.startNode));
            if (i >= token.getLow()) {
                pair.endNode.addEdge(NFAEdgeFactory.createEpsilonEdge(end));
            }
            pair.endNode = p.endNode;
        }
        //pair.endNode.addEdge(NFAEdgeFactory.createEpsilonEdge(end));
        pair.endNode = end;
    }

}
