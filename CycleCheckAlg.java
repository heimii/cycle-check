package test.tools;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * @author zhong
 * @date 2021-1-26
 */
public class CycleCheckAlg {

    public void check(Set<String> pairSet) {
        if (CollectionUtils.isEmpty(pairSet)) {
            return;
        }
        // 建立临接表
        Map<String, Node> nodeMap = new HashMap<>();
        for (String pair : pairSet) {
            String[] tokens = pair.split(",");
            Node fromNode = getOrCreateNode(tokens[0], nodeMap);
            Node toNode = getOrCreateNode(tokens[1], nodeMap);
            fromNode.toNodes.add(toNode);
        }

        Stack<Node> stack = new Stack<>();
        for (Node node : nodeMap.values()) {
            doCheckNode(node, stack);
            stack.clear();
        }
    }

    /**
     * 采用深度优先搜索算法
     */
    private void doCheckNode(Node node, Stack<Node> stack) {
        if (node.toNodes.isEmpty()) {
            return;
        }
        // 检查是否有圈
        int index = -1;
        for (Node nodeInStack : stack) {
            if (nodeInStack.equals(node)) {
                // 存在循环引用
                index = stack.indexOf(node);
                break;
            }
        }
        if (index >= 0) {
            // 打印循环引用链
            List<String> nodes = stack.subList(index, stack.size())
                    .stream()
                    .map(item -> item.key)
                    .collect(Collectors.toList());
            String link = StringUtils.join(nodes, " --> ") + " --> " + node.key;
            System.err.println("存在循环引用，引用链：" + link);
        } else {
            // 递归搜索
            stack.push(node);
            for (Node toNode : node.toNodes) {
                doCheckNode(toNode, stack);
            }
            stack.pop();
        }
    }

    private Node getOrCreateNode(String key, Map<String, Node> nodeMap) {
        Node node = nodeMap.getOrDefault(key, null);
        if (node != null) {
            return node;
        }
        node = new Node(key);
        nodeMap.put(key, node);
        return node;
    }

    public static class Node {
        private String key;
        private final Set<Node> toNodes = new HashSet<>();

        public Node(String key) {
            this.key = key;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Node)) {
                return false;
            }
            Node node = (Node) o;
            return key.equals(node.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }
}
