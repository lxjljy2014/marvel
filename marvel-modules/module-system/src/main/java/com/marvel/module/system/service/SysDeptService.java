package com.marvel.module.system.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.marvel.module.system.entity.SysDept;

import java.util.List;

/** 部门管理服务接口。 */
public interface SysDeptService extends IService<SysDept> {

    List<SysDept> listDeptTree(String deptName, String status);

    void createDept(SysDept dept);

    void updateDept(SysDept dept);

    void deleteDept(Long deptId);
}
