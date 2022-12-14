package com.example.reggie.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.DTO.SetmealDto;
import com.example.reggie.pojo.Setmeal;

import java.util.List;



public interface SetmealService extends IService<Setmeal> {

    // 新增套餐， 同时需要 保存套餐和菜品的关联 关系
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐和菜品的关联数据
     * @param ids
     */
    public void removeWithDish(List<Long> ids);
}