package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper,Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        // DishDto 是一个数据传输对象（Data Transfer Object, DTO），
        // 通常用来封装菜品的完整信息，并在不同层（如服务层和控制器层）之间传递数据。
        this.save(dishDto);



        // 保存菜品口味数据到菜品口味表dish_flavor
        Long dishId = dishDto.getId();//菜品id

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();

//        List<DishFlavor> 类型：
//
//        List<DishFlavor> 表示一个 DishFlavor 对象的列表，通常用来存储与菜品关联的多个口味信息。
//        每个 DishFlavor 对象对应一个口味，包含诸如口味名称（如“辣度”）和具体值（如“中辣”）等字段。
//        getFlavors() 方法：
//
//        dishDto.getFlavors() 是从 dishDto 对象中获取其包含的所有口味信息，这些信息被封装为 DishFlavor 对象，并存储在一个 List<DishFlavor> 中。

        flavors = flavors.stream().map((item) ->{
            item.setDishId(dishId);
            return item;
//            通过 stream().map(...) 操作，为每个口味对象设置 dishId，以便这些口味数据能与菜品正确关联。
//            item.setDishId(dishId); 将 dishId 赋值给每个 DishFlavor 对象的 dishId 属性。

        }).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);

     //   dishFlavorService.saveBatch(flavors); 将所有口味对象批量保存到 dish_flavor 表中。

    }
    // dish_flavor 表中的 dish_id 字段将多条口味记录与一个菜品关联。

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    public DishDto getByIdWithFlavor(Long id) {
        // 实现类在这里开始实现接口DishServiceImpl里的方法
        //查询菜品基本信息，从dish表查询
         Dish dish = this.getById(id);
         DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        // 使用 BeanUtils.copyProperties 方法将 dish 对象中的基础属性拷贝到 dishDto 中，这样可以避免逐个字段手动赋值，简化代码。

//查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, id);

        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
//        通过 dishFlavorService.list(queryWrapper) 查询 dish_flavor 表中的口味信息，返回 DishFlavor 对象的列表。
//        每个 DishFlavor 对象包含一个口味的详细信息
//
        dishDto.setFlavors(flavors);

       // 将查询到的 flavors 列表设置到 dishDto 对象的 flavors 属性中，使其包含完整的口味信息。

        return dishDto;
    }

    @Override
    @Transactional //多表查询添加事务，要么一起提交要么一起失败
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

//        创建一个 LambdaQueryWrapper 对象，用于构建查询条件。
//        queryWrapper.eq(DishFlavor::getDishId, id) 表示查询 dish_flavor 表中，dish_id 等于 id 的所有记录。
        // delete from dish_flavor where dish_id = id

        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来的口味数据---dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        //使用 stream().map() 方法遍历 flavors 列表，为每个口味对象设置 dishId，即当前菜品的 ID。这是为了将这些口味数据与当前菜品关联起来。

        dishFlavorService.saveBatch(flavors);
        // 使用 saveBatch() 方法批量保存新处理过的口味数据到数据库中
    }
}
