package cn.downrice.graduation_discuss.service;

import cn.downrice.graduation_discuss.dao.CommentDAO;
import cn.downrice.graduation_discuss.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentDAO commentDAO;

    @Autowired
    private SensitiveService sensitiveService;

    public List<Comment> getCommentsByEntity(int entityId, int entityType) {
        return commentDAO.selectByEntity(entityId, entityType);
    }

    public int addComment(Comment comment){
        comment.setContent(sensitiveService.filter(comment.getContent()));
        return commentDAO.insertComment(comment);
    }

    public void deleteComment(int entityId, int entityType, int userId){
        commentDAO.updateStatus(entityId, entityType, userId, -1);
    }

    public void deleteAllComment(int entityId, int entityType){
        commentDAO.updateAllStatus(entityId, entityType, -1);
    }

    public Comment getCommentById(int id) {
        return commentDAO.getCommentById(id);
    }

}
