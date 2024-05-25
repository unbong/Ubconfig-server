package io.unbong.ubconfig.server.model;

import io.unbong.ubconfig.server.dal.LocksMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Distributed Locks
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-05-12 22:00
 */
@Slf4j
@Component
public class DistributedLocks {

    @Autowired
    DataSource dataSource;
    Connection connection;

    @Getter
    private AtomicBoolean locked = new AtomicBoolean(false);
    ScheduledExecutorService executor =  Executors.newScheduledThreadPool(1);


    @PostConstruct
    public void init(){
        try{
            connection = dataSource.getConnection();
        }
        catch (Exception exception){
            exception.printStackTrace();
        }
        executor.scheduleWithFixedDelay(this::tryLock, 1000, 5000, TimeUnit.MILLISECONDS);

    }

//    @Autowired
//    LocksMapper locksMapper;

    public boolean lock() throws SQLException {

        connection.setAutoCommit(false);
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        // time out wait
        connection.createStatement().execute("set innodb_lock_wait_timeout=5");     // 修改当前连接的数据库超时时间
        connection.createStatement().execute("select app from locks where id =1 for update"); //  当超过5秒

        if(locked.get())
        {
            log.info("-----> reenter this dist lock");
        }
        else{
            log.info("-----> get a dis lock");
        }
        return true;
    }

    private void tryLock(){
        try {
            lock();
            locked.set(true);
        }catch (Exception e){
            //e.printStackTrace();
            log.info("lock failed.");
            locked.set(false);
        }
    }

    @PreDestroy
    public void close(){
        try{
            if(connection !=null || !connection.isClosed())
            {
                connection.rollback();
                connection.close();
            }
        }catch (Exception e){
            log.info("ignore connection close failed.");
        }

    }

}
