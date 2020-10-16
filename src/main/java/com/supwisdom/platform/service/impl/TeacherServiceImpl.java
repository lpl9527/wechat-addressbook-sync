package com.supwisdom.platform.service.impl;

import com.supwisdom.platform.framework.service.MybatisBaseManager;
import com.supwisdom.platform.model.Teacher;
import com.supwisdom.platform.service.TeacherService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class TeacherServiceImpl extends MybatisBaseManager<Teacher> implements TeacherService {

    private static final String SQL_ID_FIND_ALL = "findAll";

    /**
     * 查询所有教职工
     */
    @Override
    public List<Teacher> findAll() {
        return this.sqlSessionTemplate.selectList(getSqlName(SQL_ID_FIND_ALL));
    }
}
