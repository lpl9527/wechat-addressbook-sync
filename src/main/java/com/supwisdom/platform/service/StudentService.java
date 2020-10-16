package com.supwisdom.platform.service;


import com.supwisdom.platform.model.Student;

import java.util.List;

public interface StudentService {

    /**
     * 查询所有用户
     */
    List<Student> findAll();
}
