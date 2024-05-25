package io.unbong.ubconfig.server.dal;

import org.apache.ibatis.annotations.Select;

/**
 * Description
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-05-12 21:59
 */
public interface LocksMapper {

    @Select("select * from lock for yodate")
    String selectForUpdate();
}
