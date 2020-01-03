package pingcap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Created by Chen Lidong on 2020/01/03 20：55.
 *
 */
public class JugdeDeadLockCommandLine {

   static class Node { // Node of a user or a resource
        int key; // 百度百科关于阿拉伯字母的词条定义阿拉伯字母共28个，用户用 0 - 27表示；第i个资源用 i + 28 表示
        ArrayList<Integer> in; // 所有入边的源顶点
        ArrayList<Node> out; // 所有出边的目标点
        public Node(int key) {
            this.key = key;
            this.in = new ArrayList<Integer>();
            this.out = new ArrayList<Node>();
        }
        // 注：此题主要为了体现结题此路，故此处没有严格按照面向对象的规范使用 getter 和 setter
   }

    /**
     * 死锁检测
     * 场景：
     * 1、Single Resource
     * 2、只占用和申请资源，不释放资源
     * 3、同一个线程不会第二次申请同一个资源
     */
    public boolean jugdeDeadLockSingleResource()
    {
        Scanner sc = new Scanner(System.in);

        // 记录 Node 中所有的 key
        HashSet<Integer> allKeys = new HashSet<Integer>();
        // 记录 key 和所有对应的 Node
        HashMap<Integer, Node> allNodes = new HashMap<Integer, Node>();

        /**
         * Stage 1. Read inputs and construct the directed graph.

         */
        System.out.println("Please int the request sequence:");
        while(sc.hasNextLine()) {
            String s = sc.nextLine();
            String[] two = s.split(" -> ");
            int userKey = two[0].charAt(0) - 'a';
            int resKey = Integer.valueOf(two[1]) + 28;
            // 本代码不处理魔鬼数字问题
            /**
             *  注意区分请求和占用的边的方向不一样
             *  请求： user -> resource
             *  占用： resource -> user
             *
             * 1、如果 User 不存在，user -> resource 表示占有
             *  construct user
             *  add resource to user's in : a、 resource exists b、resource doesnt exists
             *
             * 2、如果 User 已存在，resource 不存在，边表示占有
             * 3、如果 User 已存在，resource 的入边数量为 0，边表示占用
             * 4、其他，表示请求
             */
            // 1、如果 User 不存在，user -> resource 表示占有
            if (!allKeys.contains(userKey)) {
                Node userNode = new Node(userKey);
                // 占有关系 ： resource -> user
                if (allKeys.contains(resKey)) {
                    // update out
                    Node resNode = allNodes.get(resKey);
                    resNode.out.add(userNode);
                    // update in
                    userNode.in.add(resKey);
                } else {
                    Node resNode = new Node(resKey);
                    allKeys.add(resKey);
                    // update out
                    resNode.out.add(userNode);
                    // update in
                    userNode.in.add(resKey);
                    allNodes.put(resKey, resNode);
                }
                allKeys.add(userKey);
                allNodes.put(userKey, userNode);

            } else if (!allKeys.contains(resKey)) {
                // 2、如果 User 已存在，resource 不存在，边表示占有
                // 占有关系 ： resource -> user
                Node resNode = new Node(resKey);

                Node userNode = allNodes.get(userKey);
                // update out
                resNode.out.add(userNode);
                // update in
                userNode.in.add(resKey);

                allNodes.put(resKey, resNode);
                allKeys.add(resKey);

            } else {

                Node resNode = allNodes.get(resKey);
                Node userNode = allNodes.get(userKey);
                if (resNode.out.isEmpty()) {
                    // 3、如果 User 已存在，resource 的出边数量为 0（resource 还未被占用），边表示占用
                    // update out
                    resNode.out.add(userNode);
                    // update in
                    userNode.in.add(resKey);
                } else { // 4、 其他，表示请求关系
                    resNode.in.add(userKey);
                    userNode.out.add(resNode);
                }
            }


        }
        sc.close();




        // 遍历所有节点,找到所有入度为 0 的点
        HashSet<Node> zeroInNodesSet = new HashSet<Node>();
        for (Node node : allNodes.values()) {
            if (node.in.isEmpty()) {
                zeroInNodesSet.add(node);
            }
        }
        if (zeroInNodesSet.isEmpty()) // 所有的点都有入边
        {
            System.out.println("DeadLock exists.");
            return true;
        }


        /**
         * Stage 2. Do topological sorting to check cycle.
         */
        HashSet<Integer> isVisited = new HashSet<Integer>(); // 记录已经被访问过一次的节点

        while ((isVisited.size() < allKeys.size()) && !zeroInNodesSet.isEmpty() ) {
            Node cur = zeroInNodesSet.iterator().next();

            ArrayList<Node> dests = cur.out;
            allKeys.remove(new Integer(cur.key));
            for (Node dest : dests) {
                dest.in.remove(new Integer(cur.key));
                // out不用更新，只需要更新 in
                if (dest.in.isEmpty()) {
                    zeroInNodesSet.add(dest);
                }
            }

            zeroInNodesSet.remove(cur);
        }

        if (allKeys.isEmpty()) { // 最终可以完成拓扑排序
            System.out.println("No DeadLock exists.");
            return false; // 不死锁
        } else {
            System.out.println("DeadLock exists.");
            return true;
        }
    }
}
