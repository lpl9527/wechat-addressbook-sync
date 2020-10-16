package com.supwisdom.platform.service;

import com.supwisdom.platform.model.Department;

import java.util.List;

/**
 * 部门接口
 */
public interface DepartmentService {

    /**
     * 查询所有部门
     */
    List<Department> findAll();
}
