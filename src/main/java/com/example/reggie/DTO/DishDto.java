package com.example.reggie.DTO;

import com.example.reggie.pojo.Dish;
import com.example.reggie.pojo.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {
    //  Dish 不符合前端传过来的数据,需要将其转化为DishDto
    // flavors: 菜品对应的口味数据
    private List<DishFlavor> flavors = new ArrayList<>();

    private String CategoryName;

    private Integer copies;

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }
}