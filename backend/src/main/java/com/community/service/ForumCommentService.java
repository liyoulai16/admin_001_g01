package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.entity.ForumComment;
import com.community.entity.ForumCommentLike;
import com.community.entity.ForumPost;
import com.community.entity.ForumPostLike;
import com.community.mapper.ForumCommentLikeMapper;
import com.community.mapper.ForumCommentMapper;
import com.community.mapper.ForumPostLikeMapper;
import com.community.mapper.ForumPostMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ForumCommentService {
    
    @Resource
    private ForumCommentMapper commentMapper;
    
    @Resource
    private ForumCommentLikeMapper commentLikeMapper;
    
    @Resource
    private ForumPostMapper postMapper;
    
    @Resource
    private ForumPostLikeMapper postLikeMapper;
    
    public List<ForumComment> getCommentsByPostId(Long postId, Long userId) {
        List<ForumComment> rootComments = commentMapper.selectRootCommentsByPostId(postId);
        
        for (ForumComment comment : rootComments) {
            enrichComment(comment, userId);
            List<ForumComment> replies = commentMapper.selectRepliesByParentId(comment.getId());
            for (ForumComment reply : replies) {
                enrichComment(reply, userId);
            }
            comment.setReplies(replies);
        }
        
        return rootComments;
    }
    
    private void enrichComment(ForumComment comment, Long userId) {
        if (comment == null) return;
        
        comment.setUserName(StringUtils.hasText(comment.getUserName()) ? comment.getUserName() : "匿名用户");
        comment.setUserAvatar("👤");
        
        if (comment.getLikes() == null) comment.setLikes(0);
        
        if (userId != null) {
            LambdaQueryWrapper<ForumCommentLike> likeWrapper = new LambdaQueryWrapper<>();
            likeWrapper.eq(ForumCommentLike::getCommentId, comment.getId())
                       .eq(ForumCommentLike::getUserId, userId);
            ForumCommentLike like = commentLikeMapper.selectOne(likeWrapper);
            comment.setIsLiked(like != null);
        } else {
            comment.setIsLiked(false);
        }
    }
    
    @Transactional
    public Map<String, Object> createComment(Long postId, Long userId, String content) {
        Map<String, Object> result = new HashMap<>();
        
        if (!StringUtils.hasText(content)) {
            result.put("success", false);
            result.put("message", "请输入评论内容");
            return result;
        }
        
        ForumPost post = postMapper.selectById(postId);
        if (post == null) {
            result.put("success", false);
            result.put("message", "帖子不存在");
            return result;
        }
        
        ForumComment comment = new ForumComment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setParentId(0L);
        comment.setContent(content);
        comment.setLikes(0);
        comment.setStatus(1);
        comment.setDeleted(0);
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        
        commentMapper.insert(comment);
        
        post.setComments((post.getComments() != null ? post.getComments() : 0) + 1);
        postMapper.updateById(post);
        
        result.put("success", true);
        result.put("message", "评论成功");
        result.put("commentId", comment.getId());
        return result;
    }
    
    @Transactional
    public Map<String, Object> createReply(Long postId, Long userId, Long parentId, Long replyToId, Long replyToUserId, String content) {
        Map<String, Object> result = new HashMap<>();
        
        if (!StringUtils.hasText(content)) {
            result.put("success", false);
            result.put("message", "请输入回复内容");
            return result;
        }
        
        ForumPost post = postMapper.selectById(postId);
        if (post == null) {
            result.put("success", false);
            result.put("message", "帖子不存在");
            return result;
        }
        
        ForumComment parentComment = commentMapper.selectById(parentId);
        if (parentComment == null) {
            result.put("success", false);
            result.put("message", "被回复的评论不存在");
            return result;
        }
        
        ForumComment reply = new ForumComment();
        reply.setPostId(postId);
        reply.setUserId(userId);
        reply.setParentId(parentId);
        reply.setReplyToId(replyToId != null ? replyToId : parentId);
        reply.setReplyToUserId(replyToUserId);
        reply.setContent(content);
        reply.setLikes(0);
        reply.setStatus(1);
        reply.setDeleted(0);
        reply.setCreateTime(LocalDateTime.now());
        reply.setUpdateTime(LocalDateTime.now());
        
        commentMapper.insert(reply);
        
        post.setComments((post.getComments() != null ? post.getComments() : 0) + 1);
        postMapper.updateById(post);
        
        result.put("success", true);
        result.put("message", "回复成功");
        result.put("commentId", reply.getId());
        return result;
    }
    
    @Transactional
    public Map<String, Object> likeComment(Long commentId, Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        if (userId == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        
        ForumComment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            result.put("success", false);
            result.put("message", "评论不存在");
            return result;
        }
        
        LambdaQueryWrapper<ForumCommentLike> likeWrapper = new LambdaQueryWrapper<>();
        likeWrapper.eq(ForumCommentLike::getCommentId, commentId)
                   .eq(ForumCommentLike::getUserId, userId);
        ForumCommentLike existingLike = commentLikeMapper.selectOne(likeWrapper);
        
        if (existingLike != null) {
            commentLikeMapper.deleteById(existingLike.getId());
            comment.setLikes(Math.max(0, (comment.getLikes() != null ? comment.getLikes() : 0) - 1));
            result.put("isLiked", false);
            result.put("message", "取消点赞成功");
        } else {
            ForumCommentLike like = new ForumCommentLike();
            like.setCommentId(commentId);
            like.setUserId(userId);
            like.setCreateTime(LocalDateTime.now());
            commentLikeMapper.insert(like);
            comment.setLikes((comment.getLikes() != null ? comment.getLikes() : 0) + 1);
            result.put("isLiked", true);
            result.put("message", "点赞成功");
        }
        
        commentMapper.updateById(comment);
        
        result.put("success", true);
        result.put("likes", comment.getLikes());
        return result;
    }
    
    @Transactional
    public Map<String, Object> likePost(Long postId, Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        if (userId == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        
        ForumPost post = postMapper.selectById(postId);
        if (post == null) {
            result.put("success", false);
            result.put("message", "帖子不存在");
            return result;
        }
        
        LambdaQueryWrapper<ForumPostLike> likeWrapper = new LambdaQueryWrapper<>();
        likeWrapper.eq(ForumPostLike::getPostId, postId)
                   .eq(ForumPostLike::getUserId, userId);
        ForumPostLike existingLike = postLikeMapper.selectOne(likeWrapper);
        
        if (existingLike != null) {
            postLikeMapper.deleteById(existingLike.getId());
            post.setLikes(Math.max(0, (post.getLikes() != null ? post.getLikes() : 0) - 1));
            result.put("isLiked", false);
            result.put("message", "取消点赞成功");
        } else {
            ForumPostLike like = new ForumPostLike();
            like.setPostId(postId);
            like.setUserId(userId);
            like.setCreateTime(LocalDateTime.now());
            postLikeMapper.insert(like);
            post.setLikes((post.getLikes() != null ? post.getLikes() : 0) + 1);
            result.put("isLiked", true);
            result.put("message", "点赞成功");
        }
        
        postMapper.updateById(post);
        
        result.put("success", true);
        result.put("likes", post.getLikes());
        return result;
    }
    
    public boolean isPostLikedByUser(Long postId, Long userId) {
        if (userId == null) return false;
        LambdaQueryWrapper<ForumPostLike> likeWrapper = new LambdaQueryWrapper<>();
        likeWrapper.eq(ForumPostLike::getPostId, postId)
                   .eq(ForumPostLike::getUserId, userId);
        return postLikeMapper.selectOne(likeWrapper) != null;
    }
    
    public List<Map<String, Object>> getHotPosts() {
        LambdaQueryWrapper<ForumPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(ForumPost::getId, ForumPost::getTitle, ForumPost::getTag, ForumPost::getViews, ForumPost::getLikes)
               .eq(ForumPost::getStatus, 1)
               .eq(ForumPost::getDeleted, 0)
               .and(w -> w.eq(ForumPost::getTag, 1).or().eq(ForumPost::getTag, 3))
               .orderByDesc(ForumPost::getViews)
               .orderByDesc(ForumPost::getLikes)
               .last("LIMIT 10");
        
        List<ForumPost> posts = postMapper.selectList(wrapper);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (ForumPost post : posts) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", post.getId());
            item.put("title", post.getTitle());
            item.put("tag", post.getTag());
            item.put("views", post.getViews());
            item.put("likes", post.getLikes());
            result.add(item);
        }
        
        return result;
    }
}
