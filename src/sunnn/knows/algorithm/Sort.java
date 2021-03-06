package sunnn.knows.algorithm;

import java.util.Arrays;

public class Sort {
    /**
     * 希尔排序
     * 直接插入排序的变体，一次跨好几个数插
     */
    public static int[] shellSort(int[] array) {
        int inc = array.length;
        int t;
        do {
            inc = inc / 3 + 1;  //确定增量
            for (int i = inc; i < array.length; ++i) {  //从增量开始（直接插入排序默认第一个就是正确的
                if (array[i] < array[i - inc]) {    //如果i比排好序的数都要小
                    t = array[i];   //先暂存
                    int j;
                    for (j = i - inc; j >= 0 && t < array[j]; j -= inc) //从排好序的数里从后往前找（从大到小）（当然是按增量跳着来），并且不断的往后挪
                        array[j + inc] = array[j];  //这里的思路和直接插入排序是一样的
                    array[j + inc] = t; //找到了合适的位置，直接把暂存值放进去
                }
            }
        } while (inc > 1);
        return array;
    }

    /**
     * 堆排序
     */
    public static int[] heapSort(int[] array) {
        /*自下而上，建堆*/
        for (int i = array.length / 2; i > 0; --i) //利用完全二叉树的原理，直接从非叶子节点开始建，从最底部的节点开始建堆
            heapAdjust(array, i, array.length);

        /*排序，同时调整堆*/
        for (int i = array.length - 1; i > 0; --i) {
            swap(array, 0, i);  //从后往前把最大值从堆顶放到正确位置
            heapAdjust(array, 1, i);
        }

        return array;
    }

    /**
     * 建堆
     *
     * @param array  数组
     * @param root   要建堆的堆顶
     * @param length 堆shuzu大cang小du
     */
    private static void heapAdjust(int[] array, int root, int length) {
//        ++root;  //因为数组下标从0开始，而堆利用二叉树性质得从1开始计数，因此这里计算都是+1计算，取值时再-1
        for (int i = root * 2; i <= length; i *= 2) {
            if (i + 1 <= length && array[index(i)] < array[i]) //先判断右儿子在不在再判断哪个儿子最大
                ++i;

            if (array[index(root)] > array[index(i)])  //如果堆顶的值已经比大儿子大了，就不用建了
                break;
            swap(array, index(root), index(i));    //交换，使堆顶是最大的

            root = i;    //这一层搞定后进行下一个迭代，刚才交换的儿子作为新堆顶
        }
    }

    /**
     * 将用于计算树节点的下标转换为数组实际下标
     *
     * @param target 目标下标
     * @return 转换之后的下标
     */
    private static int index(int target) {
        return target - 1;
    }

    /**
     * 归并排序
     */
    public static int[] mergeSort(int[] array) {
        doMergeSort(Arrays.copyOf(array, array.length), array, 0, array.length - 1);  //调用一个递归方法，第一个参数是辅助空间
        return array;
    }

    /**
     * 拆分
     *
     * @param array 要归并的数组
     * @param r     保存归并结果的数组
     * @param start 起始下标
     * @param end   结束下标
     */
    private static void doMergeSort(int[] array, int[] r, int start, int end) {
        if (start == end)   //已经拆分到最后了
            r[start] = array[end];
        else {
            int mid = (start + end) / 2;    //找出中点
            int[] temp = new int[r.length];     //构造一个辅助空间，临时保存归并结果
            doMergeSort(array, temp, start, mid); //拆分出来的左子数组继续递归拆
            doMergeSort(array, temp, mid + 1, end);   //右子数组

            merge(temp, r, start, mid, end);    //将temp这一段临时归并的数组合并成有序的到r中
        }
    }

    /**
     * 合并
     *
     * @param array 要归并的数组
     * @param r     保存归并结果的数组
     * @param start 起始下标
     * @param mid   要归并的俩段的分界
     * @param end   结束下标
     */
    private static void merge(int[] array, int[] r, int start, int mid, int end) {
        int k = start;
        int m = start, n = mid + 1;

        while (m <= mid && n <= end) {   //用循环一个个归并到r
            if (array[m] < array[n])
                r[k++] = array[m++];
            else
                r[k++] = array[n++];
        }

        while (m <= mid)    //如果第一段有多
            r[k++] = array[m++];
        while (n <= end)    //如果第二段有多
            r[k++] = array[n++];
    }

    /**
     * 快排
     * 冒泡排序的变体
     */
    public static int[] quickSort(int[] array) {
        doQuickSort(array, 0, array.length - 1);  //调用一个递归方法
        return array;
    }

    /**
     * 递归拆分
     *
     * @param array 数组
     * @param start 起始下标
     * @param end   结束下标
     */
    private static void doQuickSort(int[] array, int start, int end) {
        if (start < end) {   //只有1个数或者以下的直接回去
            int mid = partition(array, start, end); //调用分区操作，获取分区的基准下标
            doQuickSort(array, start, mid - 1);   //左子数组递归
            doQuickSort(array, mid + 1, end);     //右子数组
        }
    }

    /**
     * 对范围内的数组进行大致排序
     *
     * @param array 数组
     * @param start 起始下标
     * @param end   结束下标
     * @return 基准下标
     */
    private static int partition(int[] array, int start, int end) {
        /*先三数取中，以防极端情况*/
        int mid = (start + end) / 2;
        if (array[start] > array[end])
            swap(array, start, end);
        if (array[mid] > array[end])
            swap(array, start, end);
        if (array[start] < array[mid])
            swap(array, start, end);

        int temp = array[start];    //以第一个数为基准
        while (start < end) {   //把小于基准的数都丢到它左边，大于的都丢到右边
            while (temp < array[end] && start < end)
                --end;
            array[start] = array[end];
            while (temp > array[start] && start < end)
                ++start;
            array[end] = array[start];
        }
        array[end] = temp;
        return end; //返回基准所在下标
    }

    /**
     * 交换俩数
     *
     * @param array 数组
     * @param a     要交换
     * @param b     的俩数
     */
    private static void swap(int[] array, int a, int b) {
        int t = array[a];
        array[a] = array[b];
        array[b] = t;
    }
}
