package com.tencent.wxcloudrun.dao.mapper;

import com.tencent.wxcloudrun.dao.dataobject.AccessDO;
import java.util.ArrayList;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhangyichuan
 */
@Mapper
public interface AccessMapper {

  void insertAccess(AccessDO access);

  ArrayList<String> getCityList();
}
