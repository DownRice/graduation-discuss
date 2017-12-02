package cn.downrice.graduation_discuss.dao;

import cn.downrice.graduation_discuss.model.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDAO {
    String TABLE_NAME = " user ";
    String INSERT_FIELDS = " name, password, email, sex, salt, head_url ";
    String SELECT_FILEDS = " id," + INSERT_FIELDS;

    @Insert({"INSERT INTO ", TABLE_NAME,  "( ", INSERT_FIELDS, ") VALUES ( #{name}, #{password}, #{email}, #{sex}, #{salt}, #{headUrl})"})
    int insertUser(User user);

    @Select({"SELECT ", SELECT_FILEDS, " FROM ", TABLE_NAME, " WHERE id=#{id}"})
    User selectUserById(int id);

    @Delete({"DELETE FROM ", TABLE_NAME, " WHERE od=#{id}"})
    int deletUserById(int id);

    @Select({"SELECT ", SELECT_FILEDS, " FROM ", TABLE_NAME, " WHERE name=#{name}"})
    User selectUserByName(String name);



}
