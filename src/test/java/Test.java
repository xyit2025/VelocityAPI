import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test {
    public static void main(String[] args) {
//        testEdgeCases();
//        testNormalCase();
//        testSizeBasedPartition();
//        List<String> l = new ArrayList<>();
//        for (int i = 0; i <= 100; i++) {
//            l.add(String.valueOf(i));
//        }
//        List<List<String>> l1 = partition(l,10);
//        System.out.println(l1);
        a(1,2,3,4,5,6);

    }

    public static void a(int ... a){}

    public static <T> List<List<T>> partition(@NotNull List<T> list, int chunkSize) {
        int size = list.size();
        // 计算实际需要的分区数量（向上取整）
        int partitionCount = (int) Math.ceil((double) size / chunkSize);

        return IntStream.range(0, partitionCount)
                .mapToObj(i -> list.subList(
                        i * chunkSize,
                        Math.min((i + 1) * chunkSize, size)))
                .collect(Collectors.toList());
    }

    private static void testNormalCase() {
        // 正常分割测试
        List<Integer> list = List.of(1,2,3,4);
        List<List<Integer>> result = partition(list, 2);
        System.out.println("正常分割测试: " +
                (result.size() == 2 && result.get(0).size() == 2 ? "✓" : "✗"));
    }
    private static void testEdgeCases() {
        // 边界条件测试
        try {
            // 空列表测试
            List<List<Object>> emptyResult = partition(Collections.emptyList(), 3);
            boolean emptyTest = emptyResult.size() == 3 && emptyResult.stream().allMatch(List::isEmpty);

            // 异常输入测试
            try {
                partition(null, 2);
                System.out.println("Null输入测试: ✗ (未抛出异常)");
            } catch (NullPointerException e) {
                System.out.println("Null输入测试: ✓");
            }

            System.out.println("边界测试结果：" + (emptyTest ? "✓" : "✗"));
        } catch (Exception e) {
            System.out.println("边界测试异常: " + e.getClass().getSimpleName());
        }
    }
    private static void testSizeBasedPartition() {
        System.out.println("\n按大小分割测试:");

        // 正常分割测试
        List<Integer> list1 = List.of(1,2,3,4,5);
        List<List<Integer>> result1 = partition(list1, 2);
        System.out.println("正常分割[5元素分2个]: " +
                (result1.size() == 3 && result1.get(2).size() == 1 ? "✓" : "✗"));

        // 刚好整除的情况
        List<String> list2 = List.of("A","B","C","D");
        List<List<String>> result2 = partition(list2, 2);
        System.out.println("整除分割[4元素分2个]: " +
                (result2.size() == 2 ? "✓" : "✗"));

        // chunkSize大于列表长度
        List<Double> list3 = List.of(1.1, 2.2);
        List<List<Double>> result3 = partition(list3, 5);
        System.out.println("大尺寸分割[2元素分5个]: " +
                (result3.size() == 1 && result3.get(0).size() == 2 ? "✓" : "✗"));

        // 边界值测试
        try {
            partition(Collections.emptyList(), 3);
            System.out.println("空列表测试: ✓");
        } catch (Exception e) {
            System.out.println("空列表测试异常: " + e.getClass().getSimpleName());
        }

        try {
            partition(null, 2);
            System.out.println("Null输入测试: ✗ (未抛出异常)");
        } catch (NullPointerException e) {
            System.out.println("Null输入测试: ✓");
        }
    }

}
