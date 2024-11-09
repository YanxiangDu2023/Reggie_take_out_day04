package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishFlavorMapper;
import com.itheima.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
//@Service：
//这是一个 Spring 的注解，用来标识这个类是一个服务组件（Service），这样 Spring 容器可以将其自动识别并管理，方便依赖注入。
// 即，这个类可以在其他类中通过 @Autowired 注解自动注入使用

// 这些都是实现类
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper,DishFlavor> implements DishFlavorService {

//    public class DishFlavorServiceImpl：
//    这是一个类定义，DishFlavorServiceImpl 是具体实现业务逻辑的类，用于处理菜品口味相关的操作。
//
//            extends ServiceImpl<DishFlavorMapper, DishFlavor>：
//    ServiceImpl 是 MyBatis-Plus 提供的一个通用服务实现类。通过继承 ServiceImpl，可以直接使用它的基础 CRUD 操作方法，避免编写重复的增删改查代码。
//    ServiceImpl 类接受两个泛型参数：
//
//    DishFlavorMapper：这是一个 MyBatis-Plus 的 Mapper 接口，用于数据库操作。DishFlavorMapper 中定义了 DishFlavor 实体类对应的数据表的操作方法。
//    DishFlavor：这是一个实体类，表示数据库中的某个表（菜品口味表），封装了表中的各个字段。


//    implements DishFlavorService：
//    这个类实现了 DishFlavorService 接口，通常用于定义菜品口味模块特有的业务逻辑方法。如果 DishFlavorService 中定义了一些自定义业务方法，
//    那么 DishFlavorServiceImpl 可以实现这些方法。


}
