package com.example.reggie.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.pojo.Category;
import com.example.reggie.pojo.Employee;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}