package com.regex.nfa;

import com.regex.analysis.Lexer;
import com.regex.token.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

public class NFAConstructor {
    private Lexer lexer;
    private NFANodeManager manager;

    public NFAConstructor() {
        this.lexer = new Lexer();
        this.manager = new NFANodeManager();
    }

    public NFAConstructor(Lexer lexer) {
        this.lexer = lexer;
        this.manager = new NFANodeManager();
    }

    public NFAPair constructNFAPair(String regex) {
        manager.clear();
        ArrayList<Token> suffixList = lexer.buildSuffixTokenList(regex);
        Stack<NFAPair> pairStack = new Stack<>();
        
        for (Token token : suffixList) {
            if (token instanceof CharToken) {

                pairStack.push(constructForChar((CharToken) token));
            } else if (token instanceof SetToken) {

                pairStack.push(constructForSet((SetToken) token));
            } else if (token instanceof SignToken) {
                if (token.equals(SignToken.CONNECT)) {
                    if (pairStack.size() < 2) throw new NFAException("Error: lack of com.regex.token to connect");

                    NFAPair pair2 = pairStack.pop();
                    NFAPair pair1 = pairStack.pop();

                    pairStack.push(handleConnect(pair1, pair2));
                } else if (token.equals(SignToken.OR)) {
                    if (pairStack.size() < 2) throw new NFAException("Error: lack of com.regex.token to or");

                    NFAPair pair2 = pairStack.pop();
                    NFAPair pair1 = pairStack.pop();

                    pairStack.push(handleOr(pair1, pair2));
                } else {
                    throw new NFAException("Error: wrong " +  token);
                }
            }  else if (token instanceof RangeToken) {
                if (pairStack.empty()) throw new NFAException("Error: lack of com.regex.token to build closure");

                NFAPair pair = pairStack.pop();
                constructForRange((RangeToken) token, pair);
                pairStack.push(pair);
            }
        }
        
        if (pairStack.size() != 1) throw new NFAException("Error: more than one NFA Graph");
        NFAPair pair = pairStack.pop();
        pair.startNode.setState(NFANode.STATE.START);
        pair.endNode.setState(NFANode.STATE.END);

        //distributeId(pair);

        return pair;
    }

    private NFAPair handleConnect(NFAPair pair1, NFAPair pair2) {
        pair1.endNode.addEdge(NFAEdge.createEpsilonEdge(pair2.startNode));
        pair1.endNode = pair2.endNode;

        pair1.merge(pair2);

        return pair1;
    }
    
    private NFAPair handleOr(NFAPair pair1, NFAPair pair2) {
        NFAPair pair;
        if (pair1.or) {
            pair1.startNode.addEdge(NFAEdge.createEpsilonEdge(pair2.startNode));
            pair2.endNode.addEdge(NFAEdge.createEpsilonEdge(pair1.endNode));

            pair1.merge(pair2);

            pair = pair1;
        } else {
            pair = new NFAPair();
            pair.startNode = manager.createNode();
            pair.endNode = manager.createNode();

            pair.addNode(pair.startNode);
            pair.addNode(pair.endNode);

            pair.or = true;

            pair.startNode.addEdge(NFAEdge.createEpsilonEdge(pair1.startNode));
            pair.startNode.addEdge(NFAEdge.createEpsilonEdge(pair2.startNode));

            pair1.endNode.addEdge(NFAEdge.createEpsilonEdge(pair.endNode));
            pair2.endNode.addEdge(NFAEdge.createEpsilonEdge(pair.endNode));

            pair.merge(pair1);
            pair.merge(pair2);
        }
        return pair;
    }
    
    private NFAPair constructForChar(CharToken token) {
        NFAPair pair = new NFAPair();
        pair.startNode = manager.createNode();
        pair.endNode = manager.createNode();

        pair.addNode(pair.startNode);
        pair.addNode(pair.endNode);

        NFAEdge edge = NFAEdge.createCharEdge(pair.endNode, token.getChar());
        pair.startNode.addEdge(edge);
        pair.addLabel(edge.getLabel());
        return pair;
    }

    private NFAPair constructForSet(SetToken token) {
        NFAPair pair = new NFAPair();
        pair.startNode = manager.createNode();
        pair.endNode = manager.createNode();

        pair.addNode(pair.startNode);
        pair.addNode(pair.endNode);

        NFAEdge edge = NFAEdge.createSetEdge(pair.endNode, token.getSet());
        pair.startNode.addEdge(edge);
        pair.addLabel(edge.getLabel());
        return pair;
    }

    // handle different kinds of closures with Thompson Construction Method
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

        start = manager.createNode();
        end = manager.createNode();

        start.addEdge(NFAEdge.createEpsilonEdge(pair.startNode));
        pair.endNode.addEdge(NFAEdge.createEpsilonEdge(end));

        start.addEdge(NFAEdge.createEpsilonEdge(end));
        pair.endNode.addEdge(NFAEdge.createEpsilonEdge(pair.startNode));

        pair.startNode = start;
        pair.endNode = end;
    }

    private void constructForPlusClosure(NFAPair pair) {
        NFANode start, end;

        start = manager.createNode();
        end = manager.createNode();

        start.addEdge(NFAEdge.createEpsilonEdge(pair.startNode));
        pair.endNode.addEdge(NFAEdge.createEpsilonEdge(end));
        pair.endNode.addEdge(NFAEdge.createEpsilonEdge(pair.startNode));

        pair.startNode = start;
        pair.endNode = end;
    }

    private void constructForOptionClosure(NFAPair pair) {
        NFANode start, end;

        start = manager.createNode();
        end = manager.createNode();

        start.addEdge(NFAEdge.createEpsilonEdge(pair.startNode));
        pair.endNode.addEdge(NFAEdge.createEpsilonEdge(end));

        start.addEdge(NFAEdge.createEpsilonEdge(end));

        pair.startNode = start;
        pair.endNode = end;
    }

    private void constructForManyClosure(RangeToken token, NFAPair pair) {
        NFAPair np = pair;
        NFAPair op = pair;
        for (int i = 1; i < token.getLow(); i++) {
            np = cloneLocalNFAPair(op);
            if (i != 1) op.deprecated();
            pair.endNode.addEdge(NFAEdge.createEpsilonEdge(np.startNode));
            pair.endNode = np.endNode;
            op = np;
        }
        if (np != pair) np.deprecated();
    }


    private void constructForMoreClosure(RangeToken token, NFAPair pair) {
        NFAPair np = pair;
        NFAPair op = pair;
        for (int i = 1; i < token.getLow(); i++) {
            np = cloneLocalNFAPair(op);
            if (i != 1) op.deprecated();
            pair.endNode.addEdge(NFAEdge.createEpsilonEdge(np.startNode));
            pair.endNode = np.endNode;
            op = np;
        }
        pair.endNode.addEdge(NFAEdge.createEpsilonEdge(np.startNode));
        if (np != pair) np.deprecated();
    }

    private void constructForRangeClosure(RangeToken token, NFAPair pair) {
        NFANode end = manager.createNode();

        NFAPair np = pair;
        NFAPair op = pair;
        for (int i = 1; i < token.getHigh(); i++) {
            np = cloneLocalNFAPair(op);
            if (i != 1) op.deprecated();
            pair.endNode.addEdge(NFAEdge.createEpsilonEdge(np.startNode));
            if (i >= token.getLow()) {
                pair.endNode.addEdge(NFAEdge.createEpsilonEdge(end));
            }
            pair.endNode = np.endNode;
            op = np;
        }
        if (np != pair) np.deprecated();
        pair.endNode.addEdge(NFAEdge.createEpsilonEdge(end));
        pair.endNode = end;
    }

    private NFAPair cloneLocalNFAPair(NFAPair oldPair) {
        NFAPair newPair = new NFAPair();

        HashMap<NFANode, NFANode> map = new HashMap<>();
        for (NFANode node : oldPair.getNodeSet()) {
            NFANode n = cloneLocalNFANode(node);
            map.put(node, n);
            newPair.addNode(n);
        }

        for (NFANode node : newPair.getNodeSet()) {
            for (NFAEdge edge : node.getEdges()) {
                NFANode n = map.get(edge.getTarget());
                if (n == null) edge.setTarget(edge.getTarget());
                else edge.setTarget(n);
            }
        }

        newPair.startNode = map.get(oldPair.startNode);
        newPair.endNode = map.get(oldPair.endNode);

        return newPair;
    }

    private NFANode cloneLocalNFANode(NFANode oldNode) {
        NFANode newNode = manager.createNode();

        for (NFAEdge edge : oldNode.getEdges()) {
            newNode.addEdge(edge.clone());
        }

        return newNode;
    }

    // set id for every NFA node for debugging easily
    private void distributeId(NFAPair pair) {
        int id = 0;
        pair.startNode.setId(id);

        HashSet<NFANode> set = new HashSet<>();
        Stack<NFANode> stack = new Stack<>();
        set.add(pair.startNode);
        stack.push(pair.startNode);

        while (!stack.empty()) {
            NFANode node = stack.pop();
            for (NFAEdge edge : node.getEdges()) {
                NFANode next = edge.getTarget();
                if (!set.contains(next)) {
                    next.setId(id++);
                    set.add(next);
                    stack.push(next);
                }
            }
        }
    }

}
