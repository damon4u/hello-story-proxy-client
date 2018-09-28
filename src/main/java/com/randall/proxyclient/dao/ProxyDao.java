package com.randall.proxyclient.dao;

import com.randall.proxyclient.entity.Proxy;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ProxyDao {

    @Insert("INSERT IGNORE INTO proxy (ip, port, proto, create_time) " +
            "VALUES (#{ip}, #{port}, #{proto}, now())")
    int save(Proxy proxy);

    @Select("SELECT * FROM proxy LIMIT #{index},1")
    Proxy getByIndex(@Param("index") int index);

    @Select("SELECT * FROM proxy")
    List<Proxy> getAllProxy();

    @Select("SELECT count(id) FROM proxy")
    int count();

    @Delete("DELETE FROM proxy WHERE id = #{id}")
    int delete(@Param("id") int id);

}
