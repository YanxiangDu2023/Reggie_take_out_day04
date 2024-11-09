package com.itheima.reggie.dto;

import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {
    // 继承了Dish

    private List<DishFlavor> flavors = new ArrayList<>();
    // 接收页面传过来的
    //    flavors [{name: "甜味", value: "["无糖","半糖","多糖","全糖"]", showOption: false},…]

    private String categoryName;

    private Integer copies;
}
