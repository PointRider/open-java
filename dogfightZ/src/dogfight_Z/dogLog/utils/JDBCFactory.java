package dogfight_Z.dogLog.utils;

import java.sql.SQLException;
import java.util.LinkedList;

public class JDBCFactory {

    private static Object mutex;

    private static LinkedList<JDBCConnection> idlePool;
    private static LinkedList<JDBCConnection> workingPool;

    private static int minConnectionCount = 4;
    private static int maxConnectionCount = 32;
    //连接的最大使用时间，超时后有连接索取请求时移交给新连接
    private static int connectionUsingTimeOut = 10;
    //连接的最大空闲时间，超时后有连接被归还时释放
    private static int connectionIdleTimeOut = 10;

    private static Thread inOrder = null;

    static {
        mutex = new Object();
        synchronized (mutex){
            //从配置文件加载数据库配置文件
            //Properties config = new Properties();
            minConnectionCount     = 1;
            maxConnectionCount     = 4;
            connectionUsingTimeOut = 16;
            connectionIdleTimeOut  = 32;

            idlePool    = new LinkedList<JDBCConnection>();
            workingPool = new LinkedList<JDBCConnection>();

            try {
                int x = (int) Math.ceil((double)minConnectionCount / 8.0);
                System.out.print("JDBC连接池INFO - JDBCFactoryV2 by 兰彦真 正在初始化连接池(min " + minConnectionCount + " / max " + maxConnectionCount + ") ... ");

                for (int i = 1; i <= minConnectionCount; ++i) {
                    if(i % x == 0) System.out.print(i);
                    idlePool.addFirst(new JDBCConnection(connectionUsingTimeOut));
                    if(i % x == 0 && i < minConnectionCount) System.out.print('.');
                }
                System.out.println(" 完毕！");
            } catch (SQLException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
                System.err.println("JDBC连接池ERRO - 初始化连接池失败，请检查网络连接是否可靠。");
                System.exit(-1);
            }
        }
    }

    public static JDBCConnection takeJDBC()
    {
        synchronized (mutex) {
            //有空闲的连接可用
            if(idlePool.size() > 0) {
                JDBCConnection take = idlePool.removeLast();
                workingPool.addFirst(take);
                take.flushWorking();
                return take;
            } else {

                if(workingPool.size() < maxConnectionCount) { //如果不满最大连接数量，进行最大二倍扩容
                    if(inOrder != null) { //如果有正在扩容的订单
                        try {//等待订单生产一个连接
                            while (idlePool.size() == 0) mutex.wait();
                            JDBCConnection take = idlePool.removeLast();
                            workingPool.addFirst(take);
                            take.flushWorking();
                            return take;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            return null;
                        }
                    } else {
                        int newConnectionCount = Math.min(idlePool.size() + workingPool.size(), maxConnectionCount - workingPool.size());
                        try {
                            //扩容线程将于第一个连接被返回后运行
                            inOrder = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    int count = newConnectionCount - 1;
                                    System.out.println("JDBC连接池INFO - 连接池扩容" + count + "个JDBC连接");
                                    for (int i = 0; i < count; ++i) {
                                        try {
                                            JDBCConnection newConn = new JDBCConnection(connectionUsingTimeOut);
                                            //扩容一个通知一个等待线程
                                            synchronized (mutex) {
                                                idlePool.addFirst(newConn);
                                                mutex.notify();
                                            }
                                        } catch (SQLException e) {
                                            System.err.println("JDBC连接池WARN - 连接池扩容失败1个JDBC连接");
                                        }
                                    }
                                    synchronized (mutex) { inOrder = null; }
                                }
                            });

                            JDBCConnection take = new JDBCConnection(connectionUsingTimeOut);
                            workingPool.addFirst(take);
                            System.out.println("JDBC连接池INFO - JDBC连接池用尽，但未达到最大连接数。创建一个新连接并准备扩容");
                            inOrder.start();
                            take.flushWorking();
                            return take;

                        } catch (SQLException e) {
                            System.err.println(e.getMessage());
                            return null;
                        }
                    }
                } else { //连接已满最大连接数量，等待正在使用的连接被释放
                    try {
                        System.out.println("JDBC连接池INFO - 空闲连接用尽，并已达到最大连接数。等待连接被归还...");
                        while(idlePool.size() == 0) mutex.wait();
                        System.out.println("JDBC连接池INFO - 一个连接已被归还，现在可以使用它了");
                        JDBCConnection take = null;
                        workingPool.addFirst(take = idlePool.removeLast());
                        //take.setInQueue(workingPool.listIterator());
                        take.flushWorking();
                        return take;
                    } catch (InterruptedException e) {
                        System.err.println(e);
                        return null;
                    }
                }
            }
        }
    }

    public static void returnBack(JDBCConnection j)
    {
        j.commit();
        synchronized (mutex) {
            idlePool.addFirst(j);
            workingPool.remove(j);

            //关闭超过最大空闲时间的连接，释放带宽资源
            for(JDBCConnection timeOut; idlePool.size() > minConnectionCount && (timeOut = idlePool.getLast()).getIdleTime() > connectionIdleTimeOut;) {
                timeOut.close();
                idlePool.removeLast();
                System.out.println("JDBC连接池INFO - 一个连接由于长时间未使用，已被释放");
            }

            //新回到空闲队列的j重新计算等待时间
            j.flushIdle();
            mutex.notify();
        }
    }

    //使用两个线程测试连接池
    public static void main(String[] args) {
        Object mutex = new Object();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                JDBCConnection[] arr = new JDBCConnection[16];

                synchronized (mutex) {
                    for(int i = 0; i < 16; ++i) {
                        //Thread.sleep(1000);
                        arr[i] = JDBCFactory.takeJDBC();
                        System.out.println("t1 taken " + i);
                    }
                }

                for(int i = 0; i < 14; ++i) {
                    JDBCFactory.returnBack(arr[i]);
                    System.out.println("t1 returned " + i);
                }


                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for(int i = 14; i < 16; ++i) {
                    JDBCFactory.returnBack(arr[i]);
                }
                System.out.println("t1 returned all");
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                JDBCConnection[] arr = new JDBCConnection[16];

                synchronized (mutex) {
                    for(int i = 0; i < 16; ++i) {
                        arr[i] = JDBCFactory.takeJDBC();
                        System.out.println("t2 taken " + i);
                    }
                }
                for(int i = 0; i < 14; ++i) {
                    JDBCFactory.returnBack(arr[i]);
                    System.out.println("t2 returned " + i);
                }

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for(int i = 14; i < 16; ++i) {
                    JDBCFactory.returnBack(arr[i]);
                }
                System.out.println("t2 returned all");
            }
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
