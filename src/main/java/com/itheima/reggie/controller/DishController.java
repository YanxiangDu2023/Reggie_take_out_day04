package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    // 前面 /dish已经加过了
    public R<String> save(@RequestBody DishDto dishDto){
        // RequestBody处理json数据，一定要加
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
//    public R<Page> page(int page, int pageSize, String name){
//        Page<Dish> pageInfo = new Page<>(page, pageSize);
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.like(name != null,Dish::getName,name);
//        queryWrapper.orderByDesc(Dish::getUpdateTime);
//        dishService.page(pageInfo, queryWrapper);
//
//
//
//
//    }

    public R<Page> page(int page,int pageSize,String name){


        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);

        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo,queryWrapper);

        // 如果没有Page<DishDto> dishDtoPage = new Page<>(); 这里只返回了categoryID，前端prop="categoryName"找不到categoryName

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        // 把records忽略到
        // BeanUtils.copyProperties 是 Spring 提供的一个工具方法，用于复制 Java Bean 中的属性。
        //这行代码的作用是：将 pageInfo 中的属性（如总记录数、总页数等）复制到 dishDtoPage 中，但不包括 records 字段（因为 records 是菜品的记录列表，需要单独处理）。

        List<Dish> records = pageInfo.getRecords();
//        获取查询结果中的所有 Dish 实体记录。


        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            // 使用 Java 流式操作对每一条 Dish 记录进行转换，转换成 DishDto 对象，加入到新的列表 list 中。

            BeanUtils.copyProperties(item,dishDto);
            // 将 Dish 对象的数据拷贝到 DishDto 对象中。

            Long categoryId = item.getCategoryId();//分类id

            // 获取菜品的分类 ID：Long categoryId = item.getCategoryId();

            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

//        records.stream().map((item) -> {...})：将 records 列表转换成流，map 操作会对每一条 Dish 记录执行传入的 Lambda 表达式。
//        BeanUtils.copyProperties(item, dishDto)：将 Dish 对象的属性（如 name、price 等）复制到 DishDto 对象中。
//        Long categoryId = item.getCategoryId();：获取当前菜品的分类 ID。
//        Category category = categoryService.getById(categoryId);：根据分类 ID 查询对应的 Category 对象。
//        如果 category 对象不为 null，就将 categoryName 赋值给 dishDto 的 categoryName 字段。否则，categoryName 字段为空。
//        dishDto 被转换后返回，最终通过 collect(Collectors.toList()) 将所有的 DishDto 对象收集到一个新的列表 list 中。


        dishDtoPage.setRecords(list);

        // 将转换后的 DishDto 列表设置到 dishDtoPage 的 records 字段。此时，dishDtoPage 包含了分页信息和每一条菜品的 DishDto 记录。



        return R.success(dishDtoPage);
    }

//    R.success(dishDtoPage) 是一个返回成功结果的封装，R 是一个工具类，用于统一包装返回的结果（如成功、失败等）。
//    这行代码将 dishDtoPage（包含了分页数据和菜品信息）作为成功响应返回给前端。

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    // get 方法是一个 RESTful API 的 GET 请求，用于通过 id 获取特定菜品的信息并返回给前端
    public R<DishDto> get(@PathVariable Long id){
        // 回现口味，DishDto继承了Dish，还有扩展
        // @PathVariable 把 URL 中的 id 部分映射到方法参数 id 上

        DishDto dishDto = dishService.getByIdWithFlavor(id);
        // 在controller调用dishService的方法,dishService接口的具体实现在DishServiceImpl


        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    // 保存菜品的时候发送的是put请求
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.updateWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }
}
