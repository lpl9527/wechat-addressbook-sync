package com.supwisdom.platform.service.impl;

import com.supwisdom.platform.framework.service.MybatisBaseManager;
import com.supwisdom.platform.model.Department;
import com.supwisdom.platform.service.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl extends MybatisBaseManager<Department> implements DepartmentService {

    private static final String SQL_ID_FIND_ALL = "findAll";
    /**
     * 查询所有部门
     */
    @Override
    public List<Department> findAll() {
        return this.sqlSessionTemplate.selectList(getSqlName(SQL_ID_FIND_ALL));
    }
}
