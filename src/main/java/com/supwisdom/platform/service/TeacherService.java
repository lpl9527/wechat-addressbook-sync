package com.supwisdom.platform.service;

import com.supwisdom.platform.model.Teacher;

import java.util.List;

public interface TeacherService {

    /**
     * 查询所有教职工
     */
    List<Teacher> findAll();
}
