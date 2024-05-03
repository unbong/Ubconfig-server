package io.unbong.ubconfig.server;

import io.unbong.ubconfig.server.dal.ConfigsMapper;
import io.unbong.ubconfig.server.model.Configs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * config server endpoint
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-05-03 16:02
 */
@RestController
public class UbConfigController {

    @Autowired
    ConfigsMapper mapper;
    Map<String, Long> versions = new HashMap<>();



    @GetMapping("/list")
    public List<Configs> list(String app, String env, String ns){

        return mapper.list(app, env, ns);
    }

    @PostMapping("/update")
    public List<Configs> update(@RequestParam("app") String app,
                                @RequestParam("env") String env,
                                @RequestParam("ns") String ns,
                                @RequestBody Map<String,String> params)
    {
        params.forEach((k,v)->{
            insertOrUpdate(new Configs(app, env, ns, k, v));
        });

        List<Configs> configs = list(app, env,ns);

        versions.put(app+"-"+env+"-"+ns,System.currentTimeMillis());

        return configs;

    }

    private void insertOrUpdate(Configs configs) {
        Configs conf = mapper.select(configs.getApp(), configs.getEnv(),
                    configs.getNs(), configs.getPkey());
        if(conf == null){
            mapper.insert(configs);
        }
        else{
            mapper.update(configs);
        }
    }

    @GetMapping("/version")
    public long version(String app, String env, String ns){
        return versions.getOrDefault(app+"-"+env+"-"+ns,-1l);
    }
}
