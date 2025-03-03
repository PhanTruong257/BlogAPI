package study.blogapi.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import study.blogapi.model.Post;
import study.blogapi.payload.ApiResponse;
import study.blogapi.payload.PagedResponse;
import study.blogapi.payload.PostRequest;
import study.blogapi.payload.PostResponse;
import study.blogapi.security.CurrentUser;
import study.blogapi.security.UserPrincipal;
import study.blogapi.service.PostService;
import study.blogapi.utils.AppConstants;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<PagedResponse<Post>> getAllPosts(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        PagedResponse<Post> response = postService.getAllPosts(page, size);

        return new ResponseEntity< >(response, HttpStatus.OK);
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<PagedResponse<Post>> getPostsByCategory(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size,
            @PathVariable(name = "id") Long id) {
        PagedResponse<Post> response = postService.getPostsByCategory(id, page, size);

        return new ResponseEntity< >(response, HttpStatus.OK);
    }

    @GetMapping("/tag/{id}")
    public ResponseEntity<PagedResponse<Post>> getPostsByTag(
            @RequestParam(value = "page", required = false, defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
            @RequestParam(value = "size", required = false, defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size,
            @PathVariable(name = "id") Long id) {
        PagedResponse<Post> response = postService.getPostsByTag(id, page, size);

        return new ResponseEntity< >(response, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PostResponse> addPost(@Valid @RequestBody PostRequest postRequest,
                                                @CurrentUser UserPrincipal currentUser) {
        PostResponse postResponse = postService.addPost(postRequest, currentUser);

        return new ResponseEntity< >(postResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable(name = "id") Long id) {
        Post post = postService.getPost(id);

        return new ResponseEntity< >(post, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Post> updatePost(@PathVariable(name = "id") Long id,
                                           @Valid @RequestBody PostRequest newPostRequest, @CurrentUser UserPrincipal currentUser) {
        Post post = postService.updatePost(id, newPostRequest, currentUser);

        return new ResponseEntity< >(post, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deletePost(@PathVariable(name = "id") Long id, @CurrentUser UserPrincipal currentUser) {
        ApiResponse apiResponse = postService.deletePost(id, currentUser);

        return new ResponseEntity< >(apiResponse, HttpStatus.OK);
    }
}