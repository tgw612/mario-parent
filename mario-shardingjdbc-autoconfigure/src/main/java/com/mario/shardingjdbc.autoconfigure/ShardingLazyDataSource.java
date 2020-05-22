package com.mario.shardingjdbc.autoconfigure;

import com.alibaba.druid.pool.DruidDataSource;
import com.mario.common.concurrent.DouboThreadFactory;
import com.mario.common.exception.SystemException;
import com.mario.shardingjdbc.autoconfigure.core.properties.SpringBootShardingRuleConfigurationProperties;
import io.shardingsphere.api.config.MasterSlaveRuleConfiguration;
import io.shardingsphere.shardingjdbc.jdbc.unsupported.AbstractUnsupportedOperationDataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShardingLazyDataSource extends AbstractUnsupportedOperationDataSource implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(ShardingLazyDataSource.class);
    private PrintWriter logWriter;
    private DataSource targetDataSource;
    private volatile boolean isInit;
    private static final ReentrantLock lock = new ReentrantLock();
    private static final Condition condition;
    private final ThreadPoolExecutor threadPoolExecutor;
    private CountDownLatch countDownLatch;
    private Thread boss;
    private List<Exception> exceptions;
    private Map<String, DataSource> dataSourceMap;

    ShardingLazyDataSource(Map<String, DataSource> dataSourceMap,
                           SpringBootShardingRuleConfigurationProperties shardingProperties,
                           List<MasterSlaveRuleConfiguration> masterSlaveRuleConfigurationList, Integer threadsSize) {
        this.logWriter = new PrintWriter(System.out);
        this.isInit = false;
        this.exceptions = new ArrayList();
        this.dataSourceMap = dataSourceMap;
        this.threadPoolExecutor = new ThreadPoolExecutor(threadsSize, threadsSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(), DouboThreadFactory
            .create("Fast-init-dataSource-connection", false));
        this.countDownLatch = new CountDownLatch(dataSourceMap.size());
        this.boss = new Thread(() -> {
            try {
                this.countDownLatch.await();

                try {
                    this.targetDataSource = ShardingDataSourceFactoryUtil.createShardingDataSource(shardingProperties, masterSlaveRuleConfigurationList, dataSourceMap);
                    this.isInit = true;
                } catch (Exception var5) {
                    log.error("create ShardingDataSource is exception:", var5);
                    this.exceptions.add(var5);
                }
            } catch (InterruptedException var6) {
                var6.printStackTrace();
            }

            this.lockSignal();
            this.threadPoolExecutor.shutdown();
        });
        this.boss.start();
        Iterator var5 = dataSourceMap.entrySet().iterator();

        while (var5.hasNext()) {
            Map.Entry<String, DataSource> entry = (Map.Entry) var5.next();
            this.threadPoolExecutor.execute(new TaskThread((DruidDataSource) entry.getValue()));
        }

    }

    @Override
    public Connection getConnection() throws SQLException {
        this.waitAndThrowEx();
        return this.targetDataSource.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        this.waitAndThrowEx();
        return this.targetDataSource.getConnection(username, password);
    }

    private void lockSignal() {
        try {
            lock.lock();
            condition.signal();
        } finally {
            lock.unlock();
        }

    }

    private void waitAndThrowEx() throws SQLException {
        if (!this.isInit || this.targetDataSource == null) {
            try {
                lock.lock();
                condition.await();
            } catch (InterruptedException var5) {
                log.error("get connection await  exception:", var5);
            } finally {
                lock.unlock();
            }

            if (this.exceptions.size() > 0) {
                Exception exception = (Exception) this.exceptions.get(0);
                if (exception instanceof SQLException) {
                    throw (SQLException) exception;
                }

                if (exception instanceof SystemException) {
                    throw (SystemException) exception;
                }

                throw new SystemException(exception);
            }
        }

    }

    @Override
    public PrintWriter getLogWriter() {
        return this.logWriter;
    }

    @Override
    public void setLogWriter(PrintWriter out) {
        this.logWriter = out;
    }

    @Override
    public java.util.logging.Logger getParentLogger() {
        return java.util.logging.Logger.getLogger("global");
    }

    @Override
    public void close() {
        this.closeAllInitThead();
        if (this.targetDataSource != null) {
            try {
                this.targetDataSource.getClass().getDeclaredMethod("close").invoke(this.targetDataSource);
            } catch (ReflectiveOperationException var2) {
            }
        } else {
            this.dataSourceMap.forEach((k, v) -> {
                try {
                    v.getClass().getDeclaredMethod("close").invoke(v);
                } catch (ReflectiveOperationException var3) {
                }

            });
        }

    }

    private void closeAllInitThead() {
        if (this.boss != null) {
            try {
                this.boss.interrupt();
            } catch (Exception var2) {
            }
        }

        if (this.threadPoolExecutor != null && !this.threadPoolExecutor.isShutdown()) {
            this.threadPoolExecutor.shutdown();
        }

    }

    public DataSource getTargetDataSource() {
        return this.targetDataSource;
    }

    static {
        condition = lock.newCondition();
    }

    class TaskThread implements Runnable {
        DruidDataSource dataSource;

        TaskThread(DruidDataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public void run() {
            try {
                this.dataSource.init();
                ShardingLazyDataSource.this.countDownLatch.countDown();
            } catch (Exception var2) {
                ShardingLazyDataSource.this.exceptions.add(var2);
                ShardingLazyDataSource.this.lockSignal();
                ShardingLazyDataSource.this.threadPoolExecutor.shutdownNow();
                ShardingLazyDataSource.this.boss.interrupt();
            }

        }
    }
}
