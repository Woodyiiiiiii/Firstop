package reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * 该程序代码读取grammars文件夹中的文件
 * 并返回相应的FirstTop结果集
 */
public class Reader {

    // 保存文法字符串
    private List<String> stringList = new ArrayList<>();

    // 读取文件
    private void readTxtFile(String filePath) {
        try {
            String encoding = "UTF-8";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file));
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    stringList.add(lineTxt);
                }
                inputStreamReader.close();
            }
            else {
                System.out.println("Can't find the file.");
                return;
            }
        }catch (Exception e) {
            System.out.println("Something wrong.");
            e.printStackTrace();
        }
    }

    // 返回文法字符串
    public List<String> getStringList() {
        return stringList;
    }

    private void insert(List<Pair> list, String A, String a,
                        int i, int j, boolean[][] F) {
        if (i >= F.length || j >= F[0].length) return;
        if (!F[i][j]) {
            F[i][j] = true;
            list.add(new Pair(A, a));
        }
    }

    private List<String> splitRight(String right, List<String> V, List<String> T) {
        int lo = 0, hi = 1;
        List<String> res = new ArrayList<>();
        while (hi <= right.length()) {
            String tmp = right.substring(lo, hi);
            if (V.contains(tmp) || T.contains(tmp)) {
                res.add(tmp);
                lo = hi;
                hi = lo + 1;
            }else {
                ++hi;
            }
        }
        return res;
    }

    // 返回FIRSTOP集合
    private List<List<String>> getFirstTop() {
        if (stringList.size() != 4) {
            System.out.println("Not belongs to grammars.");
            return null;
        }
        String tmp;
        // 非终结符集合字符串
        tmp = stringList.get(0);
        String[] v = tmp.split(" ");
        List<String> V = Arrays.asList(v);
        // 终结符集合字符串
        tmp = stringList.get(1);
        String[] t = tmp.split(" ");
        List<String> T = Arrays.asList(t);
        // 生成|V|*|T|的二维数组
        boolean[][] F = new boolean[V.size()][T.size()];
        // 产生式集合字符串
        tmp = stringList.get(2);
        // 利用空格隔开
        String[] productions = tmp.split(" ");
        // 开始字符
        String start = stringList.get(3);
        // 初始化一个栈
        List<Pair> stack = new ArrayList<>();

        for (String production : productions) {
            String[] strs = production.split("->");
            String left = strs[0], right = strs[1];
            List<String> rightSplit = splitRight(right, V, T);
            if (rightSplit.size() >= 1 && V.contains(left) && T.contains(rightSplit.get(0))) {
                insert(stack, left, rightSplit.get(0), V.indexOf(left),
                        T.indexOf(rightSplit.get(0)), F);
            }else if (rightSplit.size() >= 1 && V.contains(left) && V.contains(rightSplit.get(0))) {
                if (rightSplit.get(0).equals(left) && rightSplit.size() >= 2) {
                    insert(stack, left, rightSplit.get(1), V.indexOf(left), T.indexOf(rightSplit.get(1)), F);
                }else {
                    for (int i = 0; i < stack.size(); ++i) {
                        String s1 = stack.get(i).s1;
                        String s2 = stack.get(i).s2;
                        for (String production1 : productions) {
                            String[] strs1 = production1.split("->");
                            String left1 = strs1[0], right1 = strs1[1];
                            List<String> rightSplit1 = splitRight(right1, V, T);
                            if (rightSplit1.size() >= 1 && left1.equals(left) && rightSplit1.get(0).equals(s1)) {
                                insert(stack, left, s2, V.indexOf(left), T.indexOf(s2), F);
                            }
                        }
                    }
                }
            }
        }

        List<List<String>> ans = new ArrayList<>();
        for (String A : v) {
            List<String> first = new ArrayList<>();
            for (String a : t) {
                int i = V.indexOf(A), j = T.indexOf(a);
                if (F[i][j]) {
                    first.add(a);
                }
            }
            ans.add(first);
        }
        return ans;
    }

    // 测试
    public static void main(String[] args) {
        Reader reader = new Reader();
        // 文件的绝对路径
        String filePath = "src\\grammars\\test.txt";
        reader.readTxtFile(filePath);
        System.out.println(reader.getFirstTop());
    }
}
