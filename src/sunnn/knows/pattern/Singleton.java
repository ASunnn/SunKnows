package sunnn.knows.pattern;

class LazySingleton {

    private static LazySingleton instance;

//    public static LazySingleton getInstance() {
//        if (instance == null)
//            instance = new LazySingleton();
//        return instance;
//    }

    public static LazySingleton getInstance() {
        // 第一轮判断
        if (instance == null) {
            // 如果还未实例化就加锁创建实例
            synchronized (LazySingleton.class) {
                // 第二轮判断
                // 因为有可能同时有两个线程通过了第一轮判断
                // 但是这段代码只能一个一个线程进
                // 有可能第一个线程创建完实例之后第二个线程进来再次创建
                if (instance == null)
                    instance = new LazySingleton(); // 创建实例
            }
        }
        return instance;
    }
}

class EagerSingleton {

    private static final EagerSingleton instance = new EagerSingleton();

    public static EagerSingleton getInstance() {
        return instance;
    }
}
