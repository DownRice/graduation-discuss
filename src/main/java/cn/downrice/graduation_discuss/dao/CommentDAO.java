package cn.downrice.graduation_discuss.dao;

import cn.downrice.graduation_discuss.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentDAO {
    String TABLE_NAME = "comment";
    String INSERT_FIELDS = " user_id, entity_id, entity_type, content, created_date, status ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"INSERT INTO ", TABLE_NAME, "(", INSERT_FIELDS, ") VALUES (#{userId}, #{entityId}, #{entityType}, #{content}, #{createdDate}, #{status})"})
    int insertComment(Comment comment);

    @Select({"SELECT ", SELECT_FIELDS, " FROM ", TABLE_NAME, " WHERE entity_id=#{entityId} AND entity_type=#{entityType}"})
    List<Comment> selectByEntity(@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Update({"UPDATE ", TABLE_NAME, " SET status=#{status} WHERE entity_id=#{entityId} AND entity_type=#{entityType} AND user_id=#{userId}"})
    void updateStatus(@Param("entityId") int entityId, @Param("entityType") int entityType, @Param("userId") int userId, @Param("status") int status);

    @Update({"UPDATE ", TABLE_NAME, " SET status=#{status} WHERE entity_id=#{entityId} AND entity_type=#{entityType}"})
    void updateAllStatus(@Param("entityId") int entityId, @Param("entityType") int entityType, @Param("status") int status);

    /**
     * 获取评论总数
     * @param entityId
     * @param entityType
     * @return
     */
    @Select({"select count(id) from ", TABLE_NAME, " where entity_id=#{entityId} and entity_type=#{entityType} "})
    int getCommentCount(@Param("entityId") int entityId, @Param("entityType") int entityType);

}
