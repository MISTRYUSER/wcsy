package com.example.reggie.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.DTO.DishDto;
import com.example.reggie.Service.CategoryService;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.Result;
import com.example.reggie.pojo.Category;
import com.example.reggie.pojo.DishFlavor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // @RequestBody: 将前端回传的JSON数据需要使用@RequestBody 转化为 实体对象
    @PostMapping
    public Result<String> save(@RequestBody Category category) {
        log.info("category:{}", category);

        categoryService.save(category);
        return Result.success("成功新增分类！");
    }
    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize){
        Page<Category> pageInfo = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件，根据sort排序
        queryWrapper.orderByAsc(Category::getSort);

        //进行分页查询
        categoryService.page(pageInfo, queryWrapper);
        return Result.success(pageInfo);
    }
    @DeleteMapping
    public Result<String> delete(Long ids){
        log.info("删除分类，分类id为: {}",ids);

//        categoryService.removeById(id);
        categoryService.remove(ids);

        return Result.success("成功删除分类信息！");
    }
    @PutMapping
    public Result<String> update(@RequestBody Category category){
        log.info("修改分类信息{}",category);
        categoryService.updateById(category);
        return Result.success("修改分类信成功");
    }

    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public Result<List<Category>> List(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return Result.success(list);
    }

 }