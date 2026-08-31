package com.marvel.module.system.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.marvel.module.system.entity.SysDictData;
import com.marvel.module.system.entity.SysDictType;

import java.util.List;

/**
 * 字典管理服务：字典类型与字典数据的维护，以及供业务侧读取的启用数据查询。
 */
public interface SysDictService extends IService<SysDictType> {

    List<SysDictType> listTypes(String dictName, String dictType, String status);

    void createType(SysDictType dictType);

    void updateType(SysDictType dictType);

    void deleteType(Long dictId);

    List<SysDictData> listData(String dictType, String keyword);

    void createData(SysDictData dictData);

    void updateData(SysDictData dictData);

    void deleteData(List<Long> dictCodes);

    /** 供业务/前端下拉使用的启用状态字典数据（按 orderNum 排序） */
    List<SysDictData> listEnabledData(String dictType);
}
