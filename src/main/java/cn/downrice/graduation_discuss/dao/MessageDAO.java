package cn.downrice.graduation_discuss.dao;

import cn.downrice.graduation_discuss.model.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageDAO {
    String TABLE_NAME = " message ";
    String INSERT_FIELDS = " from_id, to_id, content, has_read, conversation_id, created_date ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"INSERT INTO ", TABLE_NAME, "(", INSERT_FIELDS, ") VALUES (#{fromId},#{toId},#{content},#{hasRead},#{conversationId},#{createdDate})"})
    int addMessage(Message message);

    @Select({"SELECT ", SELECT_FIELDS, " FROM ", TABLE_NAME, " WHERE conversation_id=#{conversationId} ORDER BY id DESC LIMIT #{offset}, #{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId, @Param("offset") int offset, @Param("limit") int limit);

    @Select({"select count(id) from ", TABLE_NAME, " where has_read=0 and to_id=#{userId} and conversation_id=#{conversationId}"})
    int getConvesationUnreadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

    /**
     * conversationl列表，根据conversationId分组（user1Id_user2Id表示1和2之间的对话）
     * 对话总数记为id是因为mybatis SQL与对象映射
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    @Select({"SELECT ", INSERT_FIELDS, " ,COUNT(id) AS id FROM " +
            "( SELECT * FROM ", TABLE_NAME, " WHERE from_id=#{userId} OR to_id=#{userId} ORDER BY id DESC) tt " +
            "GROUP BY conversation_id ORDER BY created_date DESC LIMIT #{offset}, #{limit}"})
    List<Message> getConversationList(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);

}
