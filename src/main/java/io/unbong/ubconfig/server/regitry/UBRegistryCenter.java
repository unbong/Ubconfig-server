package io.unbong.ubconfig.server.regitry;

import cn.kimmking.utils.HttpUtils;
import com.alibaba.fastjson.JSON;
import io.unbong.ubconfig.server.meta.InstanceMeta;
import io.unbong.ubconfig.server.model.DistributedLocks;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * register config server self to registry center if config server is master
 * when is not master then unregister it self
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-05-25 18:06
 */

@Slf4j
@Component
public class UBRegistryCenter  implements RegistryCenter{

    public static final String SERVICE_CONTEXT = "service=app1_public_dev_ubconfigserver";
    @Value("${server.port}")
    private String port;

    @Value("${ubregistry.servers}")
    private String registerUrl;

    private boolean isMaterInPrevious = false;
    InstanceMeta instanceMeta ;

    private String host;


    private ScheduledExecutorService registerWorker ;

    @PostConstruct
    @Override
    public void start() {

        registerWorker = Executors.newScheduledThreadPool(1);
        registerWorker.scheduleWithFixedDelay(()->{
            this.renew();
        }, 2000, 5000, TimeUnit.MILLISECONDS);


        host = new InetUtils(new InetUtilsProperties()).findFirstNonLoopbackHostInfo().getHostname();
        log.debug("----> host is {}", host);
        instanceMeta  = InstanceMeta.http(host, Integer.valueOf(port));
    }

    @PreDestroy
    @Override
    public void stop() {

       registerWorker.shutdown();
       log.debug("register worker is done.");
    }

    @Override
    public void register() {
        String url =getRegisterUrl();
        log.debug("----> register url {}", url);
        InstanceMeta res = HttpUtils.httpPost( JSON.toJSONString(instanceMeta), url,InstanceMeta.class);
        log.debug("registered config server. res:{}" , res);
    }

    @Override
    public void unregister() {

        String url =getUnRegisterUrl();
        log.debug("----> unregister url {}", url);
        InstanceMeta res = HttpUtils.httpPost( JSON.toJSONString(instanceMeta), url,InstanceMeta.class);
        log.debug("unregistered config server. res:{}" , res);
    }

    private void renew(){
        try{
            String url =getRenewUrl();
            log.debug("----> renew url {}", url);
            Long res = HttpUtils.httpPost( JSON.toJSONString(instanceMeta), url,Long.class);
            log.debug("renewed config server. res:{}" , res);
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
        }

    }

    private String getRegisterUrl()
    {
        return registerUrl + "/reg?" + SERVICE_CONTEXT;
    }

    private String getUnRegisterUrl()
    {
        return registerUrl + "/unreg?" + SERVICE_CONTEXT;
    }

    private String getRenewUrl()
    {
        return registerUrl + "/renews?" + SERVICE_CONTEXT;
    }


}
