package com.example.reggie.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.DTO.SetmealDto;
import com.example.reggie.Service.SetmealDishService;
import com.example.reggie.Service.SetmealService;
import com.example.reggie.common.CustomException;
import com.example.reggie.mapper.SetmealMapper;
import com.example.reggie.pojo.Setmeal;
import com.example.reggie.pojo.SetmealDish;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
        implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐 和菜品的关联关系
     */
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐信息，setmeal 执行insert操作
        this.save(setmealDto);// setmealService.save(setmealDto);

        // 在浏览器的控制台 可以看出，SetmealDish对象没有setmealId,需要通过setmealDto 来获取setmealId
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关联信息，操作setmeal_dish 执行insert操作
        setmealDishService.saveBatch(setmealDishes);

    }
    /**
     * 删除套餐和菜品的关联数据
     * @param ids
     */
    @Override
    public void removeWithDish(List<Long> ids){
//        select count(*) from setmeal where id in(1,2,3) and status = 1
        //查询套餐状态，确定能否可用删除

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        //如果不能删除，抛出一个业务异常
        int count = this.count(queryWrapper);
        if(count > 0){
            //如果不能删除，抛出异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }


        //如果可以删除，先删除套餐中的数据--setmeal
        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        //删除关系表中的数据--setmeal_dish
        setmealDishService.remove(lambdaQueryWrapper);
        //setmealDishService.re
    }
}