package dogfight_Z.dogLog.utils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import dogfight_Z.dogLog.model.SimplePrimaryKey;

/**
 * 我们的电商网站 JDBC 工具类
 * public static void main(String[] args)：                                      生成默认配置文件
 * public static ResultSet query(String dql, Object ... args)：                  通过 JDBC 查询数据库的通用方法
 * public static int update(String dql, Object... args)：                        通过 JDBC 更新数据库的通用方法
 * public static ResultSet updateForGeneratedKeys(String dql, Object... args)：  通过 JDBC 更新记录并返回主键值，可用于级联更新操作
 * public static Object getGeneratedKey(ResultSet rs)：                          获取一个主键
 */
public class JDBCConnection {

    private static String url;

    private Connection connection;
    //事务是否已开启
    private boolean opened;
    //异步读写锁
    private ReentrantReadWriteLock asyncLock;
    //连接超时
    private int timeOutSec;

    //在一个事务内开始使用的时间
    private LocalDateTime workStart;
    //开始摸鱼的时间
    private LocalDateTime idleStart;

    //ListIterator<JDBCConnection> inQueue;

    JDBCConnection(int timeOutSec) throws SQLException {
        asyncLock       = new ReentrantReadWriteLock();
        idleStart       = LocalDateTime.now();
        workStart       = LocalDateTime.now();
        //this.inQueue  = null;
        this.timeOutSec = timeOutSec;
        initConnection();
    }

    void flushIdle() {
        idleStart = LocalDateTime.now();
    }

    void flushWorking() {
        workStart = LocalDateTime.now();
    }

    long getIdleTime() {
        return Duration.between(idleStart, LocalDateTime.now()).getSeconds();
    }

    private long getWorkingTime() {
        if(workStart != null  &&  opened) return Duration.between(workStart, LocalDateTime.now()).getSeconds();
        else return 0;
    }

/*
    void setInQueue(ListIterator<JDBCConnection> inQueue) {
        this.inQueue = inQueue;
    }
*/
    static {
        try
        {
            //加载 JDBC驱动
            Class.forName("org.sqlite.JDBC");
            url = "jdbc:sqlite:dogfightZ.db";

        } catch(ClassNotFoundException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
    }

    //建立数据库连接
    private void initConnection() throws SQLException {
        asyncLock.writeLock().lock();
        opened = false;
        try {
            //初始化数据库连接
            connection = DriverManager.getConnection(url);
        } finally { asyncLock.writeLock().unlock(); }
    }

    /**
     * 打开连接
     */
    public boolean open() throws SQLException
    {
        //建立数据库连接
        if(connection == null) initConnection();
        return connection == null;
    }

    /**
     * 开启事务
     */
    public boolean start()
    {
        if(!opened && connection != null) try {
            //在连接上开启事务
            asyncLock.writeLock().lock();
            workStart = LocalDateTime.now();
            connection.setAutoCommit(false);
            return opened = true;
        } catch (SQLException e) {
            asyncLock.writeLock().unlock();
            e.printStackTrace();
            return opened = false;
        }  else return opened;
    }

    void end() throws SQLException
    {
        if(connection != null) connection.setAutoCommit(true);
        opened = false;
    }

    /**
     * 提交事务并关闭事务
     */
    public void commit()
    {
        if(connection != null  &&  opened){
            try {
                connection.commit();
                end();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally { asyncLock.writeLock().unlock(); }
        }
    }

    /**
     * 回滚并关闭事务
     */
    public void rollback()
    {
        if(connection != null  &&  opened) {
            try {
                connection.rollback();
                end();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally { asyncLock.writeLock().unlock(); }
        }
    }

    //关闭数据库连接
    void close()
    {
        if(connection != null) try
        {
            connection.close();
            connection = null;
        } catch (SQLException e)
        {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试
     */
    public static void main(String[] args) throws IOException {
        /*
        for(JDBCConnection e = null;;)
        {
            System.out.println((e = JDBCFactory.takeJDBC()).connection);
            JDBCFactory.returnBack(e);
        }
        */
    }

    /**
     * 通过 JDBC 查询数据库的通用方法
     * @param dql  Data Query Language, 数据查询语言。请使用参数占位符'?'，并在 args 中设置具体参数
     * @param args 要为DQL中的占位符赋值的参数值
     * @return 返回 脱机的查询结果集
     */
    public List<Object[]> query(Iterable<Object> args, String dql) throws SQLException {
        if (connection == null) return null;
        if(opened  &&  getWorkingTime() > timeOutSec) return null;
        PreparedStatement ppdStmt = null;
        asyncLock.readLock().lock();
        try {
            ppdStmt = connection.prepareStatement(dql);
            ppdStmt.setQueryTimeout(timeOutSec);
            //绑定参数
            int i = 0;
            for(Object each : args) {
                ppdStmt.setObject(++i, each);
            }

            //执行查询并返回查询结果构造的脱机结果集
            return constructResultList(ppdStmt.executeQuery());
        } finally {
            try {
                if(ppdStmt != null  &&  !ppdStmt.isClosed()) ppdStmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            asyncLock.readLock().unlock();
        }
    }
    public List<Object[]> query(String dql, Object ... args) throws SQLException {
        if (connection == null) return null;
        if(opened  &&  getWorkingTime() > timeOutSec) return null;
        PreparedStatement ppdStmt = null;
        asyncLock.readLock().lock();
        try {
            ppdStmt = connection.prepareStatement(dql);
            ppdStmt.setQueryTimeout(timeOutSec);

            //绑定参数
            int i = 0;
            for(Object each : args) {
                ppdStmt.setObject(++i, each);
            }

            //执行查询并返回查询结果构造的脱机结果集
            return constructResultList(ppdStmt.executeQuery());
        } finally {
            try {
                if(ppdStmt != null  &&  !ppdStmt.isClosed()) ppdStmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            asyncLock.readLock().unlock();
        }
    }

    public <T> List<T> queryEntities(Class<T> tClass, String dql, Object ... args) throws SQLException, NoSuchMethodException{
        if (connection == null) return null;
        if(opened  &&  getWorkingTime() > timeOutSec) return null;
        PreparedStatement ppdStmt = null;
        asyncLock.readLock().lock();
        try {
            ppdStmt = connection.prepareStatement(dql);
            ppdStmt.setQueryTimeout(timeOutSec);

            //绑定参数
            int i = 0;
            for(Object each : args) {
                ppdStmt.setObject(++i, each);
            }

            //执行查询并返回查询结果构造的脱机结果集
            return constructResultList(tClass, ppdStmt.executeQuery());
        } finally {
            try {
                if(ppdStmt != null  &&  !ppdStmt.isClosed()) ppdStmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            asyncLock.readLock().unlock();
        }
    }

    public <T> List<T> queryEntities(Class<T> tClass, Iterable<Object> args, String dql) throws SQLException, NoSuchMethodException {
        if (connection == null) return null;
        if(opened  &&  getWorkingTime() > timeOutSec) return null;
        PreparedStatement ppdStmt = null;
        asyncLock.readLock().lock();
        try {
            ppdStmt = connection.prepareStatement(dql);
            ppdStmt.setQueryTimeout(timeOutSec);

            //绑定参数
            int i = 0;
            for(Object each : args) {
                ppdStmt.setObject(++i, each);
            }

            //执行查询并返回查询结果构造的脱机结果集
            return constructResultList(tClass, ppdStmt.executeQuery());
        } finally {
            try {
                if(ppdStmt != null  &&  !ppdStmt.isClosed()) ppdStmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            asyncLock.readLock().unlock();
        }
    }

    public <T extends SimplePrimaryKey> Map<Object, T> queryMapping(Class<T> tClass, String dql, Object ... args) throws SQLException, NoSuchMethodException{
        if (connection == null) return null;
        if(opened  &&  getWorkingTime() > timeOutSec) return null;
        PreparedStatement ppdStmt = null;
        asyncLock.readLock().lock();
        try {
            ppdStmt = connection.prepareStatement(dql);
            ppdStmt.setQueryTimeout(timeOutSec);

            //绑定参数
            int i = 0;
            for(Object each : args) {
                ppdStmt.setObject(++i, each);
            }

            //执行查询并返回查询结果构造的脱机结果集
            return constructResultMap(tClass, ppdStmt.executeQuery());
        } finally {
            try {
                if(ppdStmt != null  &&  !ppdStmt.isClosed()) ppdStmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            asyncLock.readLock().unlock();
        }
    }

    public <T extends SimplePrimaryKey> Map<Object, T> queryMapping(Class<T> tClass, Iterable<Object> args, String dql) throws SQLException, NoSuchMethodException {
        if (connection == null) return null;
        if(opened  &&  getWorkingTime() > timeOutSec) return null;
        PreparedStatement ppdStmt = null;
        asyncLock.readLock().lock();
        try {
            ppdStmt = connection.prepareStatement(dql);
            ppdStmt.setQueryTimeout(timeOutSec);

            //绑定参数
            int i = 0;
            for(Object each : args) {
                ppdStmt.setObject(++i, each);
            }

            //执行查询并返回查询结果构造的脱机结果集
            return constructResultMap(tClass, ppdStmt.executeQuery());
        } finally {
            try {
                if(ppdStmt != null  &&  !ppdStmt.isClosed()) ppdStmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            asyncLock.readLock().unlock();
        }
    }

    /**
     * 通过 JDBC 更新数据库的通用方法
     * @param dml  Data Manipulation Language, 数据查询语言。请使用参数占位符'?'，并在 args 中设置具体参数
     * @param args 要为DML中的占位符赋值的参数值
     * @return 返回成功更新的记录条数
     */
    public int update(Iterable<Object> args, String dml) throws SQLException {
        if (connection == null) return 0;
        if(opened  &&  getWorkingTime() > timeOutSec) return 0;
        PreparedStatement ppdStmt = null;
        asyncLock.writeLock().lock();
        try {
            ppdStmt = connection.prepareStatement(dml);
            ppdStmt.setQueryTimeout(timeOutSec);

            int i = 0;
            for(Object each : args)
            {
                ppdStmt.setObject(++i, each);
            }

            return ppdStmt.executeUpdate();
        } finally {
            try {
                if(ppdStmt != null  &&  !ppdStmt.isClosed()) ppdStmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            asyncLock.writeLock().unlock();
        }
    }
    public int update(String dml, Object ... args) throws SQLException {
        if (connection == null) return 0;
        if(opened  &&  getWorkingTime() > timeOutSec) return 0;
        PreparedStatement ppdStmt = null;
        asyncLock.writeLock().lock();
        try {
            ppdStmt = connection.prepareStatement(dml);
            ppdStmt.setQueryTimeout(timeOutSec);

            int i = 0;
            for(Object each : args)
            {
                ppdStmt.setObject(++i, each);
            }

            return ppdStmt.executeUpdate();
        } finally {
            try {
                if(ppdStmt != null  &&  !ppdStmt.isClosed()) ppdStmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            asyncLock.writeLock().unlock();
        }
    }

    /**
     * 通过 JDBC 更新记录并返回主键值，可用于级联更新操作
     * @param dml  Data Manipulation Language, 数据查询语言。请使用参数占位符'?'，并在 args 中设置具体参数
     * @param args 要为DML中的占位符赋值的参数值
     * @return 返回受影响的记录的主键集，可用 getGeneratedKey 方法获取第一个主键
     */
    public List<Object[]> updateForGeneratedKeys(Iterable<Object> args, String dml) throws SQLException {
        if (connection == null) return null;
        if(opened  &&  getWorkingTime() > timeOutSec) return null;
        PreparedStatement ppdStmt = null;
        asyncLock.writeLock().lock();
        try {
            ppdStmt = connection.prepareStatement(dml, Statement.RETURN_GENERATED_KEYS);
            ppdStmt.setQueryTimeout(timeOutSec);

            int i = 0;
            for(Object each : args) {
                ppdStmt.setObject(++i, each);
            }

            //执行查询
            ppdStmt.executeUpdate();
            //返回受影响的记录的主键集
            return constructResultList(ppdStmt.getGeneratedKeys());
        } finally {
            try {
                if(ppdStmt != null  &&  !ppdStmt.isClosed()) ppdStmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            asyncLock.writeLock().unlock();
        }
    }
    public List<Object[]> updateForGeneratedKeys(String dml, Object ... args) throws SQLException {
        if (connection == null) return null;
        if(opened  &&  getWorkingTime() > timeOutSec) return null;
        PreparedStatement ppdStmt = null;
        asyncLock.writeLock().lock();
        try {
            ppdStmt = connection.prepareStatement(dml, Statement.RETURN_GENERATED_KEYS);
            ppdStmt.setQueryTimeout(timeOutSec);

            int i = 0;
            for(Object each : args) {
                ppdStmt.setObject(++i, each);
            }

            //执行查询
            ppdStmt.executeUpdate();
            //返回受影响的记录的主键集
            return constructResultList(ppdStmt.getGeneratedKeys());
        } finally {
            try {
                if(ppdStmt != null  &&  !ppdStmt.isClosed()) ppdStmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            asyncLock.writeLock().unlock();
        }
    }
    public Object updateForGeneratedKey(Iterable<Object> args, String dml) throws SQLException {
        return getGeneratedKey(updateForGeneratedKeys(args, dml));
    }
    public Object updateForGeneratedKey(String dml, Object ... args) throws SQLException {
        return getGeneratedKey(updateForGeneratedKeys(dml, args));
    }

    /**
     * 获取一个主键
     * @param rs 含有主键值的查询结果集
     * @return 返回一个主键
     */
    public static Object getGeneratedKey(List<Object[]> rs) {
        if(rs == null  ||  rs.size() == 0) return null;
        return rs.get(0)[0];
    }

    /**
     * 使用 ResultSet 构造脱机的查询结果集
     * @param rs JDBC 查询结果集
     * @return 脱机的查询结果集
     * @throws SQLException
     */
    public static List<Object[]> constructResultList(ResultSet rs) throws SQLException
    {
        if(rs == null) return null;

        List<Object[]> result = new ResultList<>();
        Object[] row = null;

        for (int columnCount = rs.getMetaData().getColumnCount(); rs.next(); ) {
            row = new Object[columnCount];
            for(int i = 0; i < columnCount; ++i) {
                row[i] = rs.getObject(i + 1);
            }
            result.add(row);
        }
        rs.close();
        return result;
    }

    /**
     * 使用 ResultSet 构造脱机的查询结果集
     * @param resultClass 要构造的实体类的模板对象
     * @param rs JDBC 查询结果集
     * @param <T> 要构造的实体类
     * @return 脱机的查询结果集
     * @throws SQLException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public static <T> List<T> constructResultList(Class<T> resultClass, ResultSet rs) throws NoSuchMethodException, SQLException {
        if(rs == null) return null;

        List<T> result = new ResultList<>();

        Constructor<T> resultConstructor = resultClass.getDeclaredConstructor(ResultSet.class);
        resultConstructor.setAccessible(true);

        while(rs.next()) {
            try {
                result.add(resultConstructor.newInstance(rs));
            } catch (InstantiationException e) {
                System.out.println(e.getMessage());
            } catch (IllegalAccessException e) {
                System.out.println(e.getMessage());
            } catch (InvocationTargetException e) {
                System.out.println(e.getMessage());
            }
        }
        rs.close();
        return result;
    }

    /**
     * 使用 ResultSet 构造脱机的查询结果映射（主键 -> 实体对象）
     * @param resultClass 要构造的实体类的模板对象
     * @param rs JDBC 查询结果集
     * @param <T> 要构造的实体类
     * @return 脱机的查询结果集
     * @throws SQLException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public static <T extends SimplePrimaryKey> Map<Object, T> constructResultMap(Class<T> resultClass, ResultSet rs) throws NoSuchMethodException, SQLException {
        if(rs == null) return null;

        Map<Object, T> result = new HashMap<>();

        Constructor<T> resultConstructor = resultClass.getDeclaredConstructor(ResultSet.class);
        resultConstructor.setAccessible(true);

        while(rs.next()) {
            try {
                T v = resultConstructor.newInstance(rs);
                Object k = v.getPrimaryKey();
                result.put(k, v);
            } catch (InstantiationException e) {
                System.out.println(e.getMessage());
            } catch (IllegalAccessException e) {
                System.out.println(e.getMessage());
            } catch (InvocationTargetException e) {
                System.out.println(e.getMessage());
            }
        }
        rs.close();
        return result;
    }

    private PreparedStatement bat_ppdstmt = null;

    public boolean preparedUpdateBatch(String sql) {
        if(connection == null) return false;
        if(opened  &&  getWorkingTime() > timeOutSec) return false;

        try {
            bat_ppdstmt = connection.prepareStatement(sql);
            return true;
        } catch (SQLException e) {
            bat_ppdstmt = null;
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean addUpdateBatchWithArgsList(ArgsList<Object> args) {
        if(connection == null  ||  bat_ppdstmt == null) return false;
        if(opened  &&  getWorkingTime() > timeOutSec) return false;

        int i = 0;
        try {
            for (Object each : args) bat_ppdstmt.setObject(++i, each);
            bat_ppdstmt.addBatch();
            return true;
        } catch(SQLException e) {
            bat_ppdstmt = null;
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean addUpdateBatch(Object ... args) {
        if(connection == null  ||  bat_ppdstmt == null) return false;
        if(opened  &&  getWorkingTime() > timeOutSec) return false;

        int i = 0;
        try {
            for (Object each : args) bat_ppdstmt.setObject(++i, each);
            bat_ppdstmt.addBatch();
            return true;
        } catch(SQLException e) {
            bat_ppdstmt = null;
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean submitUpdateBatch() throws SQLException {
        if(connection == null  ||  bat_ppdstmt == null) return false;
        if(opened  &&  getWorkingTime() > timeOutSec) return false;

        try {
            asyncLock.writeLock().lock();
            bat_ppdstmt.executeBatch();
            return true;
        } finally {
            bat_ppdstmt = null;
            asyncLock.writeLock().unlock();
        }
    }
}
