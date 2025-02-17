package study.blogapi.service;

import study.blogapi.model.Comment;
import study.blogapi.payload.ApiResponse;
import study.blogapi.payload.CommentRequest;
import study.blogapi.payload.PagedResponse;
import study.blogapi.security.UserPrincipal;

public interface CommentService {

	PagedResponse<Comment> getAllComments(Long postId, int page, int size);

	Comment addComment(CommentRequest commentRequest, Long postId, UserPrincipal currentUser);

	Comment getComment(Long postId, Long id);

	Comment updateComment(Long postId, Long id, CommentRequest commentRequest, UserPrincipal currentUser);

	ApiResponse deleteComment(Long postId, Long id, UserPrincipal currentUser);

}
