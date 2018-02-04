package cn.downrice.graduation_discuss.dao;

import cn.downrice.graduation_discuss.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserDAO {
    String TABLE_NAME = " user ";
    String INSERT_FIELDS = " name, password, email, sex, salt, head_url, state ";
    String SELECT_FILEDS = " id," + INSERT_FIELDS;

    @Insert({"INSERT INTO ", TABLE_NAME,  "( ", INSERT_FIELDS, ") VALUES ( #{name}, #{password}, #{email}, #{sex}, #{salt}, #{headUrl}, #{state})"})
    int insertUser(User user);

    @Select({"SELECT ", SELECT_FILEDS, " FROM ", TABLE_NAME, " WHERE id=#{id}"})
    User selectUserById(int id);

    @Delete({"DELETE FROM ", TABLE_NAME, " WHERE od=#{id}"})
    int deletUserById(int id);

    @Select({"SELECT ", SELECT_FILEDS, " FROM ", TABLE_NAME, " WHERE name=#{name}"})
    User selectUserByName(String name);

    @Select({"SELECT ", SELECT_FILEDS, " FROM ", TABLE_NAME, " WHERE email=#{email}"})
    User selectUserByEmail(String email);

    @Update({"UPDATE ", TABLE_NAME, " SET state=#{state} WHERE email=#{email}"})
    int updateUserStateByEmail(int state, String email);

    @Update({"UPDATE ", TABLE_NAME, " SET name=#{name}, password=#{password}, email=#{email}, sex=#{sex}, salt=#{salt}, head_url=#{headUrl}, state=#{state} WHERE id=#{id}"})
    int updateUser(User user);
}
