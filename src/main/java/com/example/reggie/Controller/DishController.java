package com.example.reggie.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.DTO.DishDto;
import com.example.reggie.Service.CategoryService;
import com.example.reggie.Service.DishFlavorService;
import com.example.reggie.Service.DishService;
import com.example.reggie.Service.impl.DishServiceImpl;
import com.example.reggie.common.Result;
import com.example.reggie.pojo.Category;
import com.example.reggie.pojo.Dish;
import com.example.reggie.pojo.DishFlavor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishservice;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());

        dishservice.saveWithFlavor(dishDto);

        // 清理 后台修改分类 下面的菜品缓存数据

        return Result.success("新增菜品操作成功！");
    }

    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name) {
        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishservice.page(pageInfo, queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            // 每个item表示 一个菜品 dish，根据菜品的分类id 给菜品设置 菜品的分类名
            Long itemCategoryId = item.getCategoryId();
            Category category = categoryService.getById(itemCategoryId);

            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }

            // 当前菜品的id,根据dishId去查询当前菜品对应的口味
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> flavorQueryWrapper = new LambdaQueryWrapper<>();
            flavorQueryWrapper.eq(DishFlavor::getDishId, dishId);


            List<DishFlavor> dishFlavorList = dishFlavorService.list(flavorQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return Result.success(dishDtoPage);
    }

    /**
     * 通过id查询菜品信息和对应的口味信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishservice.getByIdWithFlavor(id);
        return Result.success(dishDto);
    }

    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());

        dishservice.updateWithFlavor(dishDto);

        // 清理 后台修改分类 下面的菜品缓存数据

        return Result.success("新增菜品操作成功！");
    }
//    @GetMapping("/list")
//    public Result<List<Dish>> list(Dish dish){
//        //构造查询条件
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId() !=null, Dish::getCategoryId,dish.getCategoryId());
//        //添加条件，1在售，0禁售
//        queryWrapper.eq(Dish::getStatus,1);
//        //添加排序条件
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishservice.list(queryWrapper);
//        return Result.success(list);
//    }

    @GetMapping("/list")
    public Result<List<DishDto>> list(Dish dish) {

        List<DishDto> dishDtoList = null;
        //  根据菜品的分类(湘菜、川菜) 去缓存菜品数据
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();


        if (dishDtoList != null) {
            return Result.success(dishDtoList);
        }

        // dishDtoList == null,即Redis中没有 对应的菜品数据，需要去查询数据库
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        Long categoryId = dish.getCategoryId();
        queryWrapper.eq(categoryId != null, Dish::getCategoryId, categoryId);

        // status 为 1: 还在售卖的菜品
        queryWrapper.eq(Dish::getStatus, 1);
        // 根据sort 属性升序片排列
        queryWrapper.orderByDesc(Dish::getSort);
        List<Dish> list = dishservice.list(queryWrapper);

        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            // 每个item表示 一个菜品 dish，根据菜品的分类id 给菜品设置 菜品的分类名
            Long itemCategoryId = item.getCategoryId();
            Category category = categoryService.getById(itemCategoryId);

            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }

            // 当前菜品的id,根据dishId去查询当前菜品对应的口味
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> flavorQueryWrapper = new LambdaQueryWrapper<>();
            flavorQueryWrapper.eq(DishFlavor::getDishId, dishId);


            List<DishFlavor> dishFlavorList = dishFlavorService.list(flavorQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;

        }).collect(Collectors.toList());


        return Result.success(dishDtoList);
    }
}