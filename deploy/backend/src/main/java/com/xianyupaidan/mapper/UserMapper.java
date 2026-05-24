package com.xianyupaidan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xianyupaidan.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
