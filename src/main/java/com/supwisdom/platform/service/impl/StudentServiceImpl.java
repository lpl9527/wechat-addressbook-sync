package com.supwisdom.platform.service.impl;

import com.supwisdom.platform.framework.service.MybatisBaseManager;
import com.supwisdom.platform.model.Student;
import com.supwisdom.platform.service.StudentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl extends MybatisBaseManager<Student> implements StudentService {

    private static final String SQL_ID_FIND_ALL = "findAll";

    /**
     * 查询所有学生
     */
    @Override
    public List<Student> findAll() {
        return this.sqlSessionTemplate.selectList(getSqlName(SQL_ID_FIND_ALL));
    }
}
