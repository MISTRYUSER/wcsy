package com.example.reggie.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.DTO.DishDto;
import com.example.reggie.Service.DishFlavorService;
import com.example.reggie.Service.DishService;
import com.example.reggie.mapper.DishMapper;
import com.example.reggie.pojo.Dish;
import com.example.reggie.pojo.DishFlavor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;

    // 由于涉及到对dish、dish_flavor两张表的操作，应该使用 @Transactional 来标注事务
    @Transactional  //  让@Transactional 生效，还需要在启动类添加@EnableTransactionManagement 来开启事务
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);
        log.info("this = " + this);

        Long dishId = dishDto.getId(); //  获取前端传过来的 dishId

        // 通过Debug的方式，发现前端传过来的 flavors 并不包含 dishId,故dish需要另外赋值
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((flavor) -> {  //  flavor 为遍历出来的 每个DishFlavor对象
            flavor.setDishId(dishId);
            return flavor;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     */
    public DishDto getByIdWithFlavor(Long id){
        //查询菜品基本信息从dish查询
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //查询当前菜品对应 的口味信息，从dish_flavor
        LambdaQueryWrapper<DishFlavor> queryWrapper = new  LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表信息
        this.updateById(dishDto);
        //清理当前菜品对应口味数据--dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //添加当前提交过来的口味信息--dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
}